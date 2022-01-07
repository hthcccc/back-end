package com.example.demo.service;

import com.example.demo.model.Admin;
import com.example.demo.repository.adminRepository;
import com.example.demo.repository.userRepository;
import com.example.demo.result.Result;
import com.example.demo.result.ResultFactory;
import com.example.demo.utils.Encryption;
import com.example.demo.utils.TokenUse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class AdminService implements IDGenenrator{
    @Autowired
    adminRepository adminRepo;
    @Autowired
    userRepository userRepo;

    public Result getAdmin(String admin_id){
        if(!adminRepo.existsById(admin_id)){return ResultFactory.buildFailResult("不存在该管理员");}
        return ResultFactory.buildSuccessResult(adminRepo.findById(admin_id).get());
    }

    public Result addAdmin(String name,String mail,String phone,String pwd){
        Admin admin=new Admin();
        String id=generateID(6);
        admin.setId(id);
        admin.setName(name);
        if(this.existsMail(mail)){
            return ResultFactory.buildFailResult("该邮箱已绑定");
        }
        if(mail!=null&&!mail.isEmpty()&&!mail.equals("")) {
            admin.setMail(mail);
        }
        if(phone==null||phone.isEmpty()||phone.equals("")) {
            return ResultFactory.buildFailResult("请输入手机号");
        }
        admin.setPhone(phone);
        admin.setSalt(Encryption.generateSalt());
        admin.setPassword(Encryption.shiroEncryption(pwd,admin.getSalt()));
        adminRepo.save(admin);
        return ResultFactory.buildSuccessResult(id);
    }

    public Result checkPasswordById(String id,String pwd){
        if (!adminRepo.existsById(id)){return ResultFactory.buildFailResult("管理员id不存在");}
        Map<String,Object> returnInfo=new HashMap<>();
        Admin admin= adminRepo.findById(id).get();
        String real_pwd=admin.getPassword();
        String en_pwd=Encryption.shiroEncryption(pwd,admin.getSalt());
        if(Encryption.shiroEncryption(pwd,admin.getSalt()).equals(admin.getPassword())){
            String token= TokenUse.sign(id,pwd,"admin");
            if(token!=null){
                returnInfo.put("id",id);
                return ResultFactory.buildResult(201,token,returnInfo);
            }else {
                return ResultFactory.buildResult(400,"Token签发失败",null);
            }
            //return ResultFactory.buildResult(201,"管理员登录成功",null);
        }
        else {
            return ResultFactory.buildFailResult("管理员密码错误");
        }
    }

    public Result checkPasswordByMail(String mail,String pwd)
    {
        if(adminRepo.existsByMail(mail)>0)
        {
            Admin admin= adminRepo.getAdminByMail(mail);
            if(Encryption.shiroEncryption(pwd,admin.getSalt()).equals(admin.getPassword())){
                return ResultFactory.buildResult(201,"管理员登录成功",null);
            }
            else {
                return ResultFactory.buildFailResult("管理员密码错误");
            }
        }
        else{
            return ResultFactory.buildFailResult("邮箱不存在");
        }
    }

    public boolean existsAdmin(String admin_id){
        return adminRepo.existsById(admin_id);
    }

    public boolean existsMail(String mail){
        if(adminRepo.existsByMail(mail)>0){
            return true;
        }
        return false;
    }

    public boolean existsPhone(String phone){
        if(adminRepo.existsByPhone(phone)>0) {
            return true;
        }else{
            return false;
        }
    }

    @Override
    public StringBuilder tryGetID(int length) {
        StringBuilder id=new StringBuilder();
        Random rd = new SecureRandom();
        for(int i=0;i<length;i++){
            int bit = rd.nextInt(10);
            id.append(bit);
        }
        return id;
    }

    @Override
    public String generateID(int length) {
        while(true)
        {
            StringBuilder id=tryGetID(length);
            if(!adminRepo.existsById(id.toString())&&!userRepo.existsById(id.toString())) return id.toString();
        }
    }
}
