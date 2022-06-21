package com.example.demo.SystemTest;

import com.example.demo.DemoApplication;
import com.example.demo.model.Good;
import com.example.demo.service.GoodService;
import com.example.demo.service.OrderService;
import com.example.demo.service.RefundService;
import com.example.demo.service.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

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

    }
}
