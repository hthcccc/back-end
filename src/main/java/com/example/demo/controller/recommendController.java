package com.example.demo.controller;

import com.example.demo.config.ApiGroup;
import com.example.demo.result.Result;
import com.example.demo.service.RecommendService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins="*")
@RestController("recommend")
@RequestMapping("/recommend")
public class recommendController {
    @Autowired
    RecommendService tmp;

    @Transactional
    @GetMapping("/getAllRecPlans")
    @ApiGroup(group = {"recommend"})
    @ApiOperation(value = "获取所有推广方案")
    public Result getAllRecPlans(){
        return tmp.getAllRecPlans();
    }

    @Transactional
    @GetMapping("/getMyCurrentRecs/{user_id}")
    @ApiGroup(group = {"recommend"})
    @ApiOperation(value = "获取该用户所有正在推广的商品及推广信息",notes ="用户id" )
    public Result getMyCurrentRecs(@PathVariable String user_id){
        return tmp.getMyCurrentRecs(user_id);
    }

    @Transactional
    @GetMapping("/getHistoryRecsByGoodID/{good_id}")
    @ApiGroup(group = {"recommend"})
    @ApiOperation(value = "获取该商品的历史推广",notes ="商品id" )
    public Result getHistoryRecsByGoodID(@PathVariable String good_id){
        return tmp.getHistoryRecsByGoodID(good_id);
    }

    @Transactional
    @PostMapping("/submitRecommend")
    @ApiGroup(group = {"recommend"})
    @ApiOperation(value = "提交推广方案，返回推广申请id",notes ="商品id，推广方案id")
    public Result submitRecommend(@RequestParam("good_id") String good_id,
                                  @RequestParam("plan_id") String plan_id){
        return tmp.submitRecommend(good_id,plan_id);
    }

    @Transactional
    @PostMapping("/payRecommend")
    @ApiGroup(group = {"recommend"})
    @ApiOperation(value = "支付推广申请",notes ="推广申请主码id")
    public Result payRecommend(@RequestParam("rec_id") String rec_id){
        return tmp.payRecommend(rec_id);
    }

}
