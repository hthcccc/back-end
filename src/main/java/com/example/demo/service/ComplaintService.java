package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.result.Result;
import com.example.demo.result.ResultFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class ComplaintService {
    @Autowired
    complaintRepository complaintRepo;
    @Autowired
    orderRepository orderRepo;
    @Autowired
    goodRepository goodRepo;
    @Autowired
    userRepository userRepo;
    @Autowired
    creditRepository creditRepo;

    public Result submitComplaint(String order_id,String text){
        if(!orderRepo.existsById(order_id)){
            return ResultFactory.buildFailResult("订单不存在");
        }
        if(complaintRepo.existsById(order_id)){
            return ResultFactory.buildFailResult("已投诉过了");
        }
        Complaint complaint=new Complaint();
        complaint.setId(order_id);
        complaint.setText(text);
        complaint.setTime(Instant.now().plusMillis(TimeUnit.HOURS.toMillis(8)));
        complaint.setIsRead("0");
        complaintRepo.save(complaint);
        return ResultFactory.buildResult(200,"已提交投诉",null);
    }

    public Result getAllUnreadComplaints(){
        Complaint complaint=new Complaint();
        complaint.setIsRead("0");
        Example<Complaint> example =Example.of(complaint);
        Sort sort= Sort.sort(Complaint.class).ascending();
        sort.getOrderFor("time");
        return ResultFactory.buildSuccessResult(complaintRepo.findAll(example,sort));
    }

    public Result readOneComplaint(String order_id){
        if(!orderRepo.existsById(order_id)){
            return ResultFactory.buildFailResult("订单不存在");
        }
        if(!complaintRepo.existsById(order_id)){
            return ResultFactory.buildFailResult("投诉不存在");
        }
        TradeOrder order=orderRepo.findById(order_id).get();
        Complaint complaint=complaintRepo.findById(order_id).get();
        Good good=goodRepo.findById(order.getGoodId()).get();
        User seller=userRepo.findById(good.getSellerId()).get();
        Map<String,Object> map=new HashMap<>();
        map.put("order_id",order_id);
        map.put("order_price",order.getPrice());
        map.put("text",complaint.getText());
        map.put("complaint_time",complaint.getTime());
        map.put("good_id",order.getGoodId());
        map.put("good_name",good.getName());
        map.put("good_url",good.getUrl());
        map.put("good_price",good.getPrice());
        map.put("good_freight",good.getFreight());
        map.put("seller_id",seller.getUserId());
        map.put("seller_name",seller.getName());
        map.put("buyer_id",order.getBuyerId());
        map.put("buyer_name",userRepo.findById(order.getBuyerId()).get().getName());
        return ResultFactory.buildResult(200,"成功获取投诉内容",map);
    }

    public Result finishComplaint(String order_id,String plan_id,String credit_name,String credit_level){
        if(!orderRepo.existsById(order_id)){
            return ResultFactory.buildFailResult("订单不存在");
        }
        if(!complaintRepo.existsById(order_id)){
            return ResultFactory.buildFailResult("投诉不存在");
        }
        TradeOrder order=orderRepo.findById(order_id).get();
        Complaint complaint=complaintRepo.findById(order_id).get();
        Good good=goodRepo.findById(order.getGoodId()).get();
        User seller=userRepo.findById(good.getSellerId()).get();
        if(!complaint.getIsRead().equals("0")){
            return ResultFactory.buildFailResult("已经处理过");
        }

        //扣信誉分
        if(plan_id.equals("1")||plan_id.equals("3")){
            seller.setCredit(seller.getCredit()*0.95);
            userRepo.save(seller);
            Credit credit=new Credit();
            credit.setId(generateCreditID());
            credit.setUid(seller.getUserId());
            credit.setName(credit_name);
            credit.setTime(Instant.now().plusMillis(TimeUnit.HOURS.toMillis(8)));
            credit.setLevel(credit_level);
            creditRepo.save(credit);
        }
        //下架商品
        if(plan_id.equals("2")||plan_id.equals("3")){
            good.setGoodState("已下架");
            goodRepo.save(good);
        }
        complaint.setIsRead("1");
        complaintRepo.save(complaint);
        return ResultFactory.buildResult(200,"处理成功",null);
    }


    public StringBuilder tryGetID(int length) {
        StringBuilder id=new StringBuilder();
        Random rd = new SecureRandom();
        for(int i=0;i<length;i++){
            int bit = rd.nextInt(10);
            id.append(bit);
        }
        return id;
    }


    public String generateCreditID() {
        while(true)
        {
            StringBuilder id=tryGetID(24);
            if(!creditRepo.existsById(id.toString())) return id.toString();
        }
    }
}
