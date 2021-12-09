package com.example.demo.controller;

import com.example.demo.config.ApiGroup;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController("user")
@RequestMapping("/user")
public class userController {
    @Autowired
    UserService tmp;

    @GetMapping("/getUser/{id}")
    @ApiGroup(group = {"user"})
    @ApiOperation(value = "通过id查询用户",notes="用户id")
    public User getUser(@PathVariable String id)
    {
        return tmp.getById(id);
    }

    @PostMapping("/checkPasswordById")
    @ApiGroup(group = {"user"})
    @ApiOperation(value = "校验用户id和密码",notes = "用户id，密码")
    public int checkPasswordById(@RequestParam("user_id") String user_id,@RequestParam("pwd") String pwd){
        return tmp.checkPasswordById(user_id,pwd);
    }

    @PostMapping("/checkPasswordByMAIL")
    @ApiGroup(group = {"user"})
    @ApiOperation(value = "校验用户邮箱和密码",notes = "用户mail，密码")
    public int checkPasswordByMail(@RequestParam("mail") String mail,@RequestParam("pwd") String pwd){
        return tmp.checkPasswordByMail(mail,pwd);
    }

    @GetMapping("/getCredit/{id}")
    @ApiGroup(group = {"user"})
    @ApiOperation(value = "查询用户信用评分",notes = "用户id")
    public double getCredit(@PathVariable String id){
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


    @Transactional
    @PostMapping(value = "/setUserPwd")
    @ApiGroup(group = {"user"})
    @ApiOperation(value = "设置用户密码",notes = "用户id，新密码")
    public void setUserPwd(@RequestParam("user_id") String user_id,
                           @RequestParam("pwd") String pwd) {
        tmp.setUserPwd(user_id,pwd);
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
