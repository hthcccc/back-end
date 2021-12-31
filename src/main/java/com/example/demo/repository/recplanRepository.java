package com.example.demo.repository;

import com.example.demo.model.RecPlans;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface recplanRepository extends JpaRepository<RecPlans,String> {
    @Query("select u from RecPlans u")
    List<RecPlans> getAllPlans();
}
