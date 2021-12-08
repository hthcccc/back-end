package com.example.demo.repository;

import com.example.demo.model.Refund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface refundRepository extends JpaRepository<Refund,String> {
    @Transactional
    @Query(value = "select refund.* from refund natural join trade_order where buyer_id=?1 order by start_date desc",nativeQuery = true)
    List<Refund> getAllByBuyer(String buyerId);
}
