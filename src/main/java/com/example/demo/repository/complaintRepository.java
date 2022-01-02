package com.example.demo.repository;

import com.example.demo.model.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;

public interface complaintRepository extends JpaRepository<Complaint,String> {
}
