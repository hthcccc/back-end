package com.example.demo.service;

import com.example.demo.repository.*;
import com.example.demo.result.*;
import com.example.demo.model.Notice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class NoticeService implements IDGenenrator{
    @Autowired
    userRepository userRepo;
    @Autowired
    noticeRepository noticeRepo;

    public Result sendNotice(String sender,String receiver,String text){
        Notice notice=new Notice();
        notice.setId(generateID(24));
        notice.setSenderId(sender);
        notice.setReceiverId(receiver);
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
        return ResultFactory.buildSuccessResult(noticeRepo.getAllReceivedNotice(receiver));
    }

    public Result getAllSentNotice(String sender){
        return ResultFactory.buildSuccessResult(noticeRepo.getAllSentNotice(sender));
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
