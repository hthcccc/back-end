package com.example.demo.repository;

import com.example.demo.model.Favority;
import com.example.demo.model.FavorityId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface favorityRepository extends JpaRepository<Favority, FavorityId> {
    @Query("select f.id.goodId from Favority f where f.id.userId=?1")
    List<String> getAllFavority(String user_id);
}
