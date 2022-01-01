package com.example.demo.repository;

import com.example.demo.model.Recrecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface recrecordRepository extends JpaRepository<Recrecord,String> {
    @Query("select r from Recrecord r where r.goodId=?1 and r.state='推广中'")
    Recrecord getCurrentByGoodId(String good_id);

    @Query("select r from Recrecord r where r.goodId=?1 order by r.startTime DESC ")
    List<Recrecord> getAllRecordsByGoodID(String good_id);

    @Query("select r from Recrecord r where r.goodId=?1 and r.state='未支付'")
    Recrecord getUnpaidRec(String good_id);
}
