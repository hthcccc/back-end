package com.example.demo.service;

import com.example.demo.DemoApplication;
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
    }

    @Test
    void permitRefund() {
    }

    @Test
    void refuseRefund() {
    }

    @Test
    void submitArbitration() {
    }

    @Test
    void permitArbitration() {
    }

    @Test
    void refuseArbitration() {
    }
}