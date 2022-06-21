package com.example.demo.IntegrationTest;

import com.example.demo.DemoApplication;
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

import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = DemoApplication.class)
public class OrderTest {
    @Autowired
    RefundService refundService;
    @Autowired
    OrderService orderService;
    @Autowired
    UserService userService;
    @Autowired
    GoodService goodService;

    /**
     * 集成测试
     * 发货——收货
     */
    @Transactional
    @Test
    public void OrderCase1(){
        //测试初态
        String order_id = "2077079906043517";
        Map<String,Object> order =(Map<String,Object>) orderService.getOrderInfo(order_id).getObject();
        String order_state_0 = order.get("order_state").toString();
        Assert.assertEquals("待发货",order_state_0);

        //测试发货
        orderService.sendPackage(order_id);
        order =(Map<String,Object>) orderService.getOrderInfo(order_id).getObject();
        String order_state_1 = order.get("order_state").toString();
        Assert.assertEquals("待收货",order_state_1);

        //测试收货
        orderService.ackOrder(order_id);
        order =(Map<String,Object>) orderService.getOrderInfo(order_id).getObject();
        String order_state_2 = order.get("order_state").toString();
        Assert.assertEquals("已收货",order_state_2);
    }

    /**
     * 集成测试
     * 发货——退款申请——卖家驳回——买家取消——再次申请——卖家驳回——提交仲裁——仲裁驳回——只能收货
     */
    @Transactional
    @Test
    public void OrderCase2(){
        //测试初态
        String order_id = "2077079906043517";
        Map<String,Object> order =(Map<String,Object>) orderService.getOrderInfo(order_id).getObject();
        String order_state_0 = order.get("order_state").toString();
        Assert.assertEquals("待发货",order_state_0);

        //测试发货
        orderService.sendPackage(order_id);
        order =(Map<String,Object>) orderService.getOrderInfo(order_id).getObject();
        String order_state_1 = order.get("order_state").toString();
        Assert.assertEquals("待收货",order_state_1);

        //流程测试
        Assert.assertEquals(200,refundService.submitRefund(order_id,"仅供测试").getCode());
        Assert.assertEquals(200,refundService.refuseRefund(order_id).getCode());
        Assert.assertEquals(200,refundService.cancelRefund(order_id).getCode());
        Assert.assertEquals(200,refundService.submitRefund(order_id,"仅供测试").getCode());
        Assert.assertEquals(200,refundService.refuseRefund(order_id).getCode());
        Assert.assertEquals(200,refundService.submitArbitration(order_id,"仅供测试",null).getCode());
        Assert.assertEquals(200,refundService.refuseArbitration(order_id).getCode());
        order =(Map<String,Object>) orderService.getOrderInfo(order_id).getObject();
        String order_state_2 = order.get("order_state").toString();
        Assert.assertEquals("待收货",order_state_2);

        //测试收货
        orderService.ackOrder(order_id);
        order =(Map<String,Object>) orderService.getOrderInfo(order_id).getObject();
        String order_state_3 = order.get("order_state").toString();
        Assert.assertEquals("已收货",order_state_3);
    }
}
