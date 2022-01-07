package com.example.demo.service;

import com.example.demo.model.Subscribe;
import com.example.demo.model.SubscribeId;
import com.example.demo.model.User;
import com.example.demo.repository.subscribeRepository;
import com.example.demo.repository.userRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscribeService {
    @Autowired
    subscribeRepository subRepo;

    @Autowired
    userRepository userRepo;

    public List<User> getSubscribe(String user_id){
        if(userRepo.existsById(user_id)){
            return userRepo.getSubscribe(user_id);
        }else{
            return null;
        }
    }

    public void addSubscribe(String user_id,String subscribed_id){
        if(userRepo.existsById(user_id)&&userRepo.existsById(subscribed_id)){
            if(subscribed_id.equals(user_id)){return;}
            if(!subRepo.existsById(new SubscribeId(user_id,subscribed_id))) {
                SubscribeId id = new SubscribeId();
                id.setUserId(user_id);
                id.setSubscribedId(subscribed_id);
                Subscribe sub = new Subscribe();
                sub.setId(id);
                subRepo.save(sub);
            }
        }
    }

    public void removeSubscribe(String user_id,String subscribed_id){
        if(userRepo.existsById(user_id)&&userRepo.existsById(subscribed_id)){
            if(subscribed_id.equals(user_id)){return;}
            if(subRepo.existsById(new SubscribeId(user_id,subscribed_id))) {
                SubscribeId id = new SubscribeId();
                id.setUserId(user_id);
                id.setSubscribedId(subscribed_id);
                Subscribe sub = new Subscribe();
                sub.setId(id);
                subRepo.deleteById(id);
            }
        }
    }

}
