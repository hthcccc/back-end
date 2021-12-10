package com.example.demo.controller;

import com.example.demo.config.ApiGroup;
import com.example.demo.model.TradeOrder;
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
    public List<TradeOrder> getAllByBuyer(@PathVariable String user_id){
        return tmp.getAllByBuyer(user_id);
    }

    @GetMapping("/getAllBySeller/{user_id}")
    @ApiGroup(group = {"order","user"})
    @ApiOperation(value="得到该用户卖出物品的所有订单",notes = "卖家id")
    public List<String> getAllByseller(@PathVariable String user_id){
        return tmp.getAllBySeller(user_id);
    }

    @Transactional
    @PostMapping("/generateOrder")
    @ApiGroup(group = {"order"})
    @ApiOperation(value = "生成订单",notes = "买家id，商品id，买家地址，商家地址，购买数量")
    public String generateOrder(@RequestParam("user_id") String user_id,
                                @RequestParam("good_id") String good_id,
                                @RequestParam("b_addr") String b_addr,
                                @RequestParam("s_addr") String s_addr,
                                @RequestParam("num") Integer num){
        return tmp.generateOrder(user_id,good_id,b_addr,s_addr,num);
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
    public void sendPackage(@RequestParam("order_id") String order_id){
        tmp.sendPackage(order_id);
    }

    @Transactional
    @PostMapping("/ackOrder")
    @ApiGroup(group = {"order"})
    @ApiOperation(value = "确认收货",notes = "订单id")
    public void ackOrder(@RequestParam("order_id") String order_id){
        tmp.ackOrder(order_id);
    }

}
