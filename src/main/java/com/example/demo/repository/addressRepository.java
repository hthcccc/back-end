package com.example.demo.repository;

import com.example.demo.model.Address;
import com.example.demo.model.AddressId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface addressRepository extends JpaRepository<Address, AddressId> {
//    @Query(value = "select if((select count(*) from address where user_id=?1 and address=?2)>0,1,0)",nativeQuery = true)
//    Integer ifExists(String user_id,String address);
//
//    default boolean existsByAddress(String user_id, String address){
//        if(ifExists(user_id,address)==1){
//            return true;
//        }
//        return false;
//    }

    @Query("select a.id.address from Address a where a.id.userId=?1")
    public List<String> getAllAddress(String user_id);
}
