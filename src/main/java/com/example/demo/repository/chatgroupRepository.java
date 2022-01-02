package com.example.demo.repository;

import com.example.demo.model.Chatgroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface chatgroupRepository extends JpaRepository<Chatgroup,String> {
    @Query("select g.id from Chatgroup g where (g.user1=?1 and g.user2=?2) or (g.user1=?2 and g.user2=?1)")
    String getGroupIdByUsers(String user1,String user2);

    @Query("select distinct c.id from Chatgroup c where c.user1=?1 or c.user2=?1")
    List<String> getMyGroup(String user);
}
