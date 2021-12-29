package com.example.demo.repository;

import com.example.demo.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface adminRepository extends JpaRepository<Admin,String> {
    @Query(value = "select if((select count(*) from admin where mail=?1)>0,1,0)",nativeQuery = true)
    Integer existsByMail(String mail);

    @Query(value = "select a from Admin a where a.mail=?1")
    Admin getAdminByMail(String mail);

    @Query(value = "select if((select count(*) from admin where phone=?1)>0,1,0)",nativeQuery = true)
    Integer existsByPhone(String phone);
}
