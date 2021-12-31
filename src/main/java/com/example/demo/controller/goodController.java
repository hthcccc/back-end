package com.example.demo.controller;

import com.example.demo.config.ApiGroup;
import com.example.demo.model.Good;
import com.example.demo.service.GoodService;
import com.example.demo.utils.TokenUse;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.demo.result.*;

import java.util.List;

@CrossOrigin(origins="*")
@RestController("good")
@RequestMapping("/good")
public class goodController {
    @Autowired
    GoodService tmp;

    @GetMapping("/getGood/{good_id}")
    @ApiGroup(group = {"good"})
    @ApiOperation(value = "按照id获取商品",notes = "商品id")
    public Result getGood(@PathVariable String good_id)
    {
        return tmp.getById(good_id);
    }

    @GetMapping("/getTop10")
    @ApiGroup(group = {"good"})
    @ApiOperation(value = "获取Top10商品")
    public Result getTop10() {
        return tmp.getTop10();
    }

    @Transactional
    @PostMapping("/browseGood")
    @ApiGroup(group = {"good","history"})
    @ApiOperation(value = "查看商品详细信息",notes = "用户id，商品id")
    public Result browseGood(@RequestParam("user_id") String user_id,
                             @RequestParam("good_id") String good_id){
        return tmp.browseGood(user_id,good_id);
    }

    @GetMapping("/getGoodUrl/{good_id}")
    @ApiGroup(group = {"good"})
    @ApiOperation(value = "获取商品图片url", notes = "商品id")
    public String getGoodUrl(@PathVariable String good_id)
    {
        return tmp.getUrl(good_id);
    }

    @GetMapping("/getGoodPart/")
    @ApiGroup(group = {"good"})
    @ApiOperation(value = "获取所有商品分区")
    public Result getGoodPart()
    {
        return tmp.getAllPart();
    }

    @GetMapping("/getGoodByUser/{user_id}")
    @ApiGroup(group = {"good"})
    @ApiOperation(value="获取某个用户的所有商品",notes = "用户id")
    public Result getGoodByUser(@PathVariable String user_id)
    {
        return tmp.getGoodByUser(user_id);
    }

    @GetMapping("/getGoodInventory/{good_id}")
    @ApiGroup(group = {"good"})
    @ApiOperation(value = "获取商品库存",notes = "商品id")
    public Integer getGoodInventory(@PathVariable String good_id){
        return tmp.getInventory(good_id);
    }

    @GetMapping("/getGoodOnShellByName/{name}")
    @ApiGroup(group = {"good"})
    @ApiOperation(value="按商品名字获取所有上架中的商品",notes = "商品名字")
    public Result getGoodOnShellByName(@PathVariable String name)
    {
        return tmp.getGoodOnShellByName(name);
    }

    @GetMapping("/getGoodOnShellByPart/{part}")
    @ApiGroup(group = {"good"})
    @ApiOperation(value="按照分区获取所有上架中的商品",notes = "分区名")
    public Result getGoodOnShellByPart(@PathVariable String part)
    {
        return tmp.getGoodOnShellByPart(part);
    }

    @GetMapping("/getGoodPagedByPart")
    @ApiGroup(group = {"good"})
    @ApiOperation(value="按照分区,分页获取所有上架中的商品",notes = "分区名，页面index")
    public Result getGoodPagedByPart(String part,Integer page)
    {
        return tmp.getGoodPagedOnShellByPart(part,page);
    }

    @GetMapping("/calculateSum")
    @ApiGroup(group={"good"})
    @ApiOperation(value = "计算总价（数量*单价+运费）",notes = "商品id，商品数量")
    public Result calculateSum(String good_id,Integer num){
        return tmp.calculateSum(good_id,num);
    }

    @GetMapping("/isGoodEnough")
    @ApiGroup(group={"good"})
    @ApiOperation(value = "判断商品库存是否足够",notes = "商品id，数量")
    public boolean isGoodEnough(String good_id,Integer num){
        return tmp.isEnough(good_id,num);
    }

    @PostMapping("/setGood")
    @ApiGroup(group = {"good"})
    @ApiOperation(value = "设置商品属性（不包括状态）",notes = "商品id，商品名称，商品分区，商品库存，商品信息，商品价格，运费，图片")
    public Result setGood(@RequestParam("good_id") String good_id,
                        @RequestParam("name") String name,
                        @RequestParam("part") String part,
                        @RequestParam("inventory") Integer inventory,
                        @RequestParam("info") String info,
                        @RequestParam("price") Double price,
                        @RequestParam("freight") Double freight,
                        @RequestParam(value = "file",required = false) MultipartFile file){
        return tmp.setGood(good_id,name,part,inventory,info,price,freight,file);
    }

    @PostMapping("/setGoodState")
    @ApiGroup(group = {"good"})
    @ApiOperation(value = "设置商品状态",notes = "商品id，商品新状态")
    public void setGoodState(@RequestParam("good_id") String good_id, @RequestParam("state") String state){
        tmp.setGoodState(good_id,state);
    }

    @Transactional
    @PostMapping("/setGoodInventory")
    @ApiGroup(group = {"good"})
    @ApiOperation(value = "修改商品库存",notes="商品id，新库存")
    public void setGoodInventory(@RequestParam("good_id") String good_id, @RequestParam("num") Integer num){
        tmp.setInventory(good_id,num);
    }

    @Transactional
    @PostMapping("/setGoodPicture")
    @ApiGroup(group = {"good"})
    @ApiOperation(value = "更换/上传商品图片",notes="商品id，商品图片")
    public Result setGoodPicture(@RequestParam("good_id") String good_id, @RequestParam("file") MultipartFile file){
        return tmp.setUrl(good_id,file);
    }

    @Transactional
    @PostMapping("/releaseGood")
    @ApiGroup(group = {"good"})
    @ApiOperation(value = "发布商品",notes = "用户id，商品名字，商品分区，商品库存，商品信息，商品价格，邮费")
    public Result releaseGood(@RequestHeader("Authorization") String token,
                            @RequestParam("name") String name,
                            @RequestParam("part") String part,
                            @RequestParam("inventory") Integer inventory,
                            @RequestParam("info") String info,
                            @RequestParam("price") Double price,
                            @RequestParam("freight") Double freight,
                            @RequestParam(value = "file",required = false) MultipartFile file){
        String user_id = TokenUse.getUserID(token);
        System.out.println("用户ID"+user_id);
        return tmp.releaseGood(user_id,name,part,inventory,info,price,freight,file);
    }

    @PostMapping("/allowGoodForSale")
    @ApiGroup(group = {"good"})
    @ApiOperation(value = "批准商品上架",notes = "商品id")
    public Result allowGoodForSale(@RequestParam("good_id") String good_id){
        return tmp.allowGood(good_id);
    }
}
