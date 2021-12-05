package com.example.demo.repository;

import com.example.demo.model.Good;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface goodRepository  extends JpaRepository<Good,String> {
    @Transactional//开启事务
    @Query("select t from Good t where t.sellerId=?1")
    List<Good> getGoodByUser(String u_id);

    @Transactional//开启事务
    @Query("select t from Good t where t.name=?1 and t.goodState='上架中'")
    List<Good> getGoodByName(String name);
}
