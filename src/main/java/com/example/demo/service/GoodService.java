package com.example.demo.service;

import com.example.demo.model.Good;
import com.example.demo.model.History;
import com.example.demo.model.HistoryId;
import com.example.demo.model.Part;
import com.example.demo.repository.goodRepository;
import com.example.demo.repository.historyRepository;
import com.example.demo.repository.partRepository;
import com.example.demo.repository.userRepository;
import io.minio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;



enum goodstate{
    待审核,上架中,已下架,缺货;

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

    public Good getById(String Id)
    {
        if(goodRepo.existsById(Id)) {
            Optional<Good> t = goodRepo.findById(Id);
            return t.get();
        }
        return null;
    }

    public Good browseGood(String user_id,String good_id){
        if(userRepo.existsById(user_id)&&goodRepo.existsById(good_id)){
            Good good=goodRepo.findById(good_id).get();
            //判断是否能查看商品信息
            if (good.getGoodState().equals("待审核") && !good.getSellerId().equals(user_id)) {
                return null;
            }

            //更新浏览历史
            if(!good.getSellerId().equals(user_id)){
                if(historyRepo.existsById(user_id,good_id)>0){
                    History history=historyRepo.getOneHistory(user_id,good_id);
                    history.setDate(Instant.now());
                    historyRepo.save(history);
                }else{
                    History history=new History();
                    history.setId(new HistoryId(user_id,good_id));
                    history.setDate(Instant.now());
                    historyRepo.save(history);
                }
            }

            return good;
        }else if(goodRepo.existsById(good_id)){//游客模式
            return goodRepo.getOne(good_id);
        }
        return null;
    }

    public List<Good> getGoodByUser(String user_id){
        //goodRepo
        return goodRepo.getGoodByUser(user_id);
    }

    public List<String> getAllPart() {
        List<Part> partList=partRepo.findAll();
        List<String> result = new ArrayList<String>();
        for(Part p : partList){
            result.add(p.getPart());
        }
        return result;
        //return partlist;
    }

    public void releaseGood(String u_id,String name,
                            String part,Integer inventory,
                        String info,Double prize,Double freight,MultipartFile file)
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
            goodRepo.save(good);
            setUrl(good.getId(),file);
        }
    }

    public void setGood(String g_id,String name,
                            String part,Integer inventory,
                            String info,Double prize,Double freight,MultipartFile file)
    {
        if(goodRepo.existsById(g_id)) {
            Good good=goodRepo.findById(g_id).get();
            if(!name.isEmpty()){good.setName(name);}
            if(!part.isEmpty()){good.setPart(part);}
            if(inventory>=0){good.setInventory(inventory);}
            if(!info.isEmpty()){good.setInfo(info);}
            if(prize>0){good.setPrice(prize);}
            if(freight>=0){good.setFreight(freight);}
            goodRepo.save(good);
            if(!(file==null)&&!file.isEmpty()){this.setUrl(g_id,file);}
        }
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

    public List<Good> getGoodByName(String name)
    {
        return goodRepo.getGoodByName(name);
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

    public boolean setUrl(String g_id,MultipartFile file){
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
                    return true;
                }
            } catch (Exception e) {
                System.out.println("上传失败");
                return false;
            }
        }
        return false;
    }

    public Double calculateSum(String g_id,int num){
        if(goodRepo.existsById(g_id)){
            return goodRepo.calculateSum(g_id,num);
        }
        return 0.0;
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
