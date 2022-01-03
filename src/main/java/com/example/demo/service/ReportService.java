package com.example.demo.service;

import com.example.demo.model.Good;
import com.example.demo.model.Report;
import com.example.demo.model.User;
import com.example.demo.repository.goodRepository;
import com.example.demo.repository.reportRepository;
import com.example.demo.repository.userRepository;
import com.example.demo.result.Result;
import com.example.demo.result.ResultFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class ReportService implements IDGenenrator{
    @Autowired
    reportRepository reportRepo;
    @Autowired
    goodRepository goodRepo;
    @Autowired
    userRepository userRepo;

    public Result getAllUnreadReports(){
        Report report=new Report();
        report.setReportState("0");
        Example<Report> example =Example.of(report);
        Sort sort= Sort.sort(Report.class).ascending();
        sort.getOrderFor("date");
        List<Report> reports=reportRepo.findAll(example,sort);
        List<Map<String,Object>> result=new ArrayList<>();
        for(Report report1:reports){
            Map<String,Object> map=new HashMap<>();
            map.put("report_id",report1.getId());
            map.put("good_id",report1.getId());
            Good good=goodRepo.findById(report1.getGoodId()).get();
            map.put("good_name",good.getName());
            map.put("good_url",good.getUrl());
            map.put("user_id",report1.getUserId());
            map.put("user_name",userRepo.findById(report1.getUserId()).get().getName());
            map.put("date",report1.getDate());
            result.add(map);
        }
        return ResultFactory.buildSuccessResult(result);
    }

    public Result getAllReportsByUser(String user_id){
        if(!userRepo.existsById(user_id)){
            return ResultFactory.buildFailResult("用户不存在");
        }
        List<Report> reports=reportRepo.getAllIdByUser(user_id);
        List<Map<String,Object>> result=new ArrayList<>();
        for (Report report:reports){
            Map<String,Object> map=new HashMap<>();
            map.put("report_id",report.getId());
            map.put("good_id",report.getId());
            Good good=goodRepo.findById(report.getGoodId()).get();
            map.put("good_name",good.getName());
            map.put("good_url",good.getUrl());
            map.put("report_state",report.getReportState());
            map.put("date",report.getDate());
            result.add(map);
        }
        return ResultFactory.buildSuccessResult(result);
    }

    public Result readReport(String report_id){
        if(!reportRepo.existsById(report_id)){
            return ResultFactory.buildFailResult("举报不存在");
        }
        Report report = reportRepo.findById(report_id).get();
        Good good=goodRepo.findById(report.getGoodId()).get();
        User seller=userRepo.findById(good.getSellerId()).get();
        Map<String,Object> map=new HashMap<>();
        map.put("report_id",report_id);
        map.put("report_state",report.getReportState());
        map.put("text",report.getText());
        map.put("date",report.getDate());
        map.put("good_id",good.getId());
        map.put("good_name",good.getName());
        map.put("good_url",good.getUrl());
        map.put("good_price",good.getPrice());
        map.put("good_info",good.getInfo());
        map.put("good_state",good.getGoodState());
        map.put("user_id",report.getUserId());
        map.put("user_name",userRepo.findById(report.getUserId()).get().getName());
        map.put("seller_id",good.getSellerId());
        map.put("seller_name",seller.getName());
        return ResultFactory.buildSuccessResult(map);
    }

    public Result submitReport(String user_id,String good_id,String text){
        if(!userRepo.existsById(user_id)){
            return ResultFactory.buildFailResult("用户不存在");
        }
        if(!goodRepo.existsById(good_id)){
            return ResultFactory.buildFailResult("商品不存在");
        }
        Report report=new Report();
        report.setGoodId(good_id);
        report.setUserId(user_id);
        report.setReportState("0");
        Example<Report> example=Example.of(report);
        if(reportRepo.exists(example)){
           return ResultFactory.buildFailResult("对该商品的举报还未处理");
        }
        report.setDate(Instant.now().plusMillis(TimeUnit.HOURS.toMillis(8)));
        report.setId(generateID(24));
        report.setText(text);
        reportRepo.save(report);
        return ResultFactory.buildResult(200,"成功提交举报",null);
    }

    public Result finishReport(String report_id,String plan_id){
        if(!reportRepo.existsById(report_id)){
            return ResultFactory.buildFailResult("举报不存在");
        }
        Report report=reportRepo.findById(report_id).get();
        if(!report.getReportState().equals("0")){
            return ResultFactory.buildFailResult("举报已处理");
        }
        if(plan_id.equals("1")) {
            Good good = goodRepo.findById(report.getGoodId()).get();
            if(!good.getGoodState().equals("已下架")) {
                good.setGoodState("已下架");
                goodRepo.save(good);
            }
        }
        report.setReportState("1");
        return ResultFactory.buildResult(200,"举报已处理",null);
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
            if(!reportRepo.existsById(id.toString())) return id.toString();
        }
    }
}
