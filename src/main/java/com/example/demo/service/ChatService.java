package com.example.demo.service;

import com.example.demo.model.Chat;
import com.example.demo.model.Chatgroup;
import com.example.demo.repository.chatRepository;
import com.example.demo.repository.chatgroupRepository;
import com.example.demo.repository.userRepository;
import com.example.demo.result.Result;
import com.example.demo.result.ResultFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class ChatService{
    @Autowired
    chatRepository chatRepo;
    @Autowired
    chatgroupRepository groupRepo;
    @Autowired
    userRepository userRepo;

    public Result readChat(String user1,String user2){
        if(!userRepo.existsById(user1)||!userRepo.existsById(user2)){
            return ResultFactory.buildFailResult("用户id无效");
        }
        String groupId=groupRepo.getGroupIdByUsers(user1,user2);
        if(groupId==null||groupId.isEmpty()||groupId.equals("")||groupId.equals("null")){
            return ResultFactory.buildFailResult("暂无建立聊天组");
        }
        chatRepo.setIsRead(groupId,user1);
        List<Chat>chats = chatRepo.getChatsByGroup(groupId);
        List<Map<String,Object>> result=new ArrayList<>();
        for(Chat chat:chats){
            Map<String,Object> map=new HashMap<>();
            map.put("sender_id",chat.getSenderId());
            map.put("sender_name",userRepo.findById(chat.getSenderId()).get().getName());
            map.put("receiver_id",chat.getReceiverId());
            map.put("receiver_name",userRepo.findById(chat.getReceiverId()).get().getName());
            map.put("text",chat.getText());
            map.put("isRead",chat.getIsRead());
            map.put("chat_id",chat.getId());
            map.put("group_id",chat.getGroup_id());
            map.put("time",chat.getTime());
            result.add(map);
        }
        return ResultFactory.buildSuccessResult(result);
    }

    public Result sendChat(String sender,String receiver,String text){
        if(!userRepo.existsById(sender)||!userRepo.existsById(receiver)){
            return ResultFactory.buildFailResult("用户id无效");
        }
        String groupId=groupRepo.getGroupIdByUsers(sender,receiver);
        System.out.println("group_id="+groupId);
        if(groupId==null||groupId.isEmpty()||groupId.equals("")||groupId.equals("null")){
            //新建聊天分组
            Chatgroup group =new Chatgroup();
            group.setId(generateGroupID());
            group.setUser1(sender);
            group.setUser2(receiver);
            groupRepo.save(group);
            groupId=group.getId();
        }
        Chat chat=new Chat();
        chat.setGroup_id(groupId);
        chat.setIsRead("0");
        chat.setSenderId(sender);
        chat.setReceiverId(receiver);
        chat.setText(text);
        chat.setTime(Instant.now().plusMillis(TimeUnit.HOURS.toMillis(8)));
        chat.setId(generateChatID());
        chatRepo.save(chat);
        return ResultFactory.buildResult(200,"发送成功",null);
    }

    public Result getMyChats(String user){
        if(!userRepo.existsById(user)){
            return ResultFactory.buildFailResult("该用户不存在");
        }
        List<Map<String,Object>> result=new ArrayList<>();
        List<String> groups=groupRepo.getMyGroup(user);
        for(String group_id:groups){
            Map<String,Object> map=new HashMap<>();
            Chat chat=chatRepo.getChatsByGroup(group_id).get(0);
            String user_id=((chat.getReceiverId().equals(user))?chat.getSenderId():chat.getReceiverId());
            map.put("user_id",user_id);
            map.put("user_name",userRepo.findById(user_id).get().getName());
            map.put("group_id",group_id);
            map.put("last_time",chat.getTime());
            map.put("is_read",chat.getIsRead());
            map.put("last_text",chat.getText());
            result.add(map);
        }
        return ResultFactory.buildSuccessResult(result);
    }

    public StringBuilder tryGetID(int length) {
        StringBuilder id=new StringBuilder();
        Random rd = new SecureRandom();
        for(int i=0;i<length;i++){
            int bit = rd.nextInt(10);
            id.append(bit);
        }
        return id;
    }


    public String generateGroupID() {
        while(true)
        {
            StringBuilder id=tryGetID(32);
            if(!groupRepo.existsById(id.toString())) return id.toString();
        }
    }

    public String generateChatID() {
        while(true)
        {
            StringBuilder id=tryGetID(24);
            if(!chatRepo.existsById(id.toString())) return id.toString();
        }
    }
}
