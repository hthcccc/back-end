package com.example.demo.service;

import com.example.demo.DemoApplication;
import com.example.demo.model.User;
import io.swagger.models.auth.In;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = DemoApplication.class)
class OrderServiceTest {

    @Autowired
    OrderService orderService;
    @Autowired
    RefundService refundService;
    @Autowired
    UserService userService;
    @Autowired
    GoodService goodService;

    @Test
    void getOrderInfo() {
        //测试订单
        String order_id = "2077079906043517";
        Map<String,Object> order = (Map<String,Object>) orderService.getOrderInfo(order_id).getObject();
        if(order.containsKey("order_id")){
            Assert.assertEquals(order_id,order.get("order_id").toString());
            Assert.assertEquals("hth",order.get("buyer_id").toString());
            Assert.assertEquals("7015091168564543",order.get("good_id").toString());
        }

        //测试不存在的订单
        order_id = "111111111111111";
        order = (Map<String,Object>) orderService.getOrderInfo(order_id).getObject();
        Assert.assertTrue(order==null);
    }

    @Test
    void getRefundState() {
        //测试没申请退款过的订单
        String order_id = "2077079906043517";
        Assert.assertEquals(200,orderService.getRefundState(order_id).getCode());

        //测试退款申诉但是没有进行仲裁的订单
        order_id = "9764349522442306";
        Assert.assertEquals(201,orderService.getRefundState(order_id).getCode());

        //测试进行过仲裁申诉的订单
        order_id = "8672786995889001";
        Assert.assertEquals(202,orderService.getRefundState(order_id).getCode());

    }

    @Transactional
    @Test
    void generateOrder() {
        //测试正常交易
        String order_id = orderService.generateOrder("hth","0840289660372971","上海市杨浦区",2).getObject().toString();
        Map<String,Object> order = (Map<String,Object>)orderService.getOrderInfo(order_id).getObject();
        Instant start_date = (Instant) order.get("start_date");
        System.out.println(start_date);
        Assert.assertEquals("0840289660372971",order.get("good_id").toString());
        Assert.assertTrue(2==(Integer) order.get("num"));
        Assert.assertEquals("未支付",order.get("order_state").toString());
        Assert.assertTrue(start_date.plusMillis(TimeUnit.SECONDS.toMillis(60)).isAfter(Instant.now().plusMillis(TimeUnit.HOURS.toMillis(8))));

        //测试购买自己的商品
        Assert.assertNull(orderService.generateOrder("hth","0727295953745280","上海市杨浦区",2).getObject());

        //测试购买 非上架中 的商品
        Assert.assertNull(orderService.generateOrder("hth","4028936603729721","上海市杨浦区",1).getObject());

        //测试购买超过库存的商品
        Assert.assertNull(orderService.generateOrder("hth","0840289660372971","上海市杨浦区",10001).getObject());
    }

    @Transactional
    @Test
    void payOrder(){
        //测试正常情况
        payOrderNormal();
        //测试已支付的订单
        payOrderAgain();
        //测试库存不足
        payOrderInventoryNotEnough();
        //测试余额不足
        payOrderBalanceNotEnough();
        //测试购买下架商品
        payOrderGoodNotOnShell();
    }

    @Transactional
    @Test
    void payOrderNormal() {
        //测试正常用例
        String order_id = "0277495394409761";
        //获取支付前状态
        Map<String,Object> order =(Map<String,Object>) orderService.getOrderInfo(order_id).getObject();
        User user =(User) userService.getById(order.get("buyer_id").toString()).getObject();
        Map<String,Object> good =(Map<String,Object>) goodService.getById(order.get("good_id").toString()).getObject();
        Double balance_before = user.getBalance();
        Double price = (Double) order.get("price");
        Integer num = (Integer) order.get("num");
        Integer inventory_before = (Integer) good.get("inventory");

        //获取支付后状态
        Assert.assertEquals(200,orderService.payOrder(order_id).getCode());
        order =(Map<String,Object>) orderService.getOrderInfo(order_id).getObject();
        user =(User) userService.getById(order.get("buyer_id").toString()).getObject();
        good =(Map<String,Object>) goodService.getById(order.get("good_id").toString()).getObject();
        Double balance_after = user.getBalance();
        Integer inventory_after = (Integer) good.get("inventory");
        System.out.println(inventory_before-num);
        System.out.println(inventory_after);
        Assert.assertEquals("待发货",order.get("order_state").toString());
        Assert.assertTrue( (balance_before-price)==balance_after);
        Assert.assertTrue((inventory_before-num)==inventory_after);
    }

    @Transactional
    @Test
    void payOrderAgain(){
        //测试已经支付的订单
        String order_id = "2077079906043517";

        //获取支付前状态
        Map<String,Object> order =(Map<String,Object>) orderService.getOrderInfo(order_id).getObject();
        User user =(User) userService.getById(order.get("buyer_id").toString()).getObject();
        Map<String,Object> good =(Map<String,Object>) goodService.getById(order.get("good_id").toString()).getObject();
        Double balance_before = user.getBalance();
        String order_state = order.get("order_state").toString();
        Integer inventory_before = (Integer) good.get("inventory");

        //获取支付后状态
        Assert.assertEquals(400,orderService.payOrder(order_id).getCode());
        order =(Map<String,Object>) orderService.getOrderInfo(order_id).getObject();
        user =(User) userService.getById(order.get("buyer_id").toString()).getObject();
        good =(Map<String,Object>) goodService.getById(order.get("good_id").toString()).getObject();
        Double balance_after = user.getBalance();
        Integer inventory_after = (Integer) good.get("inventory");
        System.out.println(balance_before);
        System.out.println(balance_after);
        Assert.assertEquals(order_state,order.get("order_state").toString());
        Assert.assertTrue(balance_before.equals(balance_after));
        Assert.assertTrue(Objects.equals(inventory_before, inventory_after));

    }

    @Transactional
    @Test
    void payOrderInventoryNotEnough(){
        //测试库存不足
        String order_id = "8284852149786761";

        //获取支付前状态
        Map<String,Object> order =(Map<String,Object>) orderService.getOrderInfo(order_id).getObject();
        User user =(User) userService.getById(order.get("buyer_id").toString()).getObject();
        Map<String,Object> good =(Map<String,Object>) goodService.getById(order.get("good_id").toString()).getObject();
        Double balance_before = user.getBalance();
        String order_state = order.get("order_state").toString();
        Integer inventory_before = (Integer) good.get("inventory");

        //获取支付后状态
        Assert.assertTrue(inventory_before<(Integer) order.get("num"));
        Assert.assertEquals(400,orderService.payOrder(order_id).getCode());
        order =(Map<String,Object>) orderService.getOrderInfo(order_id).getObject();
        user =(User) userService.getById(order.get("buyer_id").toString()).getObject();
        good =(Map<String,Object>) goodService.getById(order.get("good_id").toString()).getObject();
        Double balance_after = user.getBalance();
        Integer inventory_after = (Integer) good.get("inventory");
        Assert.assertEquals(order_state,order.get("order_state").toString());
        Assert.assertTrue(balance_before.equals(balance_after));
        Assert.assertTrue(Objects.equals(inventory_before, inventory_after));
    }

    @Transactional
    @Test
    void payOrderBalanceNotEnough(){
        //测试余额不足
        String order_id = "6761311923331669";

        //获取支付前状态
        Map<String,Object> order =(Map<String,Object>) orderService.getOrderInfo(order_id).getObject();
        User user =(User) userService.getById(order.get("buyer_id").toString()).getObject();
        Map<String,Object> good =(Map<String,Object>) goodService.getById(order.get("good_id").toString()).getObject();
        Double balance_before = user.getBalance();
        String order_state = order.get("order_state").toString();
        Integer inventory_before = (Integer) good.get("inventory");

        //获取支付后状态
        Assert.assertTrue((Double)order.get("price")>balance_before);
        Assert.assertEquals(400,orderService.payOrder(order_id).getCode());
        order =(Map<String,Object>) orderService.getOrderInfo(order_id).getObject();
        user =(User) userService.getById(order.get("buyer_id").toString()).getObject();
        good =(Map<String,Object>) goodService.getById(order.get("good_id").toString()).getObject();
        Double balance_after = user.getBalance();
        Integer inventory_after = (Integer) good.get("inventory");
        Assert.assertEquals(order_state,order.get("order_state").toString());
        Assert.assertTrue(balance_before.equals(balance_after));
        Assert.assertTrue(Objects.equals(inventory_before, inventory_after));
    }

    @Transactional
    @Test
    void payOrderGoodNotOnShell(){
        //测试余额不足
        String order_id = "0884013126529678";

        //获取支付前状态
        Map<String,Object> order =(Map<String,Object>) orderService.getOrderInfo(order_id).getObject();
        User user =(User) userService.getById(order.get("buyer_id").toString()).getObject();
        Map<String,Object> good =(Map<String,Object>) goodService.getById(order.get("good_id").toString()).getObject();
        Double balance_before = user.getBalance();
        String order_state = order.get("order_state").toString();
        Integer inventory_before = (Integer) good.get("inventory");

        //获取支付后状态
        Assert.assertEquals("已下架",good.get("good_state").toString());
        Assert.assertEquals(400,orderService.payOrder(order_id).getCode());
        order =(Map<String,Object>) orderService.getOrderInfo(order_id).getObject();
        user =(User) userService.getById(order.get("buyer_id").toString()).getObject();
        good =(Map<String,Object>) goodService.getById(order.get("good_id").toString()).getObject();
        Double balance_after = user.getBalance();
        Integer inventory_after = (Integer) good.get("inventory");
        Assert.assertEquals(order_state,order.get("order_state").toString());
        Assert.assertTrue(balance_before.equals(balance_after));
        Assert.assertTrue(Objects.equals(inventory_before, inventory_after));
    }

    @Transactional
    @Test
    void ackOrder() {
        //测试正常情况
        String order_id = "6631065311033663";
        //确认收获前
        Map<String,Object> order = (Map<String,Object>)orderService.getOrderInfo(order_id).getObject();
        String order_state = order.get("order_state").toString();
        Assert.assertEquals("待收货",order_state);
        //确认收货后
        orderService.ackOrder(order_id);
        order = (Map<String,Object>)orderService.getOrderInfo(order_id).getObject();
        order_state = order.get("order_state").toString();
        Assert.assertEquals("已收货",order_state);

        //测试异常状态情况
        order_id = "0884013126529678";
        //确认收获前
        order = (Map<String,Object>)orderService.getOrderInfo(order_id).getObject();
        String order_state_before = order.get("order_state").toString();
        Assert.assertTrue(!order_state_before.equals("待收货"));
        //确认收货后
        orderService.ackOrder(order_id);
        order = (Map<String,Object>)orderService.getOrderInfo(order_id).getObject();
        String order_state_after = order.get("order_state").toString();
        Assert.assertEquals(order_state_before,order_state_after);
    }

    @Transactional
    @Test
    void sendPackage() {
        //测试正常情况
        String order_id = "2077079906043517";
        //确认收获前
        Map<String,Object> order = (Map<String,Object>)orderService.getOrderInfo(order_id).getObject();
        String order_state = order.get("order_state").toString();
        Assert.assertEquals("待发货",order_state);
        //确认收货后
        orderService.sendPackage(order_id);
        order = (Map<String,Object>)orderService.getOrderInfo(order_id).getObject();
        order_state = order.get("order_state").toString();
        Assert.assertEquals("待收货",order_state);

        //测试异常状态情况
        order_id = "0884013126529678";
        //发货前
        order = (Map<String,Object>)orderService.getOrderInfo(order_id).getObject();
        String order_state_before = order.get("order_state").toString();
        Assert.assertTrue(!order_state_before.equals("待发货"));
        //发货后
        orderService.sendPackage(order_id);
        order = (Map<String,Object>)orderService.getOrderInfo(order_id).getObject();
        String order_state_after = order.get("order_state").toString();
        Assert.assertEquals(order_state_before,order_state_after);
    }

    @Test
    void getRefundingOrder() {
    }

}