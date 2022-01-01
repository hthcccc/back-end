package com.example.demo.service;

import com.example.demo.model.Good;
import com.example.demo.model.History;
import com.example.demo.model.HistoryId;
import com.example.demo.repository.goodRepository;
import com.example.demo.repository.historyRepository;
import com.example.demo.repository.userRepository;
import com.example.demo.result.Result;
import com.example.demo.result.ResultFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HistoryService {
    @Autowired
    historyRepository historyRepo;
    @Autowired
    userRepository userRepo;
    @Autowired
    goodRepository goodRepo;

    public Result getHistory(String user_id){
        List<History> histories = historyRepo.getAllHistory(user_id);
        List<Map<String,Object>> result=new ArrayList<>();
        for(History history:histories){
            Good good=goodRepo.findById(history.getId().getGoodId()).get();
            Map<String,Object> map=new HashMap<>();
            map.put("good_id",good.getId());
            map.put("good_name",good.getName());
            map.put("date",history.getDate());
            map.put("url",good.getUrl());
            map.put("price",good.getPrice());
            map.put("seller_id",good.getSellerId());
            map.put("seller_name",userRepo.findById(good.getSellerId()).get().getName());
            map.put("good_state",good.getGoodState());
            result.add(map);
        }
        return ResultFactory.buildSuccessResult(result);
    }

    public void removeOneHistory(String user_id,String good_id){
        historyRepo.removeOneHistory(user_id,good_id);
    }

    public void removeAllHistory(String user_id){
        historyRepo.removeAllHistory(user_id);
    }

}
