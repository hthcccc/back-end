package com.example.demo.repository;

import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

public interface userRepository extends JpaRepository<User,String> {
    @Query("select u.balance from User u where u.userId=?1")
    Double getBalance(String user_id);

    @Transactional
    @Modifying
    @Query("update User u set u.balance=u.balance-?2 where u.userId=?1")
    void deleteBalance(String user_id,Double cost);

}
