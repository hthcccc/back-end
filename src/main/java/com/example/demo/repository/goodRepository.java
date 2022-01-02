package com.example.demo.repository;

import com.example.demo.model.Good;
import com.example.demo.controller.resultBody.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface goodRepository  extends JpaRepository<Good,String>, JpaSpecificationExecutor<Good> {
    @Transactional//开启事务
    @Query("select t from Good t where t.sellerId=?1")
    List<Good> getGoodByUser(String u_id);

    @Transactional//开启事务
    @Query("select t from Good t join Recrecord r on t.id=r.goodId where t.sellerId=?1 and r.state='推广中'")
    List<Good> getMyRecGoods(String u_id);

    @Transactional//开启事务
    @Query("select t from Good t where t.name like %?1% and t.goodState='上架中' order by t.isRec desc")
    List<Good> getGoodByName(String name);

    @Transactional//开启事务
    @Query("select t from Good t where t.part=?1 and t.goodState='上架中' order by t.isRec desc")
    List<Good> getGoodByPart(String part);

    @Query("select t from Good t where t.part=?1 and t.goodState='上架中' order by t.isRec DESC")
    Page<Good> getGoodPagedByPart(Pageable pageable, String part);

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
    @Modifying
    @Query("update Good g set g.goodState=?2 where g.id=?1")
    void setState(String good_id,String state);

    @Query(value = "select good_id from(select count(order_id) ct,good.good_id,good.is_rec from good left outer join trade_order on good.good_id=trade_order.good_id where good_state='上架中' group by(good.good_id) order by is_rec desc,ct desc limit ?1) as t(ct,good_id,is_rec)",nativeQuery = true)
    List<String> getTop(Integer num);


}
