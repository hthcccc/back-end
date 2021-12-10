package com.example.demo.repository;


import com.example.demo.model.TradeOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface orderRepository extends JpaRepository<TradeOrder,String> {
    @Modifying
    @Transactional
    @Query(value = "update TradeOrder o set o.orderState=?2 where o.id=?1")
    void setOrderState(String order_id,String newstate);

    @Transactional
    @Query("select t from TradeOrder t where t.buyerId=?1 order by t.startDate desc")
    List<TradeOrder> getAllByBuyer(String buyerId);

    @Transactional
    @Query(value = "select trade_order.* from trade_order join good where trade_order.good_id=good.good_id and seller_id=?1 order by start_date desc;",nativeQuery = true)
    List<String> getAllBySeller(String sellerId);

}