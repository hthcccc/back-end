package com.example.demo.service;

import com.example.demo.model.Good;
import com.example.demo.model.TradeOrder;
import com.example.demo.model.User;
import com.example.demo.repository.goodRepository;
import com.example.demo.repository.orderRepository;
import com.example.demo.repository.userRepository;
import com.example.demo.result.Result;
import com.example.demo.result.ResultFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;
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

    public Result generateOrder(String u_id, String g_id, String buy_address,
                                String sell_address, Integer num) {
        if(userRepo.existsById(u_id)&&goodRepo.existsById(g_id)) {
            //卖家不能购买自己发布的商品
            if(goodRepo.findById(g_id).get().getSellerId().equals(u_id)){
                return ResultFactory.buildFailResult("卖家不能购买自己发布的商品");
            }
            if(!goodRepo.findById(g_id).get().getGoodState().equals("上架中")){
                return ResultFactory.buildFailResult("该商品无法购买");
            }
            TradeOrder order = new TradeOrder();
            String id = generateID(16);
            order.setId(id);
            order.setBuyerId(u_id);
            order.setGoodId(g_id);
            //判断库存
            if(goodRepo.isEnough(g_id,num)>0){
                order.setNum(num);
            }else{
                return ResultFactory.buildFailResult("大于商品库存，交易失败");
            }
            order.setPrice(goodRepo.calculateSum(g_id,num));
            order.setStartDate(Instant.now());
            order.setBuyerAddress(buy_address);
            order.setSellerAddress(sell_address);
            order.setOrderState("未支付");
            order.setIsRefunding("n");
            orderRepo.save(order);
            return ResultFactory.buildResult(200,"下单成功",null);
        }
        return ResultFactory.buildFailResult("交易失败");
    }

    public Result payOrder(String o_id){
        if(!orderRepo.existsById(o_id)){return ResultFactory.buildFailResult("订单不存在");}
        TradeOrder order= orderRepo.getOne(o_id);
        if(!order.getOrderState().equals("未支付")){return ResultFactory.buildFailResult("不可以重复支付");}
        if(!goodRepo.getGoodState(order.getGoodId()).equals("上架中")){return ResultFactory.buildFailResult("该商品已下架");}
        if(goodRepo.isEnough(order.getGoodId(),order.getNum())>0){
            if(userRepo.getBalance(order.getBuyerId())>=order.getPrice()){
                order.setOrderState("待发货");
                userRepo.deleteBalance(order.getBuyerId(),order.getPrice());
                goodRepo.setInventory(order.getGoodId(), goodRepo.getInventory(order.getGoodId())-order.getNum());
                orderRepo.save(order);
                return ResultFactory.buildResult(200,"支付成功",null);
            }else{
                return ResultFactory.buildFailResult("支付失败");
            }
        }
        else{
            return ResultFactory.buildFailResult("库存不足");
        }
    }

    public Result ackOrder(String order_id){
        if(orderRepo.existsById(order_id)){
            if(orderRepo.findById(order_id).get().getOrderState().equals("待收货")){
                setOrderState(order_id,"已收货");
                return ResultFactory.buildResult(200,"确认收货成功",null);
            }
        }
        return ResultFactory.buildFailResult("请求失败");
    }

    public Result sendPackage(String order_id){
        if(orderRepo.existsById(order_id)){
            if(orderRepo.findById(order_id).get().getOrderState().equals("待发货")){
                setOrderState(order_id,"待收货");
                return ResultFactory.buildResult(200,"成功发货",null);
            }
        }
        return ResultFactory.buildFailResult("请求失败");
    }

    public Result getRefundingOrder(String user_id){
        TradeOrder order = new TradeOrder();
        order.setIsRefunding("y");
        order.setBuyerId(user_id);
        Example<TradeOrder> orderExample=Example.of(order);
        Sort sort = Sort.sort(TradeOrder.class).descending();
        sort.getOrderFor("startDate");
        return ResultFactory.buildSuccessResult(orderRepo.findAll(orderExample));
    }

    public void setOrderState(String order_id,String newstate){
        orderRepo.setOrderState(order_id,newstate);
    }

    public Result getAllByBuyer(String buyerId){
        if(userRepo.existsById(buyerId)){
            return ResultFactory.buildSuccessResult(orderRepo.getAllByBuyer(buyerId));
        }else{
            return ResultFactory.buildFailResult("该用户不存在");
        }
    }

    public Result getAllBySeller(String sellerId){
        if(userRepo.existsById(sellerId)){
            return ResultFactory.buildSuccessResult(orderRepo.getAllBySeller(sellerId));
        }else{
            return ResultFactory.buildFailResult("不存在该用户");
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
