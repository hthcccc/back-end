package com.example.demo.IntegrationTest;

import com.example.demo.DemoApplication;
import com.example.demo.model.User;
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
public class RefundTest {
    @Autowired
    RefundService refundService;
    @Autowired
    OrderService orderService;
    @Autowired
    UserService userService;

    /**
     * 集成测试
     * 初次提交退款——卖家驳回——买家取消——买家再次提交——卖家批准
     */
    @Transactional
    @Test
    public void RefundCase1() {
        String order_id = "6631065311033663";
        //获取测试前状态
        Map<String,Object> order = (Map<String,Object>) orderService.getOrderInfo(order_id).getObject();
        Double price = (Double) order.get("price");
        String user_id = order.get("buyer_id").toString();
        User user = (User) userService.getById(user_id).getObject();
        Double balance_before = user.getBalance();
        //流程测试
        Assert.assertEquals(200,refundService.submitRefund(order_id,"仅供测试").getCode());
        Map<String,Object> refund = (Map<String,Object>) refundService.getRefundInfo(order_id).getObject();
        Assert.assertEquals("待审核",refund.get("refund_state").toString());

        Assert.assertEquals(200,refundService.refuseRefund(order_id).getCode());
        refund = (Map<String,Object>) refundService.getRefundInfo(order_id).getObject();
        Assert.assertEquals("卖家驳回",refund.get("refund_state").toString());

        Assert.assertEquals(200,refundService.cancelRefund(order_id).getCode());
        Assert.assertNull(refundService.getRefundInfo(order_id).getObject());

        Assert.assertEquals(200,refundService.submitRefund(order_id,"仅供测试").getCode());
        refund = (Map<String,Object>) refundService.getRefundInfo(order_id).getObject();
        Assert.assertEquals("待审核",refund.get("refund_state").toString());

        Assert.assertEquals(200,refundService.permitRefund(order_id).getCode());
        refund = (Map<String,Object>) refundService.getRefundInfo(order_id).getObject();
        Assert.assertEquals("卖家批准",refund.get("refund_state").toString());

        //获取测试后状态
        order = (Map<String,Object>) orderService.getOrderInfo(order_id).getObject();
        String order_state = order.get("order_state").toString();
        String isRefunding = order.get("isRefunding").toString();
        user = (User) userService.getById(user_id).getObject();
        Double balance_after = user.getBalance();
        Assert.assertTrue((balance_before+price)==balance_after);
        Assert.assertEquals("已退款",order_state);
        Assert.assertEquals("n",isRefunding);
    }

    /**
     * 集成测试
     * 初次提交退款——卖家驳回——买家取消——买家再次提交——卖家驳回——买家提交仲裁——仲裁批准
     */
    @Transactional
    @Test
    public void RefundCase2() {
        String order_id = "6631065311033663";
        //获取测试前状态
        Map<String,Object> order = (Map<String,Object>) orderService.getOrderInfo(order_id).getObject();
        Double price = (Double) order.get("price");
        String user_id = order.get("buyer_id").toString();
        User user = (User) userService.getById(user_id).getObject();
        Double balance_before = user.getBalance();
        //流程测试
        Assert.assertEquals(200,refundService.submitRefund(order_id,"仅供测试").getCode());
        Map<String,Object> refund = (Map<String,Object>) refundService.getRefundInfo(order_id).getObject();
        Assert.assertEquals("待审核",refund.get("refund_state").toString());

        Assert.assertEquals(200,refundService.refuseRefund(order_id).getCode());
        refund = (Map<String,Object>) refundService.getRefundInfo(order_id).getObject();
        Assert.assertEquals("卖家驳回",refund.get("refund_state").toString());

        Assert.assertEquals(200,refundService.cancelRefund(order_id).getCode());
        Assert.assertNull(refundService.getRefundInfo(order_id).getObject());

        Assert.assertEquals(200,refundService.submitRefund(order_id,"仅供测试").getCode());
        refund = (Map<String,Object>) refundService.getRefundInfo(order_id).getObject();
        Assert.assertEquals("待审核",refund.get("refund_state").toString());

        Assert.assertEquals(200,refundService.refuseRefund(order_id).getCode());
        refund = (Map<String,Object>) refundService.getRefundInfo(order_id).getObject();
        Assert.assertEquals("卖家驳回",refund.get("refund_state").toString());

        Assert.assertEquals(200,refundService.submitArbitration(order_id,"仅供测试",null).getCode());
        refund = (Map<String,Object>) refundService.getRefundInfo(order_id).getObject();
        Assert.assertEquals("待仲裁",refund.get("refund_state").toString());

        Assert.assertEquals(200,refundService.permitArbitration(order_id).getCode());
        refund = (Map<String,Object>) refundService.getRefundInfo(order_id).getObject();
        Assert.assertEquals("仲裁批准",refund.get("refund_state").toString());

        //获取测试后状态
        order = (Map<String,Object>) orderService.getOrderInfo(order_id).getObject();
        String order_state = order.get("order_state").toString();
        String isRefunding = order.get("isRefunding").toString();
        user = (User) userService.getById(user_id).getObject();
        Double balance_after = user.getBalance();
        Assert.assertTrue((balance_before+price)==balance_after);
        Assert.assertEquals("已退款",order_state);
        Assert.assertEquals("n",isRefunding);
    }
    /**
     * 集成测试
     * 初次提交退款——卖家驳回——买家取消——买家再次提交——卖家批准
     */
    @Transactional
    @Test
    public void RefundCase3() {
        String order_id = "6631065311033663";
        //获取测试前状态
        Map<String,Object> order = (Map<String,Object>) orderService.getOrderInfo(order_id).getObject();
        Double price = (Double) order.get("price");
        String user_id = order.get("buyer_id").toString();
        User user = (User) userService.getById(user_id).getObject();
        Double balance_before = user.getBalance();
        //流程测试
        Assert.assertEquals(200,refundService.submitRefund(order_id,"仅供测试").getCode());
        Map<String,Object> refund = (Map<String,Object>) refundService.getRefundInfo(order_id).getObject();
        Assert.assertEquals("待审核",refund.get("refund_state").toString());

        Assert.assertEquals(200,refundService.refuseRefund(order_id).getCode());
        refund = (Map<String,Object>) refundService.getRefundInfo(order_id).getObject();
        Assert.assertEquals("卖家驳回",refund.get("refund_state").toString());

        Assert.assertEquals(200,refundService.cancelRefund(order_id).getCode());
        Assert.assertNull(refundService.getRefundInfo(order_id).getObject());

        Assert.assertEquals(200,refundService.submitRefund(order_id,"仅供测试").getCode());
        refund = (Map<String,Object>) refundService.getRefundInfo(order_id).getObject();
        Assert.assertEquals("待审核",refund.get("refund_state").toString());

        Assert.assertEquals(200,refundService.permitRefund(order_id).getCode());
        refund = (Map<String,Object>) refundService.getRefundInfo(order_id).getObject();
        Assert.assertEquals("卖家批准",refund.get("refund_state").toString());

        //获取测试后状态
        order = (Map<String,Object>) orderService.getOrderInfo(order_id).getObject();
        String order_state = order.get("order_state").toString();
        String isRefunding = order.get("isRefunding").toString();
        user = (User) userService.getById(user_id).getObject();
        Double balance_after = user.getBalance();
        Assert.assertTrue((balance_before+price)==balance_after);
        Assert.assertEquals("已退款",order_state);
        Assert.assertEquals("n",isRefunding);
    }

    /**
     * 集成测试
     * 初次提交退款——卖家驳回——买家取消——买家再次提交——卖家驳回——买家提交仲裁——仲裁驳回
     */
    @Transactional
    @Test
    public void RefundCase4() {
        String order_id = "6631065311033663";
        //获取测试前状态
        Map<String,Object> order = (Map<String,Object>) orderService.getOrderInfo(order_id).getObject();
        String order_state_before = order.get("order_state").toString();
        String user_id = order.get("buyer_id").toString();
        User user = (User) userService.getById(user_id).getObject();
        Double balance_before = user.getBalance();
        //流程测试
        Assert.assertEquals(200,refundService.submitRefund(order_id,"仅供测试").getCode());
        Map<String,Object> refund = (Map<String,Object>) refundService.getRefundInfo(order_id).getObject();
        Assert.assertEquals("待审核",refund.get("refund_state").toString());

        Assert.assertEquals(200,refundService.refuseRefund(order_id).getCode());
        refund = (Map<String,Object>) refundService.getRefundInfo(order_id).getObject();
        Assert.assertEquals("卖家驳回",refund.get("refund_state").toString());

        Assert.assertEquals(200,refundService.cancelRefund(order_id).getCode());
        Assert.assertNull(refundService.getRefundInfo(order_id).getObject());

        Assert.assertEquals(200,refundService.submitRefund(order_id,"仅供测试").getCode());
        refund = (Map<String,Object>) refundService.getRefundInfo(order_id).getObject();
        Assert.assertEquals("待审核",refund.get("refund_state").toString());

        Assert.assertEquals(200,refundService.refuseRefund(order_id).getCode());
        refund = (Map<String,Object>) refundService.getRefundInfo(order_id).getObject();
        Assert.assertEquals("卖家驳回",refund.get("refund_state").toString());

        Assert.assertEquals(200,refundService.submitArbitration(order_id,"仅供测试",null).getCode());
        refund = (Map<String,Object>) refundService.getRefundInfo(order_id).getObject();
        Assert.assertEquals("待仲裁",refund.get("refund_state").toString());

        Assert.assertEquals(200,refundService.refuseArbitration(order_id).getCode());
        refund = (Map<String,Object>) refundService.getRefundInfo(order_id).getObject();
        Assert.assertEquals("仲裁驳回",refund.get("refund_state").toString());

        //获取测试后状态
        order = (Map<String,Object>) orderService.getOrderInfo(order_id).getObject();
        String order_state_after = order.get("order_state").toString();
        String isRefunding = order.get("isRefunding").toString();
        user = (User) userService.getById(user_id).getObject();
        Double balance_after = user.getBalance();
        System.out.println(balance_before);
        System.out.println(balance_after);
        Assert.assertTrue(balance_before.equals(balance_after));
        Assert.assertEquals(order_state_before,order_state_after);
        Assert.assertEquals("n",isRefunding);
        //测试仲裁后不能再退款申请
        Assert.assertEquals(400,refundService.submitRefund(order_id,"仅供测试").getCode());
        order = (Map<String,Object>) orderService.getOrderInfo(order_id).getObject();
        String order_state_final = order.get("order_state").toString();
        String isRefunding_final = order.get("isRefunding").toString();
        Assert.assertEquals("n",isRefunding_final);
        Assert.assertEquals(order_state_after,order_state_final);
    }
}
