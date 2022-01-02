package com.example.demo.controller;

import com.example.demo.config.ApiGroup;
import com.example.demo.result.Result;
import com.example.demo.service.ComplaintService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins="*")
@RestController("complaint")
@RequestMapping("/complaint")
public class complaintController {
    @Autowired
    ComplaintService tmp;

    @Transactional
    @PostMapping("/getAllUnreadComplaints")
    @ApiGroup(group = {"admin","complaint"})
    @ApiOperation(value = "管理员获取所有未处理的投诉")
    public Result getAllUnreadComplaints(){
        return tmp.getAllUnreadComplaints();
    }

    @Transactional
    @PostMapping("/submitComplaint")
    @ApiGroup(group = {"order","complaint"})
    @ApiOperation(value = "提交投诉",notes = "订单id，文字信息")
    public Result submitComplaint(@RequestParam("order_id") String order_id,
                                  @RequestParam("text") String text){
        return tmp.submitComplaint(order_id,text);
    }

    @Transactional
    @PostMapping("/readOneComplaint")
    @ApiGroup(group = {"admin","complaint"})
    @ApiOperation(value = "管理员读取一条投诉",notes = "订单id")
    public Result readOneComplaint(@RequestParam("order_id") String order_id){
        return tmp.readOneComplaint(order_id);
    }

    @Transactional
    @PostMapping("/finishComplaint")
    @ApiGroup(group = {"admin","complaint"})
    @ApiOperation(value = "管理员处理一条投诉",notes = "订单id，处理方案id，信用名称，信用等级")
    public Result finishComplaint(@RequestParam("order_id") String order_id,
                                  @RequestParam("plan_id") String plan_id,
                                  @RequestParam("credit_name") String credit_name,
                                  @RequestParam("credit_level") String credit_level){
        return tmp.finishComplaint(order_id,plan_id,credit_name,credit_level);
    }
}
