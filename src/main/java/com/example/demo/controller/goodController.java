package com.example.demo.controller;

import com.example.demo.config.ApiGroup;
import com.example.demo.model.Good;
import com.example.demo.service.GoodService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/getGoodOnShellByName/{name}")
    @ApiGroup(group = {"good"})
    @ApiOperation(value="按商品名字获取所有上架中的商品")
    public List<Good> getGoodOnShellByName(@PathVariable String name)
    {
        return tmp.getGoodByName(name);
    }

    @PostMapping("/setGood")
    @ApiGroup(group = {"good"})
    @ApiOperation(value = "设置商品属性（不包括状态）",notes = "商品id，商品名称，商品分区，商品库存，商品信息，商品价格，运费")
    public void setGood(@RequestBody Good good){
        tmp.setGood(good.getId(),good.getName(),
                good.getPart(),good.getInventory(),
                good.getInfo(),good.getPrice(),good.getFreight());
    }

    @PostMapping("/setGoodState")
    @ApiGroup(group = {"good"})
    @ApiOperation(value = "设置商品状态",notes = "商品id，商品新状态")
    public void setGoodState(@RequestBody Good good){
        tmp.setGoodState(good.getId(),good.getGoodState());
    }

    @PostMapping("/releaseGood")
    @ApiGroup(group = {"good"})
    @ApiOperation(value = "发布商品",notes = "用户id，商品名字，商品分区，商品库存，商品信息，商品价格，邮费")
    public void releaseGood(@RequestBody Good good){
        tmp.releaseGood(good.getSellerId(),good.getName(),
                good.getPart(),good.getInventory(),
                good.getInfo(),good.getPrice(),good.getFreight());
    }
}
