package com.example.demo.repository;

import com.example.demo.model.*;
import com.example.demo.controller.resultBody.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


public interface commentRepository extends JpaRepository<Comment, CommentId> {
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

    @Query(value="select * from comment where comment_id=?1 order by level",nativeQuery = true)
    public List<Comment> getAllBehind(String comment_id);

    @Transactional
    @Query(value="select if((select count(*) from comment where comment_id=?1)>0,1,0)",nativeQuery = true)
    Integer ifExistsByCommentID(String comment_id);

    @Query(value = "select comment_id,name,text,date from comment natural join good where good_id=?1 and level=0 order by date desc" ,nativeQuery = true)
    List<commentListBody> listAllbyGood(String good_id);

    @Query("select c from Comment c,Good g where g.id=c.goodId and c.goodId=?1 and c.id.level=0 order by c.date desc")
    List<Comment> getAllbyGood(String good_id);

    @Transactional
    @Modifying()
    @Query(value = "update comment set level=level-1 where comment_id =?1 and level>?2",nativeQuery = true)
    void retrieveLevel(String comment_id,Integer level);

    @Transactional
    @Modifying
    @Query("delete from Comment c where c.id.commentId=?1")
    void removeComment(String comment_id);

//    @Transactional
//    @Modifying
//    @Query(value = "insert into comment(comment_id,user_id,good_id,text,date,level) values(?1,?2,?3,?4,?5,?6)",nativeQuery = true)
//    Void newComment(String comment_id, String user_id, String good_id, String text, Instant date,Integer level);
}
