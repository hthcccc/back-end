package com.example.demo.controller;

import com.example.demo.config.ApiGroup;
import com.example.demo.model.History;
import com.example.demo.result.Result;
import com.example.demo.service.HistoryService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins="*")
@RestController("history")
@RequestMapping("/history")
public class historyController {
    @Autowired
    HistoryService tmp;

    @GetMapping("/getHistory/{user_id}")
    @ApiGroup(group = {"history"})
    @ApiOperation(value="获取用户浏览历史",notes = "用户id")
    public Result getHistory(@PathVariable String user_id){
        return tmp.getHistory(user_id);
    }

    @PostMapping("/removeOneHistory")
    @ApiGroup(group = "history")
    @ApiOperation(value = "删除用户某条浏览记录",notes="用户id，商品id")
    public Result removeOneHistory(@RequestParam("user_id") String user_id,
                                 @RequestParam("good_id") String good_id){
        return tmp.removeOneHistory(user_id,good_id);
    }

    @PostMapping("/removeAllHistory")
    @ApiGroup(group = "history")
    @ApiOperation(value = "清空用户浏览记录",notes="用户id")
    public Result removeAllHistory(@RequestParam("user_id") String user_id){
        return tmp.removeAllHistory(user_id);
    }
}
