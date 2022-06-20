package com.example.demo.repository;

import com.example.demo.model.Address;
import com.example.demo.model.AddressId;
import com.example.demo.model.Credit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface creditRepository extends JpaRepository<Credit, String> {

    @Query("select a from Credit a where a.uid=?1")
    public List<Credit> getCreditHistory(String user_id);
}

