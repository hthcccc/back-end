package com.example.demo.controller;

import com.example.demo.config.ApiGroup;
import com.example.demo.result.*;
import com.example.demo.service.NoticeService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins="*")
@RestController("notice")
@RequestMapping("/notice")
public class noticeController {
    @Autowired
    NoticeService tmp;

    @PostMapping("/sendNotice")
    @ApiGroup(group = {"notice"})
    @ApiOperation(value = "发送通知",notes = "发送方id，接收方id，标题，文本信息")
    public Result sendNotice(@RequestParam("sender_id") String sender_id,
                             @RequestParam("receiver_id") String receiver_id,
                             @RequestParam("topic") String topic,
                             @RequestParam("text") String text){
        return tmp.sendNotice(sender_id,receiver_id,topic,text);
    }

    @PostMapping("/readNotice")
    @ApiGroup(group = {"notice"})
    @ApiOperation(value = "阅读通知",notes = "发送方id，接收方id，文本信息")
    public Result readNotice(@RequestParam("notice_id") String notice_id){
        return tmp.readNotice(notice_id);
    }

    @GetMapping("/getAllReceivedNotice/{receiver_id}")
    @ApiGroup(group = {"notice"})
    @ApiOperation(value = "得到我收到的所有通知",notes = "接收方id")
    public Result getAllReceivedNotice(@PathVariable("receiver_id") String receiver_id){
        return tmp.getAllReceivedNotice(receiver_id);
    }

    @GetMapping("/getAllSentNotice/{sender_id}")
    @ApiGroup(group = {"notice"})
    @ApiOperation(value = "得到我发出的所有通知",notes = "发送方id")
    public Result getAllSentNotice(@PathVariable("sender_id") String sender_id){
        return tmp.getAllSentNotice(sender_id);
    }

}
