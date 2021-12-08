package com.example.demo.service;


import com.example.demo.model.User;
import com.example.demo.repository.userRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.Optional;
import java.util.Random;

@Service
public class UserService implements IDGenenrator{
    @Autowired
    userRepository userRepo;

    public User getById(String ID)
    {
        if(userRepo.existsById(ID)) {
            Optional<User> t = userRepo.findById(ID);
            return t.get();
        }
        return null;
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
        User user= userRepo.findById(id).get();
        if(user!=null)
        {
            if(pwd.length()>=6)
            {
                user.setPassword(pwd);
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

    public boolean deleteUser(String id)
    {
        if(userRepo.existsById(id))
        {
            User user1= userRepo.findById(id).get();
            userRepo.delete(user1);
            return true;
        }

        return false;
    }

    public int checkPassword(String id,String pwd)
    {
        if(userRepo.existsById(id))
        {
            User user1= userRepo.findById(id).get();
            if(pwd.equals(user1.getPassword())){
                return 1;
            }
            else {
                return 0;
            }
        }
        else{
            return 2;
        }
    }

    public double getCredit(String id)
    {
        if(userRepo.existsById(id))
        {
            User user= userRepo.findById(id).get();
            double credit=user.getCredit();
            return new BigDecimal(credit).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        }
        else{
            return 0.0;
        }
    }

    public String addUser(String name,String mail,String pwd){
        User user=new User();
        String id=generateID(16);
        user.setUserId(id);
        user.setName(name);
        user.setMail(mail);
        user.setPassword(pwd);
        user.setBalance(999.99);
        userRepo.save(user);
        return id;
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
            if(!userRepo.existsById(id.toString())) return id.toString();
        }
    }



}
