package com.example.demo.repository;

import com.example.demo.model.RefundUrl;
import com.example.demo.model.RefundUrlId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface refundUrlRepository extends JpaRepository<RefundUrl, RefundUrlId> {
    @Query("select r.id.url from RefundUrl r where r.id.orderId=?1")
    List<String> getUrlsByOrder(String order_id);
}
