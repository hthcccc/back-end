package com.example.demo.service;

import com.example.demo.model.Refund;
import com.example.demo.model.TradeOrder;
import com.example.demo.repository.orderRepository;
import com.example.demo.repository.refundRepository;
import com.example.demo.repository.userRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

@Service
public class RefundService{
    @Autowired
    refundRepository refundRepo;

    @Autowired
    orderRepository orderRepo;

    public boolean refund(String o_id,String text){
        if(!orderRepo.existsById(o_id)){return false;}
        TradeOrder order=orderRepo.getOne(o_id);
        if(order.getIsRefunding().equals("y")){return false;}
        if(!order.getOrderState().equals("待发货") && !order.getOrderState().equals("待收货")){return false;}

        Refund refund=new Refund();
        refund.setId(o_id);
        refund.setText(text);
        refund.setRefundState("待审核");
        refundRepo.save(refund);

        order.setIsRefunding("y");
        orderRepo.save(order);
        return true;
    }

    public List<Refund> getAllByBuyer(String buyer_id){
        return refundRepo.getAllByBuyer(buyer_id);
    }
}
