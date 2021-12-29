package com.example.demo.repository;

import com.example.demo.model.Verification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface verificationRepository extends JpaRepository<Verification,String> {
}
