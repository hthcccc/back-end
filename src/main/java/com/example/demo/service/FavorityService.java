package com.example.demo.service;

import com.example.demo.model.Favority;
import com.example.demo.model.FavorityId;
import com.example.demo.model.Good;
import com.example.demo.repository.favorityRepository;
import com.example.demo.repository.goodRepository;
import com.example.demo.repository.userRepository;
import com.example.demo.result.Result;
import com.example.demo.result.ResultFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FavorityService {
    @Autowired
    favorityRepository favorRepo;
    @Autowired
    userRepository userRepo;
    @Autowired
    goodRepository goodRepo;

    public void addFavority(String user_id,String good_id){
        if(userRepo.existsById(user_id)&&goodRepo.existsById(good_id)){
            FavorityId id=new FavorityId(user_id,good_id);
            if(!favorRepo.existsById(id)){
                favorRepo.save(new Favority(id));
            }
        }
    }

    public Result getAllFavority(String user_id){
        List<String> favorities = favorRepo.getAllFavority(user_id);
        List<Map<String,Object>> result=new ArrayList<>();
        for(String good_id:favorities){
            Good good=goodRepo.findById(good_id).get();
            Map<String,Object> map=new HashMap<>();
            map.put("good_id",good_id);
            map.put("good_name",good.getName());
            map.put("url",good.getUrl());
            map.put("price",good.getPrice());
            map.put("seller_id",good.getSellerId());
            map.put("seller_name",userRepo.findById(good.getSellerId()).get().getName());
            map.put("good_state",good.getGoodState());
            result.add(map);
        }
        return ResultFactory.buildSuccessResult(result);
    }
}
