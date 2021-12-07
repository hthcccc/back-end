package com.example.demo.repository;

import com.example.demo.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface commentRepository extends JpaRepository<Comment,String> {
    @Transactional
    @Query("select max(t.id.level) from Comment t where t.id.commentId=?1")
    Integer getCurrentLevel(String commentId);

    @Transactional
    @Query("select t.goodId from Comment t where t.id.commentId=?1 and t.id.level=0")
    String getGoodIdByCommentId(String CommentId);
}
