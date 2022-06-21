package com.example.demo.IntegrationTest;

import com.example.demo.DemoApplication;
import com.example.demo.model.Good;
import com.example.demo.model.User;
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

    /**
     * 集成测试
     * 发货——退款申请——卖家驳回——买家取消——再次申请——卖家驳回——提交仲裁——仲裁批准（已退款）
     */
    @Transactional
    @Test
    public void OrderCase3(){
        //测试初态
        String order_id = "2077079906043517";
        Map<String,Object> order =(Map<String,Object>) orderService.getOrderInfo(order_id).getObject();
        Double price = (Double)order.get("price");
        String user_id = order.get("buyer_id").toString();
        User user = (User)userService.getById(user_id).getObject();
        Double balance_before = user.getBalance();
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
        Assert.assertEquals(200,refundService.permitArbitration(order_id).getCode());

        //测试末态
        order =(Map<String,Object>) orderService.getOrderInfo(order_id).getObject();
        String order_state_2 = order.get("order_state").toString();
        Assert.assertEquals("已退款",order_state_2);
        user = (User)userService.getById(user_id).getObject();
        Double balance_after = user.getBalance();
        Assert.assertTrue((balance_before+price)==balance_after);
    }

    /**
     * 集成测试
     * 发货——退款申请——卖家驳回——买家取消——再次申请——卖家批准（已退款）
     */
    @Transactional
    @Test
    public void OrderCase4(){
        //测试初态
        String order_id = "2077079906043517";
        Map<String,Object> order =(Map<String,Object>) orderService.getOrderInfo(order_id).getObject();
        Double price = (Double)order.get("price");
        String user_id = order.get("buyer_id").toString();
        User user = (User)userService.getById(user_id).getObject();
        Double balance_before = user.getBalance();
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
        Assert.assertEquals(200,refundService.permitRefund(order_id).getCode());

        //测试末态
        order =(Map<String,Object>) orderService.getOrderInfo(order_id).getObject();
        String order_state_2 = order.get("order_state").toString();
        Assert.assertEquals("已退款",order_state_2);
        user = (User)userService.getById(user_id).getObject();
        Double balance_after = user.getBalance();
        Assert.assertTrue((balance_before+price)==balance_after);
    }

    /**
     * 集成测试
     * 下单——支付
     */
    @Transactional
    @Test
    public void OrderCase5(){
        //测试初态
        String good_id = "0840289660372971";
        String user_id = "hth";
        Integer num = 1;
        User user = (User)userService.getById(user_id).getObject();
        Double balance_0 = user.getBalance();
        Map<String,Object> good = (Map<String,Object>)goodService.getById(good_id).getObject();
        Double good_price = (Double) good.get("price");
        Double freight = (Double) good.get("freight");
        Integer inventory_0 = (Integer) good.get("inventory");
        Assert.assertTrue(num<=inventory_0);

        //测试创建订单后
        String order_id = orderService.generateOrder(user_id,good_id,"上海市杨浦区",num).getObject().toString();
        Map<String,Object> order =(Map<String,Object>) orderService.getOrderInfo(order_id).getObject();
        Double price = (Double) order.get("price");
        user = (User)userService.getById(user_id).getObject();
        Double balance_1 = user.getBalance();
        good = (Map<String,Object>)goodService.getById(good_id).getObject();
        Integer inventory_1 = (Integer) good.get("inventory");
        Assert.assertTrue((good_price*num+freight)==price);
        Assert.assertTrue(num<=inventory_1);
        Assert.assertTrue(balance_0.equals(balance_1));

        //测试支付订单后
        orderService.payOrder(order_id);
        order =(Map<String,Object>) orderService.getOrderInfo(order_id).getObject();
        price = (Double) order.get("price");
        user = (User)userService.getById(user_id).getObject();
        Double balance_2 = user.getBalance();
        good = (Map<String,Object>)goodService.getById(good_id).getObject();
        Integer inventory_2 = (Integer) good.get("inventory");
        Assert.assertTrue((good_price*num+freight)==price);
        Assert.assertTrue((inventory_1-num)==inventory_2);
        Assert.assertTrue((balance_1-price)==balance_2);
    }

    /**
     * 集成测试（这个不用测，不用写文档）
     * 下单——支付——发货——收货
     */
    @Transactional
    @Test
    public void OrderCase6(){
        //测试用例
        String good_id = "0840289660372971";
        String user_id = "hth";
        Integer num = 1;
        //创建订单后
        String order_id = orderService.generateOrder(user_id,good_id,"上海市杨浦区",num).getObject().toString();
        //测试支付订单后
        orderService.payOrder(order_id);
        Map<String,Object> order =(Map<String,Object>) orderService.getOrderInfo(order_id).getObject();
        String order_state_0 = order.get("order_state").toString();
        Assert.assertEquals("待发货",order_state_0);
        //测试发货收货后
        Assert.assertEquals(200,orderService.sendPackage(order_id).getCode());
        Assert.assertEquals(200,orderService.ackOrder(order_id).getCode());
        order =(Map<String,Object>) orderService.getOrderInfo(order_id).getObject();
        String order_state_1 = order.get("order_state").toString();
        Assert.assertEquals("已收货",order_state_1);
    }

    /**
     * 集成测试
     * 下单——支付——发货——退款申请——卖家驳回——买家取消——
     * 再次申请——卖家驳回——提交仲裁——仲裁驳回——只能收货
     */
    @Transactional
    @Test
    public void OrderCase7(){
        //测试用例
        String good_id = "0840289660372971";
        String user_id = "hth";
        Integer num = 1;
        //创建订单后
        String order_id = orderService.generateOrder(user_id,good_id,"上海市杨浦区",num).getObject().toString();
        //测试支付订单后
        orderService.payOrder(order_id);
        //测试发货收货后
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
     * 集成测试
     * 下单——支付——发货——退款申请——卖家驳回——买家取消——只能收货
     */
    @Transactional
    @Test
    public void OrderCase8(){
        //测试用例
        String good_id = "0840289660372971";
        String user_id = "hth";
        Integer num = 1;
        //创建订单后
        String order_id = orderService.generateOrder(user_id,good_id,"上海市杨浦区",num).getObject().toString();
        //测试支付订单后
        orderService.payOrder(order_id);
        //测试发货收货后
        Assert.assertEquals(200,orderService.sendPackage(order_id).getCode());
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
     * 集成测试
     * 下单——支付——发货——退款申请——卖家驳回——买家取消——再次申请
     * ——卖家批准（已退款）
     */
    @Transactional
    @Test
    public void OrderCase9(){
        //测试用例
        String good_id = "0840289660372971";
        String user_id = "hth";
        Integer num = 1;
        User user = (User) userService.getById(user_id).getObject();
        Double balance_0 = user.getBalance();
        //创建订单后
        String order_id = orderService.generateOrder(user_id,good_id,"上海市杨浦区",num).getObject().toString();
        //测试支付订单后
        orderService.payOrder(order_id);
        //测试发货收货后
        Assert.assertEquals(200,orderService.sendPackage(order_id).getCode());
        Assert.assertEquals(200,refundService.submitRefund(order_id,"仅供测试").getCode());
        Assert.assertEquals(200,refundService.refuseRefund(order_id).getCode());
        Assert.assertEquals(200,refundService.cancelRefund(order_id).getCode());
        Assert.assertEquals(200,refundService.submitRefund(order_id,"仅供测试").getCode());
        Assert.assertEquals(200,refundService.permitRefund(order_id).getCode());
        Map<String,Object> order =(Map<String,Object>) orderService.getOrderInfo(order_id).getObject();
        String order_state_1 = order.get("order_state").toString();
        String isRefunding = order.get("isRefunding").toString();
        user = (User) userService.getById(user_id).getObject();
        Double balance_1 = user.getBalance();
        Assert.assertEquals("已退款",order_state_1);
        Assert.assertEquals("n",isRefunding);
        Assert.assertTrue(balance_0.equals(balance_1));
    }

    /**
     * 集成测试
     * 下单——支付——发货——退款申请——卖家驳回——买家取消——再次申请
     * ——卖家驳回——仲裁申请——仲裁批准（已退款）
     */
    @Transactional
    @Test
    public void OrderCase10(){
        //测试用例
        String good_id = "0840289660372971";
        String user_id = "hth";
        Integer num = 1;
        User user = (User) userService.getById(user_id).getObject();
        Double balance_0 = user.getBalance();
        //创建订单后
        String order_id = orderService.generateOrder(user_id,good_id,"上海市杨浦区",num).getObject().toString();
        //测试支付订单后
        orderService.payOrder(order_id);
        //测试发货收货后
        Assert.assertEquals(200,orderService.sendPackage(order_id).getCode());
        Assert.assertEquals(200,refundService.submitRefund(order_id,"仅供测试").getCode());
        Assert.assertEquals(200,refundService.refuseRefund(order_id).getCode());
        Assert.assertEquals(200,refundService.cancelRefund(order_id).getCode());
        Assert.assertEquals(200,refundService.submitRefund(order_id,"仅供测试").getCode());
        Assert.assertEquals(200,refundService.refuseRefund(order_id).getCode());
        Assert.assertEquals(200,refundService.submitArbitration(order_id,"仅供测试",null).getCode());
        Assert.assertEquals(200,refundService.permitArbitration(order_id).getCode());

        Map<String,Object> order =(Map<String,Object>) orderService.getOrderInfo(order_id).getObject();
        String order_state_1 = order.get("order_state").toString();
        String isRefunding = order.get("isRefunding").toString();
        user = (User) userService.getById(user_id).getObject();
        Double balance_1 = user.getBalance();
        Assert.assertEquals("已退款",order_state_1);
        Assert.assertEquals("n",isRefunding);
        Assert.assertTrue(balance_0.equals(balance_1));
    }
}
