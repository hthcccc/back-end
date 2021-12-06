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
}
