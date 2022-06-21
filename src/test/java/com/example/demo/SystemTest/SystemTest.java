package com.example.demo.SystemTest;

import com.example.demo.DemoApplication;
import com.example.demo.model.Good;
import com.example.demo.service.*;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = DemoApplication.class)
public class SystemTest {
    @Autowired
    RefundService refundService;
    @Autowired
    OrderService orderService;
    @Autowired
    UserService userService;
    @Autowired
    GoodService goodService;
    @Autowired
    HistoryService historyService;

    /**
     * 
     */
    @Transactional
    @Test
    public void Procedure1(){
        //买家登录
        String user_id = "hth";
        String pwd = "123456";
        Assert.assertEquals(200,userService.checkPasswordById(user_id,pwd).getCode());

        //卖家登录
        String seller_id = "lh";
        String seller_pwd = "123456";
        Assert.assertEquals(200,userService.checkPasswordById(seller_id,seller_pwd).getCode());

        //卖家发布
        String good_id = goodService.releaseGood(seller_id,"仅供测试","服装",10,"仅供测试","上海市",10.0,0.0,null).getObject().toString();
        Map<String,Object> good = (Map<String, Object>) goodService.getById(good_id).getObject();
        String good_state_0 = good.get("good_state").toString();
        Assert.assertEquals("待审核",good_state_0);

        //商品待整改
        goodService.allowGood(good_id,"2");
        good = (Map<String, Object>) goodService.getById(good_id).getObject();
        String good_state_1 = good.get("good_state").toString();
        Assert.assertEquals("待整改",good_state_1);

        //修改商品
        goodService.setGood(good_id,"仅供测试1",null,-1,null,null,-1.0,-1.0,null);
        good = (Map<String, Object>) goodService.getById(good_id).getObject();
        String good_state_2 = good.get("good_state").toString();
        Assert.assertEquals("待审核",good_state_2);
        Assert.assertEquals("仅供测试1",good.get("name").toString());

        //审核商品上架
        goodService.allowGood(good_id,"1");
        good = (Map<String, Object>) goodService.getById(good_id).getObject();
        String good_state_3 = good.get("good_state").toString();
        Assert.assertEquals("上架中",good_state_3);

        //浏览商品
        Instant testTime = Instant.now().plusMillis(TimeUnit.HOURS.toMillis(8));
        goodService.browseGood(user_id,good_id).getObject();
        boolean isUpdated = false;
        List<Map<String,Object>> histories = (List<Map<String,Object>>)historyService.getHistory("hth").getObject();
        for(Map<String,Object> history : histories){
            if(history.get("good_id").toString().equals("0762113045860999")){
                Instant date = (Instant) history.get("date");
                if(testTime.plusMillis(TimeUnit.SECONDS.toMillis(60)).isAfter(date)){
                    isUpdated=true;
                    break;
                }
            }else {
                continue;
            }
        }
        Assert.assertTrue(isUpdated);

        //创建订单
        Integer num =1;
        String order_id = orderService.generateOrder(user_id,good_id,"上海市杨浦区",num).getObject().toString();

        //支付订单
        Assert.assertEquals(200,orderService.payOrder(order_id).getCode());

        //发货收货——一系列退款流程——收货
        Assert.assertEquals(200,orderService.sendPackage(order_id).getCode());
        Assert.assertEquals(200,refundService.submitRefund(order_id,"仅供测试").getCode());
        Assert.assertEquals(200,refundService.refuseRefund(order_id).getCode());
        Assert.assertEquals(200,refundService.cancelRefund(order_id).getCode());
        Assert.assertEquals(200,refundService.submitRefund(order_id,"仅供测试").getCode());
        Assert.assertEquals(200,refundService.refuseRefund(order_id).getCode());
        Assert.assertEquals(200,refundService.submitArbitration(order_id,"仅供测试",null).getCode());
        Assert.assertEquals(200,refundService.refuseArbitration(order_id).getCode());
        Assert.assertEquals(200,orderService.ackOrder(order_id).getCode());
        Map<String,Object> order =(Map<String,Object>) orderService.getOrderInfo(order_id).getObject();
        String order_state_1 = order.get("order_state").toString();
        String isRefunding = order.get("isRefunding").toString();
        Assert.assertEquals("已收货",order_state_1);
        Assert.assertEquals("n",isRefunding);
    }
}
