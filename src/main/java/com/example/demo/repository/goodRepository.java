package com.example.demo.repository;

import com.example.demo.model.Good;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    @Transactional
    @Query("select g.inventory from Good g where g.id=?1")
    Integer getInventory(String good_id);

    @Query("select t.goodState from Good t where t.id=?1")
    String getGoodState(String good_id);

    @Transactional
    @Query(value = "select sum(good.price*?2+good.freight) from good where good_id=?1",nativeQuery = true)
    Double calculateSum(String good_id,Integer num);

    @Query(value = "select if(inventory>=?2,1,0) from good where good_id=?1",nativeQuery = true)
    Integer isEnough(String good_id,Integer num);

    @Transactional
    @Modifying
    @Query(value = "update good set inventory=?2 where good_id=?1",nativeQuery = true)
    void setInventory(String good_id,Integer num);

    @Transactional
    @Query("update Good g set g.goodState=?2 where g.id=?1")
    void setState(String good_id,String newstate);

}
