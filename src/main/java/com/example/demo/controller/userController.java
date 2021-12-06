package com.example.demo.controller;

import com.example.demo.config.ApiGroup;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("user")
@RequestMapping("/user")
public class userController {
    @Autowired
    UserService tmp;

    @GetMapping("/getUser/{id}")
    @ApiGroup(group = {"user"})
    public User getUser(@PathVariable String id)
    {
        return tmp.getById(id);
    }

    @GetMapping("/checkPassword/{id,password}")
    @ApiGroup(group = {"user"})
    public int checkPassword(String id,String pwd){
        return tmp.checkPassword(id,pwd);
    }

    @GetMapping("/checkPassword/{id}")
    @ApiGroup(group = {"user"})
    @ApiOperation(value = "查询用户信用评分",notes = "用户id")
    public double getCredit(String id){
        return tmp.getCredit(id);
    }


    @ApiOperation(value="register",notes="名字，邮箱，密码")
    @PostMapping(value = "/register")
    @ApiGroup(group = {"user"})
    public String register(@RequestBody User user) {
        return tmp.addUser(user.getName(),user.getMail(),user.getPassword());
    }

    @PostMapping(value = "/setUserName")
    @ApiGroup(group = {"user"})
    @ApiOperation(value = "设置用户名字",notes="用户id，用户新名字")
    public void setUserName(@RequestBody User user1) {
        tmp.setUserName(user1.getUserId(),user1.getName());
    }

    @PostMapping(value = "/setUserAge")
    @ApiGroup(group = {"user"})
    public void setUserAge(@RequestBody User user1) {
        tmp.setUserAge(user1.getUserId(),user1.getAge());
    }

    @PostMapping(value = "/setUserSex")
    @ApiGroup(group = {"user"})
    public void setUserSex(@RequestBody User user1) {
        tmp.setUserSex(user1.getUserId(),user1.getSex());
    }

    @PostMapping(value = "/setUserMail")
    @ApiGroup(group = {"user"})
    public void setUserMail(@RequestBody User user1) {
        tmp.setUserMail(user1.getUserId(),user1.getMail());
    }


    @PostMapping(value = "/setUserPwd")
    @ApiGroup(group = {"user"})
    public void setUserPwd(@RequestBody User user1) {
        tmp.setUserPwd(user1.getUserId(),user1.getPassword());
    }

    @PostMapping(value = "/setUserBalance")
    @ApiGroup(group = {"user"})
    public void setUserBalance(@RequestBody User user1) {
        tmp.setUserBalance(user1.getUserId(),user1.getBalance());
    }


    @GetMapping("/deleteUser/{id}")
    @ApiGroup(group = {"user"})
    public boolean deleteUser(@PathVariable String id)
    {
        return tmp.deleteUser(id);
    }


}
