package com.example.demo.controller;

import com.example.demo.config.ApiGroup;
import com.example.demo.model.Refund;
import com.example.demo.model.TradeOrder;
import com.example.demo.result.Result;
import com.example.demo.service.RefundService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins="*")
@RestController("refund")
@RequestMapping("/refund")
public class refundController {
    @Autowired
    RefundService tmp;

    @Transactional
    @GetMapping("/getAllByBuyer/{user_id}")
    @ApiGroup(group = {"refund","user"})
    @ApiOperation(value = "获取该用户提交的所有退款申请",notes = "用户id")
    public Result getAllByBuyer(@PathVariable String user_id) {
        return tmp.getAllByBuyer(user_id);
    }

    @Transactional
    @PostMapping("/submitRefund")
    @ApiGroup(group = {"refund"})
    @ApiOperation(value = "退款",notes = "订单id，举报原因描述")
    public Result refund(@RequestParam("order_id") String order_id, @RequestParam("text") String text){
        return tmp.submitRefund(order_id,text);
    }

    @Transactional
    @PostMapping("/permitRefund")
    @ApiGroup(group = {"refund"})
    @ApiOperation(value = "批准退款",notes = "订单id，举报原因描述")
    public Result permitRefund(@RequestParam("order_id") String order_id){
        return tmp.permitRefund(order_id);
    }

    @Transactional
    @PostMapping("/refuseRefund")
    @ApiGroup(group = {"refund"})
    @ApiOperation(value = "拒绝退款",notes = "订单id，举报原因描述")
    public Result refuseRefund(@RequestParam("order_id") String order_id){
        return tmp.refuseRefund(order_id);
    }

    @Transactional
    @PostMapping("/cancelRefund")
    @ApiGroup(group = {"refund"})
    @ApiOperation(value = "取消退款",notes = "订单id，举报原因描述")
    public Result cancelRefund(@RequestParam("order_id") String order_id){
        return tmp.cancelRefund(order_id);
    }

    @GetMapping("/getRefundInfo/{order_id}")
    @ApiGroup(group = {"refund"})
    @ApiOperation(value = "查看某笔订单退款情况",notes = "订单id")
    public Result getRefundInfo(@PathVariable("order_id") String order_id){
        return tmp.getRefundInfo(order_id);
    }
}
