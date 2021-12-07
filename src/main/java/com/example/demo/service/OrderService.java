package com.example.demo.service;

import com.example.demo.model.Good;
import com.example.demo.model.TradeOrder;
import com.example.demo.model.User;
import com.example.demo.repository.goodRepository;
import com.example.demo.repository.orderRepository;
import com.example.demo.repository.userRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Optional;
import java.util.Random;

@Service
public class OrderService implements IDGenenrator{

    @Autowired
    orderRepository orderRepo;

    @Autowired
    goodRepository goodRepo;

    @Autowired
    userRepository userRepo;

    public String generateOrder(String u_id,String g_id,String buy_address,
                                String sell_address,Integer num) {
        if(userRepo.existsById(u_id)&&goodRepo.existsById(g_id)) {
            TradeOrder order = new TradeOrder();
            String id = generateID(16);
            order.setId(id);
            order.setBuyerId(u_id);
            order.setGoodId(g_id);
            Good good=goodRepo.findById(g_id).get();
            if(good.getInventory()>=num){
                order.setNum(num);
            }else{
                return null;
            }
            order.setPrice(good.getPrice()*num+good.getFreight());
            order.setStartDate(Instant.now());
            order.setBuyerAddress(buy_address);
            order.setSellerAddress(sell_address);
            order.setOrderState("未支付");
            order.setIsRefunding("n");
            orderRepo.save(order);
            return id;
        }
        return null;
    }

    public boolean payOrder(String o_id){
        if(!orderRepo.existsById(o_id)){return false;}
        TradeOrder order= orderRepo.getOne(o_id);
        if(!order.getOrderState().equals("未支付")){return false;}
        User user=userRepo.getOne(order.getBuyerId());
        Good good=goodRepo.getOne(order.getGoodId());
        if(!good.getGoodState().equals("上架中")){return false;}
        if(order.getNum()<=good.getInventory()){
            if(user.getBalance()>=order.getPrice()){
                order.setOrderState("待发货");
                user.setBalance(user.getBalance()-order.getPrice());
                good.setInventory(good.getInventory()-order.getNum());
                orderRepo.save(order);
                userRepo.save(user);
                orderRepo.save(order);
                return true;
            }else{
                return false;
            }
        }
        else{
            return false;
        }
    }

    public void refund(String o_id){
        if(!orderRepo.existsById(o_id)){return;}
        TradeOrder order=orderRepo.getOne(o_id);
        if(!order.getOrderState().equals("待发货") && !order.getOrderState().equals("待收货")){return;}
        if(order.getIsRefunding().equals('y')){return;}
        order.setIsRefunding("y");
        orderRepo.save(order);
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
            if(!orderRepo.existsById(id.toString())) return id.toString();
        }
    }
}
