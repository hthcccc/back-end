package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.result.Result;
import com.example.demo.result.ResultFactory;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class RefundService{
    @Autowired
    refundRepository refundRepo;
    @Autowired
    goodRepository goodRepo;
    @Autowired
    orderRepository orderRepo;
    @Autowired
    userRepository userRepo;
    @Autowired
    refundUrlRepository urlRepo;
    @Resource
    MinioClient client;

    private final String refund_url="http://116.62.208.68:9000/refund/";

    public Result getAllArbitrition(){
        Refund refund=new Refund();
        refund.setRefundState("待仲裁");
        Example<Refund> example=Example.of(refund);
        Sort sort = Sort.sort(Refund.class).ascending();
        sort.getOrderFor("refund_date");
        List<Refund> refunds=refundRepo.findAll(example,sort);
        List<Map<String,Object>> result= new ArrayList<>();
        for (Refund refund1:refunds){
            Map<String,Object> map=new HashMap<>();
            TradeOrder order=orderRepo.findById(refund1.getId()).get();
            map.put("order_id",order.getId());
            map.put("text",refund1.getText());
            map.put("refund_state",refund1.getRefundState());
            map.put("refund_time",refund1.getRefund_time());
            map.put("good_id",order.getGoodId());
            map.put("good_name",goodRepo.findById(order.getGoodId()).get().getName());
            map.put("good_url",goodRepo.findById(order.getGoodId()).get().getUrl());
            map.put("refund_urls",urlRepo.getUrlsByOrder(order.getId()));
            result.add(map);
        }
        return ResultFactory.buildSuccessResult(result);
    }

    public Result getRefundInfo(String order_id){
        if(!refundRepo.existsById(order_id)){
            return ResultFactory.buildFailResult("暂无受理中的退款");
        }
        Refund refund =refundRepo.findById(order_id).get();
        Map<String,Object> result=new HashMap<>();
        TradeOrder order = orderRepo.findById(order_id).get();
        result.put("order_id",order_id);
        result.put("text",refund.getText());
        result.put("refund_state",refund.getRefundState());
        result.put("refund_time",refund.getRefund_time());
        result.put("good_id",order.getGoodId());
        result.put("good_name",goodRepo.findById(order.getGoodId()).get().getName());
        result.put("good_url",goodRepo.findById(order.getGoodId()).get().getUrl());
        result.put("price",order.getPrice());
        result.put("refund_urls",urlRepo.getUrlsByOrder(order_id));
        String isRefunding="y";
        if(refund.getRefundState().equals("卖家批准")||refund.getRefundState().equals("仲裁批准")||refund.getRefundState().equals("仲裁驳回")){
            isRefunding="n";
        }
        result.put("isRefunding",isRefunding);
        return ResultFactory.buildSuccessResult(result);
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
        refund.setRefund_time(Instant.now().plusMillis(TimeUnit.HOURS.toMillis(8)));
        refundRepo.save(refund);

        order.setIsRefunding("y");
        orderRepo.save(order);
        return ResultFactory.buildResult(200,"退款已申请",null);
    }

    public Result getAllByBuyer(String buyer_id){
        if(!userRepo.existsById(buyer_id)){return ResultFactory.buildFailResult("不存在该用户");}
        List<Refund> refunds = refundRepo.getAllByBuyer(buyer_id);
        List<Map<String,Object>> result=new ArrayList<>();
        for(Refund refund:refunds){
            Map<String,Object> map=new HashMap<>();
            map.put("order_id",refund.getId());
            map.put("text",refund.getText());
            map.put("refund_state",refund.getRefundState());
            map.put("refund_time",refund.getRefund_time());
            TradeOrder order=orderRepo.findById(refund.getId()).get();
            map.put("good_id",order.getGoodId());
            map.put("good_name",goodRepo.findById(order.getGoodId()).get().getName());
            map.put("good_url",goodRepo.findById(order.getGoodId()).get().getUrl());
            map.put("price",order.getPrice());
            String isRefunding="y";
            if(refund.getRefundState().equals("卖家批准")||refund.getRefundState().equals("仲裁批准")||refund.getRefundState().equals("仲裁驳回")){
                isRefunding="n";
            }
            map.put("isRefunding",isRefunding);
            result.add(map);
        }
        return ResultFactory.buildSuccessResult(result);
    }

    public Result getAllBySeller(String seller_id){
        if(!userRepo.existsById(seller_id)){return ResultFactory.buildFailResult("不存在该用户");}
        List<Refund> refunds = refundRepo.getAllBySeller(seller_id);
        List<Map<String,Object>> result=new ArrayList<>();
        for(Refund refund:refunds){
            Map<String,Object> map=new HashMap<>();
            map.put("order_id",refund.getId());
            map.put("text",refund.getText());
            map.put("refund_state",refund.getRefundState());
            map.put("refund_time",refund.getRefund_time());
            TradeOrder order=orderRepo.findById(refund.getId()).get();
            map.put("good_id",order.getGoodId());
            map.put("good_name",goodRepo.findById(order.getGoodId()).get().getName());
            map.put("good_url",goodRepo.findById(order.getGoodId()).get().getUrl());
            map.put("price",order.getPrice());
            result.add(map);
        }
        return ResultFactory.buildSuccessResult(result);
    }

    public Result cancelRefund(String order_id){
        if(!refundRepo.existsById(order_id)){return ResultFactory.buildFailResult("不存在该退款条目");}
        Refund refund = refundRepo.findById(order_id).get();
        TradeOrder order = orderRepo.findById(order_id).get();
        if(!order.getIsRefunding().equals("y")){return ResultFactory.buildFailResult("该订单尚未申请退款");}
        if(order.getOrderState().equals("已收货")||order.getOrderState().equals("已退款")||order.getOrderState().equals("未支付")){
            return ResultFactory.buildFailResult("订单状态错误");
        }
        if(!refund.getRefundState().equals("待审核")&&!refund.getRefundState().equals("卖家驳回")){return ResultFactory.buildFailResult("退款状态错误");}
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
        if(!refund.getRefundState().equals("待审核")){return ResultFactory.buildFailResult("退款状态错误");}
        refund.setRefundState("卖家批准");
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
        if(!refund.getRefundState().equals("待审核")){return ResultFactory.buildFailResult("退款状态错误");}
        refund.setRefundState("卖家驳回");
        refundRepo.save(refund);
        return ResultFactory.buildResult(200,"退款驳回",null);
    }

    public Result submitArbitration(String order_id, String text, MultipartFile file){
        if(!orderRepo.existsById(order_id)){return ResultFactory.buildFailResult("不存在该订单");}
        if(!refundRepo.existsById(order_id)){return ResultFactory.buildFailResult("不存在该退款");}
        TradeOrder order=orderRepo.getOne(order_id);
        if(!order.getIsRefunding().equals("y")){return ResultFactory.buildFailResult("订单状态错误");}
        if(!order.getOrderState().equals("待发货") && !order.getOrderState().equals("待收货")){return ResultFactory.buildFailResult("订单状态错误");}
        Refund refund=refundRepo.findById(order_id).get();
        if(!refund.getRefundState().equals("卖家驳回")){return ResultFactory.buildFailResult("退款状态错误");}
        refund.setRefundState("待仲裁");
        if(text!=null&&!text.isEmpty()&&!text.equals("")){
            refund.setText(text);
        }
        refundRepo.save(refund);
        RefundUrl url=new RefundUrl();
        try {
            //上传照片到minio
            if(!(file==null)&&!file.isEmpty()) {
                client.putObject(
                        PutObjectArgs.builder()
                                .bucket("refund")
                                .object(file.getOriginalFilename())
                                .contentType(file.getContentType())
                                .stream(file.getInputStream(),file.getSize(),-1)
                                .build());

                RefundUrlId urlId=new RefundUrlId(order_id,refund_url + file.getOriginalFilename());
                url.setId(urlId);
                urlRepo.save(url);
            }
        } catch (Exception e) {
            System.out.println("上传失败");
            return ResultFactory.buildFailResult("图片上传失败");
        }
        /*for(MultipartFile file:files){
            RefundUrl url=new RefundUrl();
            try {
                //上传照片到minio
                System.out.println(file.isEmpty());
                if(!(file==null)&&!file.isEmpty()) {
                    client.putObject(
                            PutObjectArgs.builder()
                                    .bucket("refund")
                                    .object(file.getOriginalFilename())
                                    .contentType(file.getContentType())
                                    .stream(file.getInputStream(),file.getSize(),-1)
                                    .build());
                    RefundUrlId urlId=new RefundUrlId(order_id,refund_url + file.getOriginalFilename());
                    url.setId(urlId);
                    urlRepo.save(url);
                }
            } catch (Exception e) {
                System.out.println("上传失败");
                return ResultFactory.buildFailResult("图片上传失败");
            }
        }*/
        return ResultFactory.buildResult(200,"已提交仲裁",null);
    }

    public Result permitArbitration(String order_id){
        if(!orderRepo.existsById(order_id)){return ResultFactory.buildFailResult("不存在该订单");}
        if(!refundRepo.existsById(order_id)){return ResultFactory.buildFailResult("不存在该退款");}
        TradeOrder order=orderRepo.getOne(order_id);
        if(!order.getIsRefunding().equals("y")){return ResultFactory.buildFailResult("订单状态错误");}
        if(!order.getOrderState().equals("待发货") && !order.getOrderState().equals("待收货")){return ResultFactory.buildFailResult("订单状态错误");}
        Refund refund=refundRepo.findById(order_id).get();
        if(!refund.getRefundState().equals("待仲裁")){return ResultFactory.buildFailResult("退款状态错误");}
        refund.setRefundState("仲裁批准");
        refundRepo.save(refund);
        order.setOrderState("已退款");
        order.setIsRefunding("n");
        orderRepo.save(order);
        //退钱
        User buyer = userRepo.findById(order.getBuyerId()).get();
        buyer.setBalance(buyer.getBalance()+order.getPrice());
        userRepo.save(buyer);
        return ResultFactory.buildResult(200,"退款成功",null);
    }

    public Result refuseArbitration(String order_id) {
        if (!orderRepo.existsById(order_id)) {
            return ResultFactory.buildFailResult("不存在该订单");
        }
        if (!refundRepo.existsById(order_id)) {
            return ResultFactory.buildFailResult("不存在该退款");
        }
        TradeOrder order = orderRepo.getOne(order_id);
        if (!order.getIsRefunding().equals("y")) {
            return ResultFactory.buildFailResult("订单状态错误");
        }
        if (!order.getOrderState().equals("待发货") && !order.getOrderState().equals("待收货")) {
            return ResultFactory.buildFailResult("订单状态错误");
        }
        Refund refund = refundRepo.findById(order_id).get();
        if (!refund.getRefundState().equals("待仲裁")) {
            return ResultFactory.buildFailResult("退款状态错误");
        }
        refund.setRefundState("仲裁驳回");
        refundRepo.save(refund);
        order.setIsRefunding("n");
        orderRepo.save(order);
        return ResultFactory.buildResult(200,"仲裁驳回",null);
    }
}
