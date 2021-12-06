package com.example.demo.repository;


import com.example.demo.model.TradeOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface orderRepository extends JpaRepository<TradeOrder,String> {

}
