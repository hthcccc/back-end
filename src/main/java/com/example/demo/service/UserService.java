package com.example.demo.service;

import com.example.demo.repository.adminRepository;
import com.example.demo.repository.creditRepository;
import com.example.demo.repository.verificationRepository;
import com.example.demo.result.*;
import com.auth0.jwt.JWT;
import com.example.demo.model.Address;
import com.example.demo.model.AddressId;
import com.example.demo.model.User;
import com.example.demo.repository.addressRepository;
import com.example.demo.repository.userRepository;
import com.example.demo.result.ResultFactory;
import com.example.demo.utils.Encryption;
import com.example.demo.utils.TokenUse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class UserService implements IDGenenrator{
    @Autowired
    userRepository userRepo;
    @Autowired
    adminRepository adminRepo;
    @Autowired
    addressRepository addrRepo;
    @Autowired
    verificationRepository verificationRepo;
    @Autowired
    creditRepository creditRepo;

    public boolean existsUser(String user_id){
        return userRepo.existsById(user_id);
    }

    public boolean existsPhone(String phone){
        if(userRepo.existsByPhone(phone)>0) {
            return true;
        }else{
            return false;
        }
    }

    public Result getById(String ID)
    {
        if(userRepo.existsById(ID)) {
            User user = userRepo.findById(ID).get();
            user.setSalt("");
            user.setPassword("");
            return ResultFactory.buildSuccessResult(user);
        }
        return ResultFactory.buildFailResult("no student exists by id="+ID);
    }

    public Result getUsersByName(String name){
        List<User> users = userRepo.getUsersByName(name);
        for(User user:users){
            user.setBalance(0);
            user.setPassword(null);
            user.setSalt(null);
        }
        return ResultFactory.buildSuccessResult(users);
    }

    public Double getBalance(String ID){
        return userRepo.getBalance(ID);
    }

//    public void addUser(String id,String name)
//    {
//        user user=new user();
//        user.setUserId(id);
//        user.setName(name);
//        tmp.save(user);
//    }

    public void setUserName(String id,String name)
    {
        User user= userRepo.findById(id).get();
        if(user!=null)
        {
            user.setName(name);
            userRepo.save(user);
        }
    }

    public void setUserAge(String id,int age)
    {
        User user= userRepo.findById(id).get();
        if(user!=null)
        {
            user.setAge(age);
            userRepo.save(user);
        }
    }

    public void setUserSex(String id,String sex)
    {
        User user= userRepo.findById(id).get();
        if(user!=null)
        {
            if(sex.equals("M")||sex.equals("F")||sex.equals("N"))
            {
                user.setSex(sex);
                userRepo.save(user);
            }
        }
    }

    public void setUserMail(String id,String mail)
    {
        User user= userRepo.findById(id).get();
        if(user!=null)
        {
            if(mail.contains("@"))
            {
                user.setMail(mail);
                userRepo.save(user);
            }
        }
    }

    public void setUserPwd(String id,String pwd)
    {
        if(userRepo.existsById(id))
        {
            User user=userRepo.getOne(id);
            if(pwd.length()>=6)
            {
                user.setSalt(Encryption.generateSalt());
                user.setPassword(Encryption.shiroEncryption(pwd,user.getSalt()));
                userRepo.save(user);
            }
        }
    }


    public void setUserBalance(String id,double balance)
    {
        User user= userRepo.findById(id).get();
        if(user!=null)
        {
            if(balance>=0)
            {
                user.setBalance(balance);
                userRepo.save(user);
            }
        }
    }

    public Result deleteUser(String id)
    {
        if(userRepo.existsById(id))
        {
            User user1= userRepo.findById(id).get();
            userRepo.delete(user1);
            return ResultFactory.buildResult(200,"delete this user successfully",null);
        }

        return ResultFactory.buildFailResult("no user exists by this id");
    }

    public Result checkPasswordById(String id,String pwd)
    {
        if(userRepo.existsById(id))
        {
            User user= userRepo.findById(id).get();
            String real_pwd=user.getPassword();
            String en_pwd=Encryption.shiroEncryption(pwd,user.getSalt());
            if(real_pwd.equals(en_pwd)){
                System.out.println("密码校验正确");
            }
            if(Encryption.shiroEncryption(pwd,user.getSalt()).equals(user.getPassword())){
                String token= TokenUse.sign(id,pwd,"customer");
                if(token!=null){
                    return ResultFactory.buildResult(200,token,null);
                }else {
                    return ResultFactory.buildResult(400,"Token签发失败",null);
                }
            }
            else {
                return ResultFactory.buildFailResult("密码错误");
            }
        }
        else{
            return ResultFactory.buildFailResult("用户id不存在");
        }
    }

    public Result checkPassword(String user_id,String pwd){
        Result result = checkPasswordById(user_id,pwd);
        if(result.getCode()==200||result.getCode()==201){
            System.out.println("登录成功");
            String token= TokenUse.sign(user_id,pwd,"customer");
            if(token!=null){
                return ResultFactory.buildResult(200,token,null);
            }else {
                return ResultFactory.buildResult(400,"Token签发失败",null);
            }
        }
        return ResultFactory.buildResult(400,result.getMsg(),null);
    }

    public String checkPasswordByToken(String token){
        if(TokenUse.tokenVerify(token)) {
            String user_id = JWT.decode(token).getClaim("user_id").asString();
            String pwd = JWT.decode(token).getClaim("pwd").asString();
            if (checkPasswordById(user_id, pwd).getCode() == 200) {
                String newtoken = TokenUse.sign(userRepo.getName(user_id), user_id,"customer");
                if (token != null) {
                    return newtoken;
                }
            }
            return null;
        }else{
            String user_id = JWT.decode(token).getClaim("user_id").asString();
            String pwd = JWT.decode(token).getClaim("pwd").asString();
            if (checkPasswordById(user_id, pwd).getCode()==200) {
                String newtoken = TokenUse.sign(userRepo.getName(user_id), user_id,"customer");
                if (token != null) {
                    return newtoken;
                }
            }else{
                return null;
            }
        }
        return null;
    }

    public Result checkPasswordByMail(String mail,String pwd)
    {
        if(userRepo.existsByMail(mail)>0)
        {
            User user= userRepo.getUserByMail(mail);
            if(Encryption.shiroEncryption(pwd,user.getSalt()).equals(user.getPassword())){
                return ResultFactory.buildResult(200,"用户登录成功",null);
            }
            else {
                return ResultFactory.buildFailResult("用户密码错误");
            }
        }
        else{
            return ResultFactory.buildFailResult("邮箱不存在");
        }
    }

    public Result getCredit(String id)
    {
        if(userRepo.existsById(id))
        {
            User user= userRepo.findById(id).get();
            double credit=user.getCredit();
            credit= new BigDecimal(credit).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            Map<String,Double> result=new HashMap<>();
            result.put("credit",credit);
            return ResultFactory.buildSuccessResult(result);
        }
        else{
            return ResultFactory.buildFailResult("no user exists by this id");
        }
    }
    public void remove(String phone){
        if(verificationRepo.existsById(phone)){
            verificationRepo.deleteById(phone);
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
    public boolean checkCode(String phone,String code){
        if(verificationRepo.existsById(phone)&&code.equals(verificationRepo.findById(phone).get().getCode())&&!isOverdue(phone)){
            return true;
        }
        return false;
    }
    public Result addUser(String name,String mail,String phone,String pwd,String code){
        if(!verificationRepo.existsById(phone)){
            return ResultFactory.buildFailResult("请点击发送验证码");
        }
        if(!checkCode(phone,code)) {
            return ResultFactory.buildFailResult("验证码不正确");
        }
        User user=new User();
        String id=generateID(16);
        user.setUserId(id);
        user.setName(name);
        user.setPhone(phone);
        if(mail!=null&&!mail.isEmpty()&&!mail.equals("")) {
            user.setMail(mail);
        }
        if(phone==null||phone.isEmpty()||phone.equals("")){
            return ResultFactory.buildFailResult("请输入手机号");
        }
        user.setPhone(phone);
        user.setCredit(5);
        user.setSalt(Encryption.generateSalt());
        user.setPassword(Encryption.shiroEncryption(pwd,user.getSalt()));
        user.setBalance(999.99);
        userRepo.save(user);
        return ResultFactory.buildSuccessResult(id);
    }
    public Result resetPassword(String phone,String pwd,String code) {
        if(!verificationRepo.existsById(phone)){
            return ResultFactory.buildFailResult("您还未发送验证码，请点击发送验证码。");
        }
        if(!checkCode(phone,code)) {
            return ResultFactory.buildFailResult("验证码不正确！");
        }
        List<User> userlist = userRepo.getUserByPhone(phone);
        if(userlist.size()==0) {
            return ResultFactory.buildFailResult("用户不存在！");
        }
        User user=userlist.get(0);
        user.setSalt(Encryption.generateSalt());
        user.setPassword(Encryption.shiroEncryption(pwd,user.getSalt()));
        userRepo.save(user);
        return ResultFactory.buildResult(200,"密码重置成功！",null);
    }

    public Result newAddress(String user_id,String address){
        if(!addrRepo.existsById(new AddressId(user_id,address))){
            addrRepo.save(new Address(user_id,address));
            return ResultFactory.buildResult(200,"地址保存成功！",null);
        }
        return ResultFactory.buildResult(400,"新增的地址已经存在",null);
    }

    public void removeOneAddress(String user_id,String address){
        if(addrRepo.existsById(new AddressId(user_id,address))){
            addrRepo.delete(new Address(user_id,address));
        }
    }

    public Result getAllAddress(String user_id){
        if(userRepo.existsById(user_id)){
            return ResultFactory.buildSuccessResult(addrRepo.getAllAddress(user_id));
        }
        return ResultFactory.buildFailResult("no user exists by this id");
    }

    public Result updateUserInfo(String user_id,String name,String mail,String sex,Integer age){
        if(userRepo.existsById(user_id)){
            User user = userRepo.getOne(user_id);
            user.setName(name);
            user.setMail(mail);
            user.setSex(sex);
            user.setAge(age);
            userRepo.save(user);
            return ResultFactory.buildSuccessResult("用户信息更新成功");
        }
        return ResultFactory.buildFailResult("用户不存在");
    }

    public boolean existsMail(String mail){
        if(userRepo.existsByMail(mail)>0){
            return true;
        }
        return false;
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

    public Result getCreditHistory(String uid) {
        return ResultFactory.buildResult(200,"成功",creditRepo.getCreditHistory(uid));
    }

}
