package com.example.demo.repository;

import com.example.demo.model.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface noticeRepository extends JpaRepository<Notice, String> {
    @Query("select n from Notice n where n.receiver_id=?1 order by n.isread asc,n.date desc")
    List<Notice> getAllReceivedNotice(String receiver);

    @Query("select n from Notice n where n.sender_id=?1 order by n.isread asc,n.date desc")
    List<Notice> getAllSentNotice(String sender);
}
