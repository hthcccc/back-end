package com.example.demo.repository;

import com.example.demo.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

public interface commentRepository extends JpaRepository<Comment,String> {
    @Transactional
    @Query(value = "select max(level) from comment where comment_id=?1",nativeQuery = true)
    Integer getCurrentLevel(String commentId);

    @Transactional
    @Query(value = "select good_id from comment where comment_id=?1 and level=0",nativeQuery = true)
    String getGoodIdByCommentId(String CommentId);

    @Query(value="select text from comment where comment_id=?1 and level=?2",nativeQuery = true)
    public String getText(String comment_id,Integer level);

    @Query(value="select * from comment where comment_id=?1 and level=?2",nativeQuery = true)
    public Comment getComment(String comment_id,Integer level);

    @Transactional
    @Query(value="select if((select count(*) from comment where comment_id=?1)>0,1,0)",nativeQuery = true)
    Integer ifExistsByCommentID(String comment_id);

//    @Transactional
//    @Modifying
//    @Query(value = "insert into comment(comment_id,user_id,good_id,text,date,level) values(?1,?2,?3,?4,?5,?6)",nativeQuery = true)
//    Void newComment(String comment_id, String user_id, String good_id, String text, Instant date,Integer level);
}
