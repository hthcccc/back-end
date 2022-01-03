package com.example.demo.repository;

import com.example.demo.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface reportRepository extends JpaRepository<Report,String> {
    @Query("select r from Report r where r.userId=?1 order by r.reportState asc,r.date desc")
    List<Report> getAllIdByUser(String user_id);
}
