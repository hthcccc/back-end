package com.example.demo.service;

import com.example.demo.DemoApplication;
import com.example.demo.model.User;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = DemoApplication.class)
class RefundServiceTest {
    @Autowired
    RefundService refundService;
    @Autowired
    OrderService orderService;
    @Autowired
    UserService userService;

    @Transactional
    @Test
    void submitRefund() {
        //测试正常提交退款
        String order_id = "6631065311033663";
        //退款提交前
        Map<String,Object> order = (Map<String,Object>) orderService.getOrderInfo(order_id).getObject();
        String order_state = order.get("order_state").toString();
        Assert.assertTrue(order_state.equals("待发货")||order_state.equals("待收货"));
        Assert.assertEquals("n",order.get("isRefunding").toString());
        //退款提交后
        refundService.submitRefund(order_id,"仅供测试");
        order = (Map<String,Object>) orderService.getOrderInfo(order_id).getObject();
        Map<String,Object> refund = (Map<String,Object>) refundService.getRefundInfo(order_id).getObject();
        Instant refund_time_1 = (Instant) refund.get("refund_time");
        Assert.assertEquals("y",order.get("isRefunding").toString());

        //测试反复退款
        //退款提交后
        Assert.assertEquals(400,refundService.submitRefund(order_id,"仅供测试").getCode());
        refund = (Map<String,Object>) refundService.getRefundInfo(order_id).getObject();
        Instant refund_time_2 = (Instant) refund.get("refund_time");
        Assert.assertEquals(refund_time_1,refund_time_2);
    }

    @Transactional
    @Test
    void cancelRefund() {
        //首先造一条退款申请
        String order_id = "6631065311033663";
        refundService.submitRefund(order_id,"仅供测试");
        //取消退款前
        Map<String,Object> order = (Map<String,Object>) orderService.getOrderInfo(order_id).getObject();
        String order_state_before = order.get("order_state").toString();
        Assert.assertEquals("y",order.get("isRefunding").toString());
        //取消退款后
        refundService.cancelRefund(order_id);
        order = (Map<String,Object>) orderService.getOrderInfo(order_id).getObject();
        String order_date_after = order.get("order_state").toString();
        Map<String,Object> refund = (Map<String,Object>) refundService.getRefundInfo(order_id).getObject();
        Assert.assertNull(refund);
        Assert.assertEquals("n",order.get("isRefunding").toString());
        Assert.assertEquals(order_state_before,order_date_after);
    }

    @Transactional
    @Test
    void permitRefund() {
        //首先造一条退款申请
        String order_id = "6631065311033663";
        refundService.submitRefund(order_id,"仅供测试");
        //退款批准前
        Map<String,Object> order = (Map<String,Object>) orderService.getOrderInfo(order_id).getObject();
        String user_id = order.get("buyer_id").toString();
        Double price = (Double) order.get("price");
        User user = (User)userService.getById(user_id).getObject();
        Double balance_before = user.getBalance();

        //退款批准后
        refundService.permitRefund(order_id);
        order = (Map<String,Object>) orderService.getOrderInfo(order_id).getObject();
        String order_state = order.get("order_state").toString();
        String isRefunding = order.get("isRefunding").toString();
        Map<String,Object> refund = (Map<String,Object>) refundService.getRefundInfo(order_id).getObject();
        String refund_state = refund.get("refund_state").toString();
        user = (User)userService.getById(user_id).getObject();
        Double balance_after = user.getBalance();
        Assert.assertEquals("已退款",order_state);
        Assert.assertEquals("n",isRefunding);
        Assert.assertTrue((balance_before+price)==balance_after);
        Assert.assertEquals("卖家批准",refund_state);
    }

    @Transactional
    @Test
    void refuseRefund() {
        //首先造一条退款申请
        String order_id = "6631065311033663";
        refundService.submitRefund(order_id,"仅供测试");
        Map<String,Object> order = (Map<String,Object>) orderService.getOrderInfo(order_id).getObject();
        String order_state_before = order.get("order_state").toString();

        refundService.refuseRefund(order_id);
        order = (Map<String,Object>) orderService.getOrderInfo(order_id).getObject();
        Map<String,Object> refund = (Map<String,Object>) refundService.getRefundInfo(order_id).getObject();
        String refund_state = refund.get("refund_state").toString();
        String order_state_after = order.get("order_state").toString();
        String isRefunding = order.get("isRefunding").toString();
        Assert.assertEquals("卖家驳回",refund_state);
        Assert.assertEquals(order_state_before,order_state_after);
        Assert.assertEquals("y",isRefunding);
    }

    /**
     * 系统测试
     */
    @Transactional
    @Test
    void RefundCase1() {
        String order_id = "6631065311033663";
        Assert.assertEquals(200,refundService.submitRefund(order_id,"仅供测试").getCode());
        Assert.assertEquals(200,refundService.refuseRefund(order_id).getCode());
        Assert.assertEquals(200,refundService.cancelRefund(order_id).getCode());
        Assert.assertEquals(200,refundService.submitRefund(order_id,"仅供测试").getCode());
        Assert.assertEquals(200,refundService.refuseRefund(order_id).getCode());
        Assert.assertEquals(200,refundService.submitArbitration(order_id,"仅供测试",null).getCode());
        Assert.assertEquals(200,refundService.permitArbitration(order_id).getCode());
    }


}