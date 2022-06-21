package com.example.demo.SystemTest;

import com.example.demo.DemoApplication;
import com.example.demo.model.Good;
import com.example.demo.model.User;
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
     * 买家登录——卖家登录——卖家发布——审核待整改——修改商品——商品上架
     * ——浏览商品——创建订单——支付订单——卖家发货
     * ——提交退款——卖家驳回——买家取消——再次提交——卖家驳回
     * ——申请仲裁——仲裁驳回——确认收货
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
        List<Map<String,Object>> histories = (List<Map<String,Object>>)historyService.getHistory(user_id).getObject();
        for(Map<String,Object> history : histories){
            if(history.get("good_id").toString().equals(good_id)){
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

    /**
     * 买家登录——卖家登录——卖家发布——审核待整改——修改商品——商品上架
     * ——浏览商品——创建订单——支付订单——卖家发货
     * ——提交退款——卖家驳回——买家取消——再次提交——卖家批准
     */
    @Transactional
    @Test
    public void Procedure2(){
        //买家登录
        String user_id = "hth";
        String pwd = "123456";
        Assert.assertEquals(200,userService.checkPasswordById(user_id,pwd).getCode());
        User user = (User) userService.getById(user_id).getObject();
        Double balance_0 = user.getBalance();

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
        Double good_price = (Double) good.get("price");
        Double freight = (Double) good.get("freight");
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
        List<Map<String,Object>> histories = (List<Map<String,Object>>)historyService.getHistory(user_id).getObject();
        for(Map<String,Object> history : histories){
            if(history.get("good_id").toString().equals(good_id)){
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
        Map<String,Object> order =(Map<String,Object>) orderService.getOrderInfo(order_id).getObject();
        Double order_price = (Double) order.get("price");
        Assert.assertTrue((good_price*num+freight)==order_price);

        //支付订单
        Assert.assertEquals(200,orderService.payOrder(order_id).getCode());
        user = (User) userService.getById(user_id).getObject();
        Double balance_1 = user.getBalance();
        Assert.assertTrue((balance_0-order_price)==balance_1);

        //发货收货——一系列退款流程——已退款
        Assert.assertEquals(200,orderService.sendPackage(order_id).getCode());
        Assert.assertEquals(200,refundService.submitRefund(order_id,"仅供测试").getCode());
        Assert.assertEquals(200,refundService.refuseRefund(order_id).getCode());
        Assert.assertEquals(200,refundService.cancelRefund(order_id).getCode());
        Assert.assertEquals(200,refundService.submitRefund(order_id,"仅供测试").getCode());
        Assert.assertEquals(200,refundService.permitRefund(order_id).getCode());
        order =(Map<String,Object>) orderService.getOrderInfo(order_id).getObject();
        String order_state_2 = order.get("order_state").toString();
        String isRefunding = order.get("isRefunding").toString();
        user = (User) userService.getById(user_id).getObject();
        Double balance_2 = user.getBalance();
        Assert.assertEquals("已退款",order_state_2);
        Assert.assertEquals("n",isRefunding);
        Assert.assertTrue(balance_0.equals(balance_2));
    }

    /**
     * 买家登录——卖家登录——卖家发布——审核待整改——修改商品——商品上架
     * ——浏览商品——创建订单——支付订单——卖家发货
     * ——提交退款——卖家驳回——买家取消——再次提交——卖家驳回
     * ——申请仲裁——仲裁批准
     */
    @Transactional
    @Test
    public void Procedure3(){
        //买家登录
        String user_id = "hth";
        String pwd = "123456";
        Assert.assertEquals(200,userService.checkPasswordById(user_id,pwd).getCode());
        User user = (User) userService.getById(user_id).getObject();
        Double balance_0 = user.getBalance();

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
        Double good_price = (Double) good.get("price");
        Double freight = (Double) good.get("freight");
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
        List<Map<String,Object>> histories = (List<Map<String,Object>>)historyService.getHistory(user_id).getObject();
        for(Map<String,Object> history : histories){
            if(history.get("good_id").toString().equals(good_id)){
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
        Map<String,Object> order =(Map<String,Object>) orderService.getOrderInfo(order_id).getObject();
        Double order_price = (Double) order.get("price");
        Assert.assertTrue((good_price*num+freight)==order_price);

        //支付订单
        Assert.assertEquals(200,orderService.payOrder(order_id).getCode());
        user = (User) userService.getById(user_id).getObject();
        Double balance_1 = user.getBalance();
        Assert.assertTrue((balance_0-order_price)==balance_1);

        //发货收货——一系列退款流程——已退款
        Assert.assertEquals(200,orderService.sendPackage(order_id).getCode());
        Assert.assertEquals(200,refundService.submitRefund(order_id,"仅供测试").getCode());
        Assert.assertEquals(200,refundService.refuseRefund(order_id).getCode());
        Assert.assertEquals(200,refundService.cancelRefund(order_id).getCode());
        Assert.assertEquals(200,refundService.submitRefund(order_id,"仅供测试").getCode());
        Assert.assertEquals(200,refundService.refuseRefund(order_id).getCode());
        Assert.assertEquals(200,refundService.submitArbitration(order_id,"仅供测试",null).getCode());
        Assert.assertEquals(200,refundService.permitArbitration(order_id).getCode());
        order =(Map<String,Object>) orderService.getOrderInfo(order_id).getObject();
        String order_state_2 = order.get("order_state").toString();
        String isRefunding = order.get("isRefunding").toString();
        user = (User) userService.getById(user_id).getObject();
        Double balance_2 = user.getBalance();
        Assert.assertEquals("已退款",order_state_2);
        Assert.assertEquals("n",isRefunding);
        Assert.assertTrue(balance_0.equals(balance_2));
    }

    /**
     * 买家登录——卖家登录——卖家发布——审核待整改——修改商品——商品上架
     * ——浏览商品——创建订单——支付订单——卖家发货
     * ——提交退款——卖家驳回——买家取消——再次提交——卖家驳回——买家取消——确认收货
     */
    @Transactional
    @Test
    public void Procedure4(){
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
        List<Map<String,Object>> histories = (List<Map<String,Object>>)historyService.getHistory(user_id).getObject();
        for(Map<String,Object> history : histories){
            if(history.get("good_id").toString().equals(good_id)){
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
        Assert.assertEquals(200,refundService.cancelRefund(order_id).getCode());
        Assert.assertEquals(200,orderService.ackOrder(order_id).getCode());
        Map<String,Object> order =(Map<String,Object>) orderService.getOrderInfo(order_id).getObject();
        String order_state_1 = order.get("order_state").toString();
        String isRefunding = order.get("isRefunding").toString();
        Assert.assertEquals("已收货",order_state_1);
        Assert.assertEquals("n",isRefunding);
    }

    /**
     * 买家登录——卖家登录——卖家发布——审核待整改——修改商品——商品上架
     * ——浏览商品——创建订单——支付订单——卖家发货——确认收货
     */
    @Transactional
    @Test
    public void Procedure5(){
        //买家登录
        String user_id = "hth";
        String pwd = "123456";
        Assert.assertEquals(200,userService.checkPasswordById(user_id,pwd).getCode());
        User user = (User) userService.getById(user_id).getObject();
        Double balance_0 = user.getBalance();

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
        Double good_price = (Double) good.get("price");
        Double freight = (Double) good.get("freight");
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
        List<Map<String,Object>> histories = (List<Map<String,Object>>)historyService.getHistory(user_id).getObject();
        for(Map<String,Object> history : histories){
            if(history.get("good_id").toString().equals(good_id)){
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
        Map<String,Object> order =(Map<String,Object>) orderService.getOrderInfo(order_id).getObject();
        Double order_price = (Double) order.get("price");
        Assert.assertTrue((good_price*num+freight)==order_price);

        //支付订单
        Assert.assertEquals(200,orderService.payOrder(order_id).getCode());
        user = (User) userService.getById(user_id).getObject();
        Double balance_1 = user.getBalance();
        Assert.assertTrue((balance_0-order_price)==balance_1);

        //发货收货——一系列退款流程——收货
        Assert.assertEquals(200,orderService.sendPackage(order_id).getCode());
        Assert.assertEquals(200,orderService.ackOrder(order_id).getCode());
        order =(Map<String,Object>) orderService.getOrderInfo(order_id).getObject();
        String order_state_1 = order.get("order_state").toString();
        Assert.assertEquals("已收货",order_state_1);
    }

    /**
     * 买家1登录——买家2登录——卖家登录——卖家发布——审核待整改——修改商品——商品上架
     * ——买家1浏览商品——买家2浏览商品——买家1创建订单——买家2创建订单
     * ——买家1支付订单（导致库存不足以满足买家2）——买家2支付失败
     * ——卖家发货——买家1确认收货
     */
    @Transactional
    @Test
    public void Procedure6(){
        //买家1登录
        String user_id_1 = "hth";
        String pwd_1 = "123456";
        Assert.assertEquals(200,userService.checkPasswordById(user_id_1,pwd_1).getCode());
        User user1 = (User) userService.getById(user_id_1).getObject();
        Double balance_1_0 = user1.getBalance();

        //买家2登录
        String user_id_2 = "cyf";
        String pwd_2 = "123456";
        Assert.assertEquals(200,userService.checkPasswordById(user_id_2,pwd_2).getCode());
        User user2 = (User) userService.getById(user_id_2).getObject();
        Double balance_2_0 = user2.getBalance();

        //卖家登录
        String seller_id = "lh";
        String seller_pwd = "123456";
        Assert.assertEquals(200,userService.checkPasswordById(seller_id,seller_pwd).getCode());

        //卖家发布
        String good_id = goodService.releaseGood(seller_id,"仅供测试","服装",1,"仅供测试","上海市",10.0,0.0,null).getObject().toString();
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
        Double good_price = (Double) good.get("price");
        Double freight = (Double) good.get("freight");
        String good_state_2 = good.get("good_state").toString();
        Assert.assertEquals("待审核",good_state_2);
        Assert.assertEquals("仅供测试1",good.get("name").toString());

        //审核商品上架
        goodService.allowGood(good_id,"1");
        good = (Map<String, Object>) goodService.getById(good_id).getObject();
        String good_state_3 = good.get("good_state").toString();
        Assert.assertEquals("上架中",good_state_3);

        //买家1浏览商品
        Instant testTime_1 = Instant.now().plusMillis(TimeUnit.HOURS.toMillis(8));
        goodService.browseGood(user_id_1,good_id).getObject();
        boolean isUpdated_1 = false;
        List<Map<String,Object>> histories_1 = (List<Map<String,Object>>)historyService.getHistory(user_id_1).getObject();
        for(Map<String,Object> history : histories_1){
            if(history.get("good_id").toString().equals(good_id)){
                Instant date = (Instant) history.get("date");
                if(testTime_1.plusMillis(TimeUnit.SECONDS.toMillis(60)).isAfter(date)){
                    isUpdated_1=true;
                    break;
                }
            }else {
                continue;
            }
        }
        Assert.assertTrue(isUpdated_1);

        //买家2浏览商品
        Instant testTime_2 = Instant.now().plusMillis(TimeUnit.HOURS.toMillis(8));
        goodService.browseGood(user_id_2,good_id).getObject();
        boolean isUpdated_2 = false;
        List<Map<String,Object>> histories_2 = (List<Map<String,Object>>)historyService.getHistory(user_id_2).getObject();
        for(Map<String,Object> history : histories_2){
            if(history.get("good_id").toString().equals(good_id)){
                Instant date = (Instant) history.get("date");
                if(testTime_2.plusMillis(TimeUnit.SECONDS.toMillis(60)).isAfter(date)){
                    isUpdated_2=true;
                    break;
                }
            }else {
                continue;
            }
        }
        Assert.assertTrue(isUpdated_2);

        //买家1创建订单
        Integer num =1;
        String order_id_1 = orderService.generateOrder(user_id_1,good_id,"上海市杨浦区",num).getObject().toString();
        Map<String,Object> order1 =(Map<String,Object>) orderService.getOrderInfo(order_id_1).getObject();
        Double order_price_1 = (Double) order1.get("price");
        Assert.assertTrue((good_price*num+freight)==order_price_1);

        //买家2创建订单
        String order_id_2 = orderService.generateOrder(user_id_2,good_id,"上海市杨浦区",num).getObject().toString();
        Map<String,Object> order2 =(Map<String,Object>) orderService.getOrderInfo(order_id_2).getObject();
        Double order_price_2 = (Double) order2.get("price");
        Assert.assertTrue((good_price*num+freight)==order_price_2);

        //买家1支付订单
        Assert.assertEquals(200,orderService.payOrder(order_id_1).getCode());
        user1 = (User) userService.getById(user_id_1).getObject();
        Double balance_1 = user1.getBalance();
        Assert.assertTrue((balance_1_0-order_price_1)==balance_1);

        //买家2支付订单
        Assert.assertEquals(400,orderService.payOrder(order_id_2).getCode());
        user2 = (User) userService.getById(user_id_2).getObject();
        Double balance_2_1 = user2.getBalance();
        String order_state_2 = order2.get("order_state").toString();
        Assert.assertEquals("未支付",order_state_2);
        Assert.assertTrue(balance_2_0.equals(balance_2_1));

        //发货收货——一系列退款流程——收货
        Assert.assertEquals(200,orderService.sendPackage(order_id_1).getCode());
        Assert.assertEquals(200,orderService.ackOrder(order_id_1).getCode());
        order1 =(Map<String,Object>) orderService.getOrderInfo(order_id_1).getObject();
        String order_state_1 = order1.get("order_state").toString();
        Assert.assertEquals("已收货",order_state_1);
    }

}
