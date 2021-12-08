package com.example.demo.controller;

import com.example.demo.config.ApiGroup;
import com.example.demo.model.Good;
import com.example.demo.service.GoodService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController("good")
@RequestMapping("/good")
public class goodController {
    @Autowired
    GoodService tmp;

    @GetMapping("/getGood/{id}")
    @ApiGroup(group = {"good"})
    @ApiOperation(value = "按照id获取商品",notes = "商品id")
    public Good getGood(@PathVariable String id)
    {
        return tmp.getById(id);
    }

    @GetMapping("/getGoodUrl/{id}")
    @ApiGroup(group = {"good"})
    @ApiOperation(value = "获取商品图片url", notes = "商品id")
    public String getGoodUrl(@PathVariable String id)
    {
        return tmp.getUrl(id);
    }

    @GetMapping("/getGoodPart/")
    @ApiGroup(group = {"good"})
    @ApiOperation(value = "获取所有商品分区")
    public List<String> getGoodPart()
    {
        return tmp.getAllPart();
    }

    @GetMapping("/getGoodByUser/{user_id}")
    @ApiGroup(group = {"good"})
    @ApiOperation(value="获取某个用户的所有商品",notes = "用户id")
    public List<Good> getGoodByUser(@PathVariable String user_id)
    {
        return tmp.getGoodByUser(user_id);
    }

    @GetMapping("/getGoodInventory/{g_id}")
    @ApiGroup(group = {"good"})
    @ApiOperation(value = "获取商品库存",notes = "商品id")
    public Integer getGoodInventory(@PathVariable String g_id){
        return tmp.getInventory(g_id);
    }

    @GetMapping("/getGoodOnShellByName/{name}")
    @ApiGroup(group = {"good"})
    @ApiOperation(value="按商品名字获取所有上架中的商品")
    public List<Good> getGoodOnShellByName(@PathVariable String name)
    {
        return tmp.getGoodByName(name);
    }

    @GetMapping("/calculateSum")
    @ApiGroup(group={"good"})
    @ApiOperation(value = "计算总价（数量*单价+运费）",notes = "商品id，商品数量")
    public Double caculateSum(String good_id,Integer num){
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
    public void setGood(@RequestParam("g_id") String g_id,
                        @RequestParam("name") String name,
                        @RequestParam("part") String part,
                        @RequestParam("inventory") Integer inventory,
                        @RequestParam("info") String info,
                        @RequestParam("price") Double price,
                        @RequestParam("freight") Double freight,
                        @Nullable @RequestParam("file") MultipartFile file){
        tmp.setGood(g_id,name,part,inventory,info,price,freight,file);
    }

    @PostMapping("/setGoodState")
    @ApiGroup(group = {"good"})
    @ApiOperation(value = "设置商品状态",notes = "商品id，商品新状态")
    public void setGoodState(@RequestParam("g_id") String g_id, @RequestParam("newstate") String newstate){
        tmp.setGoodState(g_id,newstate);
    }

    @Transactional
    @PostMapping("/setGoodInventory")
    @ApiGroup(group = {"good"})
    @ApiOperation(value = "修改商品库存",notes="商品id，新库存")
    public void setGoodInvntory(@RequestParam("g_id") String g_id, @RequestParam("num") Integer num){
        tmp.setInventory(g_id,num);
    }

    @Transactional
    @PostMapping("/setGoodPicture")
    @ApiGroup(group = {"good"})
    @ApiOperation(value = "更换/上传商品图片",notes="商品id，商品图片")
    public boolean setGoodPicture(@RequestParam("g_id") String g_id, @RequestParam("file") MultipartFile file){
        return tmp.setUrl(g_id,file);
    }

    @Transactional
    @PostMapping("/releaseGood")
    @ApiGroup(group = {"good"})
    @ApiOperation(value = "发布商品",notes = "用户id，商品名字，商品分区，商品库存，商品信息，商品价格，邮费")
    public void releaseGood(@RequestParam("u_id") String u_id,
                            @RequestParam("name") String name,
                            @RequestParam("part") String part,
                            @RequestParam("inventory") Integer inventory,
                            @RequestParam("info") String info,
                            @RequestParam("price") Double price,
                            @RequestParam("freight") Double freight,
                            @RequestParam("file") MultipartFile file){
        tmp.releaseGood(u_id,name,part,inventory,info,price,freight,file);
    }
}
