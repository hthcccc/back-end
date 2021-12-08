package com.example.demo.controller;

import com.example.demo.config.ApiGroup;
import com.example.demo.model.User;
import com.example.demo.service.SubscribeService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("subscribe")
@RequestMapping("/subscribe")
public class subscribeController {
    @Autowired
    SubscribeService tmp;

    @GetMapping("/getSubscribe/{id}")
    @ApiGroup(group = {"subscribe","user"})
    @ApiOperation(value = "获取用户关注列表",notes = "用户id")
    public List<User> getSubscribe(@PathVariable String id){
        return tmp.getSubscribe(id);
    }

    @PostMapping("/addSubscribe")
    @ApiGroup(group = {"subscribe"})
    @ApiOperation(value = "关注用户",notes="用户id，被关注的id")
    public void addSubscribe(@RequestParam("user_id") String user_id,@RequestParam("subscribed_id") String subscribed_id){
        tmp.addSubscribe(user_id,subscribed_id);
    }
}
