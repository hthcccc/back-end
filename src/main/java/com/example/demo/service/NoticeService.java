package com.example.demo.service;

import com.example.demo.repository.*;
import com.example.demo.result.*;
import com.example.demo.model.Notice;
import org.aspectj.weaver.ast.Not;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class NoticeService implements IDGenenrator{
    @Autowired
    userRepository userRepo;
    @Autowired
    noticeRepository noticeRepo;

    public Result sendNotice(String sender,String receiver,String topic,String text){
        Notice notice=new Notice();
        notice.setId(generateID(24));
        notice.setSenderId(sender);
        notice.setReceiverId(receiver);
        notice.setTopic(topic);
        notice.setDate(Instant.now().plusMillis(TimeUnit.HOURS.toMillis(8)));
        notice.setText(text);
        notice.setIsread("0");
        noticeRepo.save(notice);
        return ResultFactory.buildSuccessResult(null);
    }

    public Result readNotice(String notice_id){
        Notice notice=noticeRepo.findById(notice_id).get();
        notice.setIsread("1");
        noticeRepo.save(notice);
        return ResultFactory.buildSuccessResult(notice);
    }

    public Result getAllReceivedNotice(String receiver){
        List<Notice> notices = noticeRepo.getAllReceivedNotice(receiver);
        List<Map<String,Object>> result=new ArrayList<>();
        for(Notice notice:notices){
            Map<String,Object> map=new HashMap<>();
            map.put("notice_id",notice.getId());
            map.put("sender_id",notice.getSender_id());
            if(userRepo.existsById(notice.getSender_id())) {
                map.put("sender_name", userRepo.findById(notice.getSender_id()).get().getName());
            }
            map.put("receiver_id",notice.getReceiver_id());
            if(userRepo.existsById(notice.getReceiver_id())) {
                map.put("receiver_name", userRepo.findById(notice.getReceiver_id()).get().getName());
            }
            map.put("date",notice.getDate());
            map.put("topic",notice.getTopic());
            map.put("text",notice.getText());
            result.add(map);
        }
        return ResultFactory.buildSuccessResult(result);
    }

    public Result getAllSentNotice(String sender){
        List<Notice> notices = noticeRepo.getAllSentNotice(sender);
        List<Map<String,Object>> result=new ArrayList<>();
        for(Notice notice:notices){
            Map<String,Object> map=new HashMap<>();
            map.put("notice_id",notice.getId());
            map.put("sender_id",notice.getSender_id());
            if(userRepo.existsById(notice.getSender_id())) {
                map.put("sender_name", userRepo.findById(notice.getSender_id()).get().getName());
            }else{
                map.put("sender_name", notice.getSender_id());
            }
            map.put("receiver_id",notice.getReceiver_id());
            if(userRepo.existsById(notice.getReceiver_id())) {
                map.put("receiver_name", userRepo.findById(notice.getReceiver_id()).get().getName());
            }else {
                map.put("receiver_name", notice.getReceiver_id());
            }
            map.put("date",notice.getDate());
            map.put("topic",notice.getTopic());
            map.put("text",notice.getText());
            result.add(map);
        }
        return ResultFactory.buildSuccessResult(result);
    }

    @Override
    public StringBuilder tryGetID(int length) {
        StringBuilder id=new StringBuilder();
        Random rd = new SecureRandom();
        for(int i=0;i<length;i++){
            int bit = rd.nextInt(10);
            id.append(String.valueOf(bit));
        }
        return id;
    }

    @Override
    public String generateID(int length) {
        while(true)
        {
            StringBuilder id=tryGetID(length);
            if(!noticeRepo.existsById(id.toString())) return id.toString();
        }
    }
}
