package com.example.demo.service;

import com.example.demo.model.Favority;
import com.example.demo.model.FavorityId;
import com.example.demo.repository.favorityRepository;
import com.example.demo.repository.goodRepository;
import com.example.demo.repository.userRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<String> getAllFavority(String user_id){
        return favorRepo.getAllFavority(user_id);
    }
}
