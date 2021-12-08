package com.example.demo.repository;

import com.example.demo.model.Subscribe;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface subscribeRepository extends JpaRepository<Subscribe,String> {
    @Query(value = "select if((select count(*) from subscribe where user_id=?1 and subscribed_id=?2)>0,1,0)",nativeQuery = true)
    Integer hasSubscribed(String user_id,String subscribed_id);

//    @Query(value = "select * from user where user_id in (select subscribed_id from subscribe where user_id=?)",nativeQuery = true)
//    List<User> getSubscribe(String user_id);
}
