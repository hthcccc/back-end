package com.example.demo.service;

import com.example.demo.DemoApplication;
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
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = DemoApplication.class)
class OrderServiceTest {

    @Autowired
    OrderService orderService;
    @Autowired
    RefundService refundService;

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

    @Test
    void payOrder() {
    }

    @Test
    void ackOrder() {
    }

    @Test
    void sendPackage() {
    }

    @Test
    void getRefundingOrder() {
    }

    @Test
    void setOrderState() {
    }

    @Test
    void getAllByBuyer() {
    }

    @Test
    void getAllBySeller() {
    }
}