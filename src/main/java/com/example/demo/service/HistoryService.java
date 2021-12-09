package com.example.demo.service;

import com.example.demo.model.History;
import com.example.demo.model.HistoryId;
import com.example.demo.repository.goodRepository;
import com.example.demo.repository.historyRepository;
import com.example.demo.repository.userRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class HistoryService {
    @Autowired
    historyRepository historyRepo;
    @Autowired
    userRepository userRepo;
    @Autowired
    goodRepository goodRepo;

    /*public void browseGood(String user_id,String good_id){
        if(userRepo.existsById(user_id)&&goodRepo.existsById(good_id)){
            if(historyRepo.existsById(user_id,good_id)>0){
                //History history=historyRepo.getOneHistory(user_id,good_id);
                //history.setDate(Instant.now());
                //historyRepo.save(history);
                historyRepo.removeOneHistory(user_id,good_id);
                History history=new History();
                history.setId(new HistoryId(user_id,good_id));
                history.setDate(Instant.now());
                historyRepo.save(history);
            }else{
                History history=new History();
                history.setId(new HistoryId(user_id,good_id));
                history.setDate(Instant.now());
                historyRepo.save(history);
            }
        }
    }*/

    public List<History> getHistory(String user_id){
        return historyRepo.getAllHistory(user_id);
    }

    public void removeOneHistory(String user_id,String good_id){
        historyRepo.removeOneHistory(user_id,good_id);
    }

    public void removeAllHistory(String user_id){
        historyRepo.removeAllHistory(user_id);
    }

}
