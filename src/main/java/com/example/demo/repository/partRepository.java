package com.example.demo.repository;

import com.example.demo.model.Part;
import org.springframework.data.jpa.repository.JpaRepository;

public interface partRepository extends JpaRepository<Part,String>{

}
