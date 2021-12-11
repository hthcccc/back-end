package com.example.demo.controller;

import com.example.demo.config.ApiGroup;
import com.example.demo.model.Comment;
import com.example.demo.service.CommentService;
import com.example.demo.controller.resultBody.*;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins="*")
@RestController("comment")
@RequestMapping("/comment")
public class commentController {
    @Autowired
    CommentService tmp;

    @GetMapping("/getText")
    @ApiGroup(group = {"comment"})
    @ApiOperation(value = "获取评论文字信息",notes = "评论id，层数")
    public String getText(String comment_id,Integer level){
        return tmp.getText(comment_id,level);
    }

    @GetMapping("/getComment")
    @ApiGroup(group = {"comment"})
    @ApiOperation(value = "获取评论信息",notes = "评论id，层数")
    public Comment getComment(String comment_id, Integer level){
        return tmp.getComment(comment_id,level);
    }

    @PostMapping("/newComment")
    @ApiGroup(group = {"comment"})
    @ApiOperation(value = "新建评论",notes = "用户id，商品id，文字信息")
    public void newComment(@RequestParam("user_id") String user_id,
                           @RequestParam("good_id") String good_id,
                           @RequestParam("text") String text){
        tmp.newComment(user_id,good_id,text);
    }

    @PostMapping("/replyComment")
    @ApiGroup(group = {"comment"})
    @ApiOperation(value = "回复评论",notes = "评论id，用户id，文字信息")
    public void replyComment(@RequestParam("comment_id") String comment_id,
                           @RequestParam("user_id") String user_id,
                           @RequestParam("text") String text){
        tmp.addComment(comment_id,user_id,text);
    }

    @PostMapping("/deleteComment")
    @ApiGroup(group = {"comment"})
    @ApiOperation(value = "删除评论",notes = "评论id，层数")
    public void deleteComment(@RequestParam("comment_id") String comment_id,
                             @RequestParam("level") Integer level){
        tmp.deleteComment(comment_id,level);
    }

    @GetMapping("/getAllByGood/{good_id}")
    @ApiGroup(group = {"good","comment"})
    @ApiOperation(value = "获取商品的所有评论（第一层）",notes = "商品id")
    public List<commentListBody> getAllByGood(@PathVariable String good_id){
        return tmp.getAllByGood(good_id);
    }

    @GetMapping("/listAllBelow/{comment_id}")
    @ApiGroup(group = {"comment"})
    @ApiOperation(value = "查看某条评论的所有回复",notes = "评论id")
    public List<commentListBody> listAllBelow(@PathVariable String comment_id){
        return tmp.listAllBelow(comment_id);
    }
}
