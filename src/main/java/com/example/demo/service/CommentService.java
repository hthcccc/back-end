package com.example.demo.service;

import com.example.demo.model.Comment;
import com.example.demo.model.CommentId;
import com.example.demo.repository.commentRepository;
import com.example.demo.repository.goodRepository;
import com.example.demo.repository.userRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Random;

@Service
public class CommentService implements IDGenenrator{
    @Autowired
    commentRepository commentRepo;
    @Autowired
    userRepository userRepo;
    @Autowired
    goodRepository goodRepo;

    public void newComment(String u_id,String g_id,String text){
        if(!userRepo.existsById(u_id)||!goodRepo.existsById(g_id)||(text==null)||text.isEmpty()){return;}
        Comment comment=new Comment();
        comment.setId(new CommentId(generateID(10),0));
        comment.setDate(Instant.now());
        comment.setText(text);
        comment.setGoodId(g_id);
        comment.setUserId(u_id);
        commentRepo.save(comment);
    }

    public void addComment(String c_id,String u_id,String text){
        if(!commentRepo.existsById(c_id)){return;}
        Comment comment=new Comment();
        comment.getId().setCommentId(c_id);
        comment.getId().setLevel(0);
        comment.setUserId(u_id);
        comment.setGoodId(commentRepo.getGoodIdByCommentId(c_id));
        comment.setText(text);
        comment.setDate(Instant.now());
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
            if(!commentRepo.existsById(id.toString())) return id.toString();
        }
    }
}
