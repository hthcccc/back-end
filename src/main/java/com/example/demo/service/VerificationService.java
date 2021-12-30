package com.example.demo.service;


import com.example.demo.model.User;
import com.example.demo.model.Verification;
import com.example.demo.repository.userRepository;
import com.example.demo.repository.verificationRepository;
import com.example.demo.utils.HttpUtils;
import com.example.demo.result.Result;
import com.example.demo.result.ResultFactory;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;


import java.security.SecureRandom;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;


//class Value{
//    public String code;
//    public Instant timestamp;
//}

@Service
public class VerificationService implements IDGenenrator {
    //private Map<String,Value> verificationMap;

    @Autowired
    verificationRepository verificationRepo;
    @Autowired
    userRepository userRepo;

    final public static String url="https://api.netease.im/sms/sendtemplate.action";
    final public static String appKey="778ec875572db2031d550d8c4eb81955";
    final public static String appSecret="3b02c7973c97";

    public void insertOrReplace(String phone,String code){
        Verification v=new Verification();
        v.setId(phone);
        v.setCode(code);
        v.setTimestamp(Instant.now().plusMillis(TimeUnit.HOURS.toMillis(8)));
        if(verificationRepo.existsById(phone)){
            verificationRepo.save(v);
        }else {
            verificationRepo.save(v);
        }
    }

    public boolean isOverdue(String phone){
        if(verificationRepo.existsById(phone)){
            System.out.println(verificationRepo.findById(phone).get().getTimestamp().plusMillis(TimeUnit.SECONDS.toMillis(600)));
            System.out.println(Instant.now().plusMillis(TimeUnit.HOURS.toMillis(8)));
            if(verificationRepo.findById(phone).get().getTimestamp().plusMillis(TimeUnit.SECONDS.toMillis(900)).isBefore(Instant.now().plusMillis(TimeUnit.HOURS.toMillis(8)))){
                remove(phone);
                return true;
            }else{
                return false;
            }
        }
        return true;
    }

    public void remove(String phone){
        if(verificationRepo.existsById(phone)){
            verificationRepo.deleteById(phone);
        }
    }

    public boolean checkCode(String phone,String code){
        if(verificationRepo.existsById(phone)&&code.equals(verificationRepo.findById(phone).get().getCode())&&!isOverdue(phone)){
            return true;
        }
        return false;
    }

    public Result checkPhone(String phone,String code,String uid){
        if(!verificationRepo.existsById(phone)){
            return ResultFactory.buildFailResult("请点击发送验证码");
        }
        if(checkCode(phone,code)){
            if(userRepo.existsById(uid)) {
                return ResultFactory.buildResult(400,"用户名已经存在",null);
            }
            if(userRepo.existsByPhone(phone)==0){
                User user=new User();
                //user.setUserId(generateID(16));
                user.setUserId(uid);
                user.setPhone(phone);
                user.setBalance(0.0);
                userRepo.save(user);
                return ResultFactory.buildResult(200,"新用户注册成功",user.getUserId());
            }
            return ResultFactory.buildResult(200,"验证成功",null);
        }else{
            return ResultFactory.buildFailResult("验证失败");
        }
    }

    public String generateCode(){
        StringBuilder code=new StringBuilder();
        Random rd = new SecureRandom();
        for(int i=0;i<6;i++){
            code.append(rd.nextInt(10));
        }
        return code.toString();
    }

    public String generateMessage(){
        return "欢迎使用二手交易平台，您的短信验证码为："+generateCode()+"，有效时间10分钟。";
    }

    public String generateMessage(String code){
        return "欢迎使用二手交易平台，您的短信验证码为："+code+"，有效时间10分钟。";
    }

    public Result sendMessage(String phone){
        String code=generateCode();

        String host = "https://gyytz.market.alicloudapi.com";
        String path = "/sms/smsSend";
        String method = "POST";
        String appcode = "81c536023bde4b528f3215182b1069c8";
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("mobile", phone);
        querys.put("param", "**code**:"+code+",**minute**:5");
        querys.put("smsSignId", "2e65b1bb3d054466b82f0c9d125465e2");
        querys.put("templateId", "908e94ccf08b4476ba6c876d13f084ad");
        Map<String, String> bodys = new HashMap<String, String>();

        try {
            /**
             * 重要提示如下:
             * HttpUtils请从
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
             * 下载
             *
             * 相应的依赖请参照
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
             */
            //HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            //System.out.println(response.toString());
            System.out.println(code);
            insertOrReplace(phone,code);
            return ResultFactory.buildResult(200,"发送成功",null);
            //获取response的body
            //System.out.println(EntityUtils.toString(response.getEntity()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResultFactory.buildFailResult("发送失败");
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
            if(!userRepo.existsById(id.toString())) return id.toString();
        }
    }
}
