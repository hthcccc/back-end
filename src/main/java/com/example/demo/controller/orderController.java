package com.example.demo.controller;

import com.example.demo.config.ApiGroup;
import com.example.demo.model.TradeOrder;
import com.example.demo.result.Result;
import com.example.demo.service.OrderService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins="*")
@RestController("order")
@RequestMapping("/order")
public class orderController {
    @Autowired
    OrderService tmp;

    @Transactional
    @GetMapping("/getAllByBuyer/{user_id}")
    @ApiGroup(group = {"order","user"})
    @ApiOperation(value="得到该用户购买相关的所有订单",notes = "买家id")
    public Result getAllByBuyer(@PathVariable String user_id){
        return tmp.getAllByBuyer(user_id);
    }

    @GetMapping("/getAllBySeller/{user_id}")
    @ApiGroup(group = {"order","user"})
    @ApiOperation(value="得到该用户卖出物品的所有订单",notes = "卖家id")
    public Result getAllByseller(@PathVariable String user_id){
        return tmp.getAllBySeller(user_id);
    }

    @GetMapping("/getOrderInfo/{order_id}")
    @ApiGroup(group = {"order","user"})
    @ApiOperation(value="查询某笔订单详细信息",notes = "订单id")
    public Result getOrderInfo(@PathVariable String order_id){
        return tmp.getOrderInfo(order_id);
    }

    @GetMapping("/getRefundingState/{order_id}")
    @ApiGroup(group = {"order"})
    @ApiOperation(value="返回订单退款状态，200：没退款过，201：退款过，202：已仲裁过",notes = "订单id")
    public Result getRefundingState(@PathVariable String order_id){
        return tmp.getRefundState(order_id);
    }

    @Transactional
    @PostMapping("/generateOrder")
    @ApiGroup(group = {"order"})
    @ApiOperation(value = "生成订单",notes = "买家id，商品id，买家地址，商家地址，购买数量")
    public Result generateOrder(@RequestParam("user_id") String user_id,
                                @RequestParam("good_id") String good_id,
                                @RequestParam("b_addr") String b_addr,
                                @RequestParam("num") Integer num){
        return tmp.generateOrder(user_id,good_id,b_addr,num);
    }

    @Transactional
    @PostMapping("/payOrder")
    @ApiGroup(group = {"order"})
    @ApiOperation(value = "支付订单",notes = "订单id")
    public Result payOrder(@RequestParam("order_id") String order_id){
        return tmp.payOrder(order_id);
    }

    @Transactional
    @PostMapping("/sendPackage")
    @ApiGroup(group = {"order"})
    @ApiOperation(value = "卖家发货",notes = "订单id")
    public Result sendPackage(@RequestParam("order_id") String order_id){
        return tmp.sendPackage(order_id);
    }

    @Transactional
    @PostMapping("/ackOrder")
    @ApiGroup(group = {"order"})
    @ApiOperation(value = "确认收货",notes = "订单id")
    public Result ackOrder(@RequestParam("order_id") String order_id){
        return tmp.ackOrder(order_id);
    }

    @GetMapping("/getAllRefundingOrders/{user_id}")
    @ApiGroup(group = {"order"})
    @ApiOperation(value = "按照时间顺序获取该用户的所有退款中的订单",notes = "用户id")
    public Result getAllRefundingOrders(@PathVariable("user_id") String user_id){
        return tmp.getRefundingOrder(user_id);
    }
}
