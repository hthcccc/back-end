package com.example.demo.controller;

import com.example.demo.config.ApiGroup;
import com.example.demo.model.Refund;
import com.example.demo.model.TradeOrder;
import com.example.demo.service.RefundService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("refund")
@RequestMapping("/refund")
public class refundController {
    @Autowired
    RefundService tmp;

    @Transactional
    @GetMapping("/getAllbyBuyer")
    @ApiGroup(group = {"refund","user"})
    @ApiOperation(value = "获取该用户提交的所以退款申请",notes = "用户id")
    public List<Refund> getAllbyBuyer(String buyer_id) {
        return tmp.getAllByBuyer(buyer_id);
    }

    @Transactional
    @PostMapping("/submitRefund")
    @ApiGroup(group = {"refund"})
    @ApiOperation(value = "退款",notes = "订单id，举报原因描述")
    public boolean refund(@RequestParam("order_id") String order_id,@RequestParam("text") String text){
        return tmp.refund(order_id,text);
    }
}
