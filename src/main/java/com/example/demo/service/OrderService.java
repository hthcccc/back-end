package com.example.demo.service;

import com.example.demo.model.Good;
import com.example.demo.model.TradeOrder;
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
