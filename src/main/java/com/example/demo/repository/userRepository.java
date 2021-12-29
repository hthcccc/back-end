package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface userRepository extends JpaRepository<User,String> {
    @Query("select u.balance from User u where u.userId=?1")
    Double getBalance(String user_id);

    @Query("select u.name from User u where u.userId=?1")
    String getName(String user_id);

    @Query("select u from User u where u.mail=?1")
    User getUserByMail(String mail);

    @Query(value = "select if((select count(*) from user where mail=?1)>0,1,0)",nativeQuery = true)
    Integer existsByMail(String mail);

    @Query(value = "select if((select count(*) from user where phone=?1)>0,1,0)",nativeQuery = true)
    Integer existsByPhone(String phone);

    @Query(value = "select * from user where user_id in (select subscribed_id from subscribe where user_id=?)",nativeQuery = true)
    List<User> getSubscribe(String user_id);

    @Transactional
    @Modifying
    @Query("update User u set u.balance=u.balance-?2 where u.userId=?1")
    void deleteBalance(String user_id,Double cost);

}
