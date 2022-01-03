package com.example.demo.service;

import com.example.demo.controller.resultBody.*;
import com.example.demo.model.Comment;
import com.example.demo.model.CommentId;
import com.example.demo.repository.commentRepository;
import com.example.demo.repository.goodRepository;
import com.example.demo.repository.userRepository;
import com.example.demo.result.Result;
import com.example.demo.result.ResultFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;

@Service
public class CommentService implements IDGenenrator{
    @Autowired
    commentRepository commentRepo;
    @Autowired
    userRepository userRepo;
    @Autowired
    goodRepository goodRepo;

//    public String getText(String comment_id,Integer level){
//        return commentRepo.getText(comment_id,level);
//    }

    public Result getComment(String comment_id,Integer level) {
        if(!commentRepo.existsById(new CommentId(comment_id,level))){
            return ResultFactory.buildFailResult("不存在该留言");
        }
        Comment comment = commentRepo.getComment(comment_id,level);
        Map<String,Object> result=new HashMap<>();
        result.put("comment_id",comment.getId().getCommentId());
        result.put("comment_level",comment.getId().getLevel());
        result.put("date",comment.getDate());
        result.put("text",comment.getText());
        result.put("user_id",comment.getUserId());
        result.put("user_name",userRepo.findById(comment.getUserId()).get().getName());
        return ResultFactory.buildSuccessResult(result);
    }

    public Result newComment(String u_id,String g_id,String text){
        if(!userRepo.existsById(u_id)||!goodRepo.existsById(g_id)||(text==null)||text.isEmpty()){
            return ResultFactory.buildFailResult("传入参数不存在");
        }
        Comment comment=new Comment();
        comment.setId(generateID(10),0);
        //CommentId id=new CommentId();
        //id.setCommentId(generateID(10));
        //id.setLevel(0);
        //comment.setId(id);
        comment.setDate(Instant.now());
        comment.setText(text);
        comment.setGoodId(g_id);
        comment.setUserId(u_id);
        commentRepo.save(comment);
        return ResultFactory.buildResult(200,"新建评论成功",null);
        //commentRepo.newComment(generateID(10),u_id,g_id,
        //        text,Instant.now(),0);
    }

    public Result addComment(String c_id,String u_id,String text){
        if(commentRepo.ifExistsByCommentID(c_id)==0){return ResultFactory.buildFailResult("不存在该留言");}
        Comment comment=new Comment();
        comment.setId(c_id,commentRepo.getCurrentLevel(c_id)+1);
        comment.setUserId(u_id);
        comment.setGoodId(commentRepo.getGoodIdByCommentId(c_id));
        comment.setText(text);
        comment.setDate(Instant.now());
//        commentRepo.newComment(c_id,u_id,commentRepo.getGoodIdByCommentId(c_id),
//                text,Instant.now(),commentRepo.getCurrentLevel(c_id)+1);
        commentRepo.save(comment);
        return ResultFactory.buildResult(200,"回复成功",null);
    }

    public Result getAllByGood(String good_id){
        if(goodRepo.existsById(good_id)) {
            List<Comment> commentList = commentRepo.getAllbyGood(good_id);
            List<Map<String,Object>> result = new ArrayList<>();
            for (Comment comment : commentList) {
                Map<String,Object> map=new HashMap<>();
                map.put("comment_id",comment.getId().getCommentId());
                map.put("time",comment.getDate());
                map.put("text",comment.getText());
                map.put("user_id",comment.getUserId());
                map.put("user_name",userRepo.findById(comment.getUserId()).get().getName());
                result.add(map);
            }
            return ResultFactory.buildSuccessResult(result);
        }
        return ResultFactory.buildFailResult("商品不存在");
    }

    public Result listAllBelow(String comment_id){
        if(commentRepo.ifExistsByCommentID(comment_id)==1){
            List<Map<String,Object>> result=new ArrayList<>();
            List<Comment> commentList = commentRepo.getAllBehind(comment_id);
            for(Comment comment:commentList){
                Map<String,Object> map=new HashMap<>();
                map.put("text",comment.getText());
                map.put("date",comment.getDate());
                map.put("user_id",comment.getUserId());
                map.put("user_name",userRepo.findById(comment.getUserId()).get().getName());
                map.put("comment_id",comment.getId().getCommentId());
                map.put("comment_level",comment.getId().getLevel());
                result.add(map);
            }
            return ResultFactory.buildSuccessResult(result);
        }
        return ResultFactory.buildFailResult("评论不存在");
    }

    public Result deleteComment(String comment_id,Integer level){
        if(commentRepo.existsById(new CommentId(comment_id,level))){
            if(level>0) {
                commentRepo.delete(commentRepo.findById(new CommentId(comment_id, level)).get());
                commentRepo.retrieveLevel(comment_id, level);
            }else{
                commentRepo.removeComment(comment_id);
            }
            return ResultFactory.buildResult(200,"删除成功",null);
        }
        return ResultFactory.buildFailResult("不存在该留言");
    }

    @Override
    public StringBuilder tryGetID(int length) {
        StringBuilder id=new StringBuilder();
        Random rd = new SecureRandom();
        for(int i=0;i<length;i++){
            int bit = rd.nextInt(10);
            id.append(String.valueOf(bit));
        }
        return id;
    }

    @Override
    public String generateID(int length) {
        while(true)
        {
            StringBuilder id=tryGetID(length);
            if(commentRepo.ifExistsByCommentID(id.toString())==0) return id.toString();
        }
    }
}
