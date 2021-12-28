package com.example.demo.service;

import com.example.demo.model.Refund;
import com.example.demo.model.TradeOrder;
import com.example.demo.model.User;
import com.example.demo.repository.orderRepository;
import com.example.demo.repository.refundRepository;
import com.example.demo.repository.userRepository;
import com.example.demo.result.Result;
import com.example.demo.result.ResultFactory;
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

    @Autowired
    userRepository userRepo;

    public Result getRefundInfo(String order_id){
        if(!refundRepo.existsById(order_id)){
            return ResultFactory.buildFailResult("暂无受理中的退款");
        }
        return ResultFactory.buildSuccessResult(refundRepo.findById(order_id).get());
    }

    public Result submitRefund(String o_id, String text){
        if(refundRepo.existsById(o_id)){return ResultFactory.buildFailResult("退款正在受理/已经受理");}
        if(!orderRepo.existsById(o_id)){return ResultFactory.buildFailResult("不存在该订单");}
        TradeOrder order=orderRepo.getOne(o_id);
        if(!order.getIsRefunding().equals("n")){return ResultFactory.buildFailResult("退款正在受理");}
        if(!order.getOrderState().equals("待发货") && !order.getOrderState().equals("待收货")){return ResultFactory.buildFailResult("该订单无法退款");}

        Refund refund=new Refund();
        refund.setId(o_id);
        refund.setText(text);
        refund.setRefundState("待审核");
        refundRepo.save(refund);

        order.setIsRefunding("y");
        orderRepo.save(order);
        return ResultFactory.buildResult(200,"退款已申请",null);
    }

    public Result getAllByBuyer(String buyer_id){
        if(!userRepo.existsById(buyer_id)){return ResultFactory.buildFailResult("不存在该用户");}
        return ResultFactory.buildSuccessResult(refundRepo.getAllByBuyer(buyer_id));
    }

    public Result cancelRefund(String order_id){
        if(!refundRepo.existsById(order_id)){return ResultFactory.buildFailResult("不存在该退款条目");}
        Refund refund = refundRepo.findById(order_id).get();
        TradeOrder order = orderRepo.findById(order_id).get();
        if(!order.getIsRefunding().equals("y")){return ResultFactory.buildFailResult("该订单尚未申请退款");}
        if(order.getOrderState().equals("已收货")||order.getOrderState().equals("已退款")||order.getOrderState().equals("未支付")){
            return ResultFactory.buildFailResult("订单状态错误");
        }
        if(refund.getRefundState().equals("已批准")){return ResultFactory.buildFailResult("退款已批准，无法取消");}
        refundRepo.delete(refund);
        order.setIsRefunding("n");
        orderRepo.save(order);
        return ResultFactory.buildResult(200,"退款取消成功",null);
    }

    public Result permitRefund(String order_id){
        if(!refundRepo.existsById(order_id)){return ResultFactory.buildFailResult("不存在该退款条目");}
        Refund refund = refundRepo.findById(order_id).get();
        TradeOrder order = orderRepo.findById(order_id).get();
        if(!order.getIsRefunding().equals("y")){return ResultFactory.buildFailResult("该订单尚未申请退款");}
        if(order.getOrderState().equals("已收货")||order.getOrderState().equals("已退款")||order.getOrderState().equals("未支付")){
            return ResultFactory.buildFailResult("订单状态错误");
        }
        if(!refund.getRefundState().equals("待审核")&&!refund.getRefundState().equals("已驳回")){return ResultFactory.buildFailResult("退款已处理");}
        refund.setRefundState("已批准");
        refundRepo.save(refund);
        order.setIsRefunding("n");
        order.setOrderState("已退款");
        orderRepo.save(order);
        //退钱
        User buyer = userRepo.findById(order.getBuyerId()).get();
        buyer.setBalance(buyer.getBalance()+order.getPrice());
        userRepo.save(buyer);
        return ResultFactory.buildResult(200,"退款成功",null);
    }

    public Result refuseRefund(String order_id){
        if(!refundRepo.existsById(order_id)){return ResultFactory.buildFailResult("不存在该退款条目");}
        Refund refund = refundRepo.findById(order_id).get();
        TradeOrder order = orderRepo.findById(order_id).get();
        if(!order.getIsRefunding().equals("y")){return ResultFactory.buildFailResult("该订单尚未申请退款");}
        if(order.getOrderState().equals("已收货")||order.getOrderState().equals("已退款")||order.getOrderState().equals("未支付")){
            return ResultFactory.buildFailResult("订单状态错误");
        }
        if(!refund.getRefundState().equals("待审核")){return ResultFactory.buildFailResult("退款已处理");}
        refund.setRefundState("已驳回");
        refundRepo.save(refund);
        return ResultFactory.buildResult(200,"退款驳回",null);
    }
}
