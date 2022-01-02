package com.example.demo.controller;

import com.example.demo.config.ApiGroup;
import com.example.demo.result.Result;
import com.example.demo.service.ChatService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins="*")
@RestController("chat")
@RequestMapping("/chat")
public class chatController {
    @Autowired
    ChatService tmp;

    @Transactional
    @PostMapping("/sendChat")
    @ApiGroup(group = {"chat"})
    @ApiOperation(value = "发送私信",notes = "发送方id，接收方id，文本信息")
    public Result sendChat(@RequestParam("sender_id") String sender_id,
                             @RequestParam("receiver_id") String receiver_id,
                             @RequestParam("text") String text){
        return tmp.sendChat(sender_id,receiver_id,text);
    }

    @Transactional
    @PostMapping("/readChat")
    @ApiGroup(group = {"chat"})
    @ApiOperation(value = "查看user1与user2的所有私信记录",notes = "用户1的id，用户2的id")
    public Result readChat(@RequestParam("user1") String user1,
                             @RequestParam("user2") String user2){
        return tmp.readChat(user1, user2);
    }

    @GetMapping("/getMyChats/{user_id}")
    @ApiGroup(group = {"chat"})
    @ApiOperation(value = "查看我的聊天区,可以看到所有私信的最近一条私信",notes = "用户id")
    public Result getMyChats(@PathVariable String user_id){
        return tmp.getMyChats(user_id);
    }
}
