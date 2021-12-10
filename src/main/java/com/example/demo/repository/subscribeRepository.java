package com.example.demo.repository;

import com.example.demo.model.Subscribe;
import com.example.demo.model.SubscribeId;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface subscribeRepository extends JpaRepository<Subscribe, SubscribeId> {

//    @Query(value = "select * from user where user_id in (select subscribed_id from subscribe where user_id=?)",nativeQuery = true)
//    List<User> getSubscribe(String user_id);
}
