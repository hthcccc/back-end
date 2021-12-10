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
import java.util.List;
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
            //卖家不能购买自己发布的商品
            if(goodRepo.findById(g_id).get().getSellerId().equals(u_id)){ return null;}
            TradeOrder order = new TradeOrder();
            String id = generateID(16);
            order.setId(id);
            order.setBuyerId(u_id);
            order.setGoodId(g_id);
            //判断库存
            if(goodRepo.isEnough(g_id,num)>0){
                order.setNum(num);
            }else{
                return null;
            }
            order.setPrice(goodRepo.calculateSum(g_id,num));
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
        if(!goodRepo.getGoodState(order.getGoodId()).equals("上架中")){return false;}
        if(goodRepo.isEnough(order.getGoodId(),order.getNum())>0){
            if(userRepo.getBalance(order.getBuyerId())>=order.getPrice()){
                order.setOrderState("待发货");
                userRepo.deleteBalance(order.getBuyerId(),order.getPrice());
                goodRepo.setInventory(order.getGoodId(), goodRepo.getInventory(order.getGoodId())-order.getNum());
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

    public void ackOrder(String order_id){
        if(orderRepo.existsById(order_id)){
            if(orderRepo.findById(order_id).get().getOrderState().equals("待收货")){
                setOrderState(order_id,"已收货");
            }
        }
    }

    public void sendPackage(String order_id){
        if(orderRepo.existsById(order_id)){
            if(orderRepo.findById(order_id).get().getOrderState().equals("待发货")){
                setOrderState(order_id,"待收货");
            }
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

    public void setOrderState(String order_id,String newstate){
        orderRepo.setOrderState(order_id,newstate);
    }

    public List<TradeOrder> getAllByBuyer(String buyerId){
        if(userRepo.existsById(buyerId)){
            return orderRepo.getAllByBuyer(buyerId);
        }else{
            return null;
        }
    }

    public List<String> getAllBySeller(String sellerId){
        if(userRepo.existsById(sellerId)){
            return orderRepo.getAllBySeller(sellerId);
        }else{
            return null;
        }
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
