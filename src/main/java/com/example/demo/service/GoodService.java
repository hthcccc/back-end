package com.example.demo.service;

import com.example.demo.model.Good;
import com.example.demo.model.Part;
import com.example.demo.repository.goodRepository;
import com.example.demo.repository.partRepository;
import com.example.demo.repository.userRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
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

    public Good getById(String Id)
    {
        Optional<Good> t=goodRepo.findById(Id);
        return t.get();
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
                        String info,Double prize,Double freight)
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
            String pict_url=url+good.getId()+good.getUrl().substring(good.getUrl().lastIndexOf("."));
            good.setUrl(pict_url);
            goodRepo.save(good);
        }
    }

    public void setGood(String g_id,String name,
                            String part,Integer inventory,
                            String info,Double prize,Double freight)
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

    public Double getSum(String g_id,int num){
        if(goodRepo.existsById(g_id)){
            Good good=goodRepo.findById(g_id).get();
            return good.getPrice()*num+good.getFreight();
        }
        return 0.0;
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

    final public static String url = "116.62.208.68:9000/project/";
}
