package com.example.demo.controller;

import com.example.demo.config.ApiGroup;
import com.example.demo.model.History;
import com.example.demo.service.HistoryService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("history")
@RequestMapping("/history")
public class historyController {
    @Autowired
    HistoryService tmp;

    @GetMapping("/getHistory/{userId}")
    @ApiGroup(group = {"history"})
    @ApiOperation(value="获取用户浏览历史",notes = "用户id")
    public List<History> getHistory(@PathVariable String userId){
        return tmp.getHistory(userId);
    }
}
