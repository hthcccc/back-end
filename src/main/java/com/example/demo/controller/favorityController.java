package com.example.demo.controller;

import com.example.demo.config.ApiGroup;
import com.example.demo.service.FavorityService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins="*")
@RestController("favority")
@RequestMapping("/favority")
public class favorityController {
    @Autowired
    FavorityService tmp;

    @GetMapping("/getAllFavourity/{user_id}")
    @ApiGroup(group = {"favority","user"})
    @ApiOperation(value = "获取用户所有收藏商品id",notes = "用户id")
    public List<String> getAllFavourity(@PathVariable String user_id){
        return tmp.getAllFavority(user_id);
    }

    @PostMapping("/addFavority")
    @ApiGroup(group = {"favority"})
    @ApiOperation(value = "增加商品到收藏夹",notes = "用户id，商品id")
    public void addFavority(String user_id,String good_id){
        tmp.addFavority(user_id,good_id);
    }
}
