package com.example.demo.service;

import com.example.demo.controller.resultBody.*;
import com.example.demo.model.*;
import com.example.demo.repository.goodRepository;
import com.example.demo.repository.historyRepository;
import com.example.demo.repository.partRepository;
import com.example.demo.repository.userRepository;
import com.example.demo.result.*;
import com.example.demo.result.Result;
import io.minio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;


enum goodstate{
    待审核,上架中,已下架,待整改;

    public static boolean isGoodState(String state)
    {
        for(int i=0;i<goodstate.values().length;i++){
            if(goodstate.values()[i].toString().equals(state)) return true;
        }
        return false;
    }
}


@Service
public class GoodService implements IDGenenrator{
    @Autowired
    goodRepository goodRepo;
    @Autowired
    userRepository userRepo;
    @Autowired
    partRepository partRepo;
    @Autowired
    historyRepository historyRepo;
    @Resource
    MinioClient client;

    public Result getById(String Id)
    {
        if(goodRepo.existsById(Id)) {
            Good good = goodRepo.findById(Id).get();
            Map<String,Object> result=new HashMap<>();
            result.put("good_id",good.getId());
            result.put("name",good.getName());
            result.put("url",good.getUrl());
            result.put("seller_id",good.getSellerId());
            result.put("seller_name",userRepo.getName(good.getSellerId()));
            result.put("seller_address",good.getShip_address());
            result.put("price",good.getPrice());
            result.put("freight",good.getFreight());
            result.put("info",good.getInfo());
            result.put("inventory",good.getInventory());
            result.put("good_state",good.getGoodState());
            result.put("part",good.getPart());
            result.put("isRec",good.getIsRec());
            return ResultFactory.buildSuccessResult(result);
        }
        return ResultFactory.buildFailResult("该商品不存在");
    }

    public Result getAllGoodsToBeAudited(){
        Good good=new Good();
        good.setGoodState("待审核");
        Example<Good> example=Example.of(good);
        List<Good> goods=goodRepo.findAll(example);
        return ResultFactory.buildSuccessResult(goods);
    }

    public Result browseGood(String user_id, String good_id){
        if(userRepo.existsById(user_id)&&goodRepo.existsById(good_id)){
            Good good=goodRepo.findById(good_id).get();
            //判断是否能查看商品信息
            if (good.getGoodState().equals("待审核") && !good.getSellerId().equals(user_id)) {
                return ResultFactory.buildResult(200,"该商品还未上架",null);
            }
            //更新浏览历史
            if(!good.getSellerId().equals(user_id)){
                if(historyRepo.existsById(user_id,good_id)>0){
                    History history=historyRepo.getOneHistory(user_id,good_id);
                    history.setDate(Instant.now().plusMillis(TimeUnit.HOURS.toMillis(8)));
                    historyRepo.save(history);
                }else{
                    History history=new History();
                    history.setId(new HistoryId(user_id,good_id));
                    history.setDate(Instant.now().plusMillis(TimeUnit.HOURS.toMillis(8)));
                    historyRepo.save(history);
                }
            }
            return getById(good_id);
        }else if(goodRepo.existsById(good_id)){//游客模式
            return getById(good_id);
        }
        return ResultFactory.buildFailResult("该商品不存在");
    }

    public Result getGoodByUser(String user_id){
        if(userRepo.existsById(user_id)) {
            return ResultFactory.buildSuccessResult(goodRepo.getGoodByUser(user_id));
        }
        return ResultFactory.buildFailResult("该用户不存在");
    }

    public Result getGoodOnShellByPart(String part){
        if(partRepo.existsById(part)){
            List<Good> goods=goodRepo.getGoodByPart(part);
            List<Map<String,Object>> result=new ArrayList<>();
            for(Good good:goods){
                Map<String,Object> map=new HashMap<>();
                map.put("good_id",good.getId());
                map.put("good_name",good.getName());
                map.put("good_url",good.getUrl());
                map.put("part",good.getPart());
                map.put("info",good.getInfo());
                map.put("seller_id",good.getSellerId());
                map.put("seller_name",userRepo.findById(good.getSellerId()).get().getName());
                map.put("price",good.getPrice());
                map.put("good_state",good.getGoodState());
                map.put("freight",good.getFreight());
                map.put("ship_address",good.getShip_address());
                map.put("inventory",good.getInventory());
                map.put("is_rec",good.getIsRec());
                result.add(map);
            }
            return ResultFactory.buildSuccessResult(result);
        }
        return ResultFactory.buildFailResult("不存在该分区");
    }

    public Result getGoodPagedOnShellByPart(String part,Integer page){
        if(partRepo.existsById(part)){
            //Sort sort=new Sort(Sort.Direction.ASC,"good_id");
            //return goodRepo.findAll();
            Pageable pageable=PageRequest.of(page,5,Sort.Direction.DESC,"id");
            //return goodRepo.getGoodPagedByPart(part);
            return ResultFactory.buildSuccessResult(goodRepo.getGoodPagedByPart(pageable,part));
        }
        return ResultFactory.buildFailResult("不存在该分区");
    }

    public Result getAllPart() {
        List<Part> partList=partRepo.findAll();
        return ResultFactory.buildSuccessResult(partList);
    }

    public Result releaseGood(String u_id,String name,
                            String part,Integer inventory,String info,
                            String addr,Double prize,Double freight,MultipartFile file)
    {
        if(userRepo.existsById(u_id)) {
            Good good=new Good();
            good.setId(generateID(16));
            good.setName(name);
            good.setSellerId(u_id);
            good.setPart(part);
            good.setInventory(inventory);
            good.setInfo(info);
            good.setPrice(prize);
            good.setFreight(freight);
            good.setGoodState("待审核");
            good.setShip_address(addr);
            good.setIsRec("0");
            goodRepo.save(good);
            setUrl(good.getId(),file);
            return ResultFactory.buildResult(200,"发布成功",null);
        }
        return ResultFactory.buildFailResult("不存在该商品");
    }

    public Result findFilteredGoods(String seller_id,String name,String part,String state){
        if(!userRepo.existsById(seller_id)){
            return ResultFactory.buildFailResult("请输入正确的用户id");
        }
        Good good =new Good();
        ExampleMatcher exampleMatcher=ExampleMatcher.matching();
        good.setSellerId(seller_id);
        exampleMatcher=exampleMatcher.withMatcher("sellerId",ExampleMatcher.GenericPropertyMatchers.exact());
        if(name!=null&&!name.isEmpty()&&!name.equals("")){
            good.setName(name);
            exampleMatcher=exampleMatcher.withMatcher("name",ExampleMatcher.GenericPropertyMatchers.contains());
        }
        if(part!=null&&!part.isEmpty()&&!part.equals("")&&partRepo.existsById(part)){
            good.setPart(part);
            exampleMatcher=exampleMatcher.withMatcher("part",ExampleMatcher.GenericPropertyMatchers.exact());
        }
        if(state!=null&&!state.isEmpty()&&!state.equals("")&&goodstate.isGoodState(state)){
            good.setGoodState(state);
            exampleMatcher=exampleMatcher.withMatcher("goodState",ExampleMatcher.GenericPropertyMatchers.exact());
        }
        Example<Good> example = Example.of(good,exampleMatcher);
        Sort sort = Sort.sort(Good.class).descending();
        sort.getOrderFor("isRec");
        return ResultFactory.buildSuccessResult(goodRepo.findAll(example,sort));
    }

    public Result setGood(String g_id, String name,
                          String part, Integer inventory,String info,
                          String addr,Double prize, Double freight, MultipartFile file)
    {
        if(goodRepo.existsById(g_id)) {
            Good good=goodRepo.findById(g_id).get();
            if(good.getGoodState().equals("已下架")){
                return ResultFactory.buildFailResult("商品已下架");
            }
            if(good.getGoodState().equals("待审核")){
                return ResultFactory.buildFailResult("商品待审核");
            }
            if(!name.isEmpty()&&!name.equals("")){good.setName(name);}
            if(!part.isEmpty()&&!part.equals("")){good.setPart(part);}
            if(inventory>=0){good.setInventory(inventory);}
            if(!info.isEmpty()&&!info.equals("")){good.setInfo(info);}
            if(prize>0){good.setPrice(prize);}
            if(freight>=0){good.setFreight(freight);}
            if(!addr.isEmpty()&&!addr.equals("")){good.setShip_address(addr);}
            good.setGoodState("待审核");
            goodRepo.save(good);
            if(!(file==null)&&!file.isEmpty()){this.setUrl(g_id,file);}
            return ResultFactory.buildResult(200,"修改商品成功，需要重新审核",null);
        }
        return ResultFactory.buildFailResult("不存在该商品");
    }

    public void setGoodState(String g_id,String newState)
    {
        if(goodRepo.existsById(g_id))
        {
            Good good = goodRepo.findById(g_id).get();
            if(goodstate.isGoodState(newState)) {
                good.setGoodState(newState);
                goodRepo.save(good);
            }
        }
    }

    public void setInventory(String g_id,Integer num){
        goodRepo.setInventory(g_id,num);
    }

    public Result getGoodOnShellByName(String name)
    {
        List<Good> goods=goodRepo.getGoodByName(name);
        List<Map<String,Object>> result=new ArrayList<>();
        for(Good good:goods){
            Map<String,Object> map=new HashMap<>();
            map.put("good_id",good.getId());
            map.put("good_name",good.getName());
            map.put("good_url",good.getUrl());
            map.put("part",good.getPart());
            map.put("info",good.getInfo());
            map.put("seller_id",good.getSellerId());
            map.put("seller_name",userRepo.findById(good.getSellerId()).get().getName());
            map.put("price",good.getPrice());
            map.put("good_state",good.getGoodState());
            map.put("freight",good.getFreight());
            map.put("ship_address",good.getShip_address());
            map.put("inventory",good.getInventory());
            map.put("is_rec",good.getIsRec());
            result.add(map);
        }
        return ResultFactory.buildSuccessResult(result);
    }

    public String getUrl(String g_id)
    {
        if(goodRepo.existsById(g_id)){
            return goodRepo.findById(g_id).get().getUrl();
        }
        else{
            return null;
        }
    }


    public Result setUrl(String g_id, MultipartFile file){
        if(goodRepo.existsById(g_id)){
            try {
                Good good= goodRepo.findById(g_id).get();
                if(!(good.getUrl()==null)&&!good.getUrl().equals(url+"default.jpeg")){
                    //删除minio的图片文件
                    client.removeObject(RemoveObjectArgs.builder()
                            .bucket("project")
                            .object(good.getUrl().replaceFirst(url,""))
                            .build());
                }
                //上传照片到minio
                if(!(file==null)&&!file.isEmpty()) {
                    client.putObject(
                            PutObjectArgs.builder()
                                    .bucket("project")
                                    .object(file.getOriginalFilename())
                                    .contentType(file.getContentType())
                                    .stream(file.getInputStream(),file.getSize(),-1)
                                    .build());

                    good.setUrl(url + file.getOriginalFilename());
                    goodRepo.save(good);
                    return ResultFactory.buildSuccessResult(null);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("上传失败");
                return ResultFactory.buildFailResult("图片上传失败");
            }
        }
        return ResultFactory.buildFailResult("该商品不存在");
    }

    public Result calculateSum(String g_id, int num){
        if(goodRepo.existsById(g_id)){
            return ResultFactory.buildSuccessResult(goodRepo.calculateSum(g_id,num));
        }
        return ResultFactory.buildFailResult("不存在该商品");
    }

    public Integer getInventory(String good_id){
        if(goodRepo.existsById(good_id)){
            return goodRepo.getInventory(good_id);
        }
        return 0;
    }

    public boolean isEnough(String good_id,Integer num){
        if(goodRepo.existsById(good_id)) {
            if(goodRepo.isEnough(good_id,num)>0){
                return true;
            }else{
                return false;
            }
        }
        return false;
    }

    public Result allowGood(String good_id,String plan_id){
        if(goodRepo.existsById(good_id)){
            Good good=goodRepo.findById(good_id).get();
            if(good.getGoodState().equals("待审核")){
                if(plan_id.equals("0")){
                    goodRepo.setState(good_id, "下架中");
                    return ResultFactory.buildResult(201, "商品下架", null);
                }else if(plan_id.equals("1")) {
                    goodRepo.setState(good_id, "上架中");
                    return ResultFactory.buildResult(200, "批准上架", null);
                }else{
                    goodRepo.setState(good_id, "待整改");
                    return ResultFactory.buildResult(202, "还需整改", null);
                }
            }else{
                return ResultFactory.buildFailResult("商品未提交审核");
            }
        }
        return ResultFactory.buildFailResult("不存在该商品");
    }

    public Result getTopX(Integer num){
        List<String> ids=goodRepo.getTop(num);
        return ResultFactory.buildSuccessResult(getGoodsByIds(ids));
    }

    public Result getGoodsByIds(List<String> idList){
        List<Good> result=new ArrayList<Good>();
        for(String id: idList){
            Good good=goodRepo.findById(id).get();
            result.add(good);
        }
        return ResultFactory.buildSuccessResult(result);
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
            if(!goodRepo.existsById(id.toString())) return id.toString();
        }
    }

    final public static String url = "http://116.62.208.68:9000/project/";
}
