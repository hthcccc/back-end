package com.example.demo.controller;

import com.example.demo.config.ApiGroup;
import com.example.demo.model.TradeOrder;
import com.example.demo.service.OrderService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("order")
@RequestMapping("/order")
public class orderController {
    @Autowired
    OrderService tmp;

    @Transactional
    @GetMapping("/getAllByBuyer")
    @ApiGroup(group = {"order","user"})
    @ApiOperation(value="得到该用户购买相关的所有订单",notes = "买家id")
    public List<TradeOrder> getAllByBuyer(String buyerId){
        return tmp.getAllByBuyer(buyerId);
    }

    @GetMapping("/getAllBySeller")
    @ApiGroup(group = {"order","user"})
    @ApiOperation(value="得到该用户卖出物品的所有订单",notes = "卖家id")
    public List<String> getAllByseller(String sellerId){
        return tmp.getAllBySeller(sellerId);
    }

    @Transactional
    @PostMapping("/generateOrder")
    @ApiGroup(group = {"order"})
    @ApiOperation(value = "生成订单",notes = "买家id，商品id，买家地址，商家地址，购买数量")
    public String generateOrder(@RequestParam("u_id") String u_id,
                                @RequestParam("g_id") String g_id,
                                @RequestParam("b_addr") String b_addr,
                                @RequestParam("s_addr") String s_addr,
                                @RequestParam("num") Integer num){
        return tmp.generateOrder(u_id,g_id,b_addr,s_addr,num);
    }

    @Transactional
    @PostMapping("/payOrder")
    @ApiGroup(group = {"order"})
    @ApiOperation(value = "支付订单",notes = "订单id")
    public boolean payOrder(@RequestParam("order_id") String order_id){
        return tmp.payOrder(order_id);
    }

    @Transactional
    @PostMapping("/sendPackage")
    @ApiGroup(group = {"order"})
    @ApiOperation(value = "卖家发货",notes = "订单id")
    public void sendPackage(String order_id){
        tmp.setOrderState(order_id,"待收货");
    }

    @Transactional
    @PostMapping("/ackOrder")
    @ApiGroup(group = {"order"})
    @ApiOperation(value = "确认收货",notes = "订单id")
    public void ackOrder(String order_id){
        tmp.setOrderState(order_id,"已收货");
    }

}
