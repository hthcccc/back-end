package com.example.demo.controller;

import com.example.demo.config.ApiGroup;
import com.example.demo.model.User;
import com.example.demo.result.Result;
import com.example.demo.service.UserService;
import com.example.demo.result.*;
import com.example.demo.utils.*;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.example.demo.controller.requestBody.*;
import java.util.List;

@CrossOrigin(origins="*")
@RestController("user")
@RequestMapping("/user")
public class userController {
    @Autowired
    UserService tmp;

    @GetMapping("/getUser/{user_id}")
    @ApiGroup(group = {"user"})
    @ApiOperation(value = "通过id查询用户",notes="用户id")
    public Result getUser(@PathVariable String user_id)
    {
        return tmp.getById(user_id);
    }

    @GetMapping("/getUsersByName/{name}")
    @ApiGroup(group = {"user"})
    @ApiOperation(value = "查询昵称包含name的用户",notes="昵称")
    public Result getUsersByName(@PathVariable("name") String name)
    {
        return tmp.getUsersByName(name);
    }

//    @PostMapping("/checkPasswordById")
//    @ApiGroup(group = {"user"})
//    @ApiOperation(value = "校验用户id和密码",notes = "用户id，密码")
//    public Integer checkPasswordById(@RequestParam("user_id") String user_id,@RequestParam("pwd") String pwd){
//        return tmp.checkPasswordById(user_id,pwd);
//    }

//    @PostMapping("/checkPasswordById")
//    @ApiGroup(group = {"user"})
//    @ApiOperation(value = "校验用户id和密码",notes = "用户id，密码")
//    public Result checkPasswordById(@RequestBody checkIDBody body){
//        return tmp.checkPasswordById(body.user_id,body.pwd);
//    }

    @PostMapping("/loginByToken")
    @ApiGroup(group = {"user"})
    @ApiOperation(value = "利用token校验用户id和密码，并返回token",notes = "用户id，密码")
    public String loginByToken(@RequestBody String token){
        return tmp.checkPasswordByToken(token);
    }

    @PostMapping("/loginById")
    @ApiGroup(group = {"user"})
    @ApiOperation(value = "通过用户id和密码校验，并返回token",notes = "用户id，密码")
    public String loginById(@RequestBody checkIDBody body){
        return tmp.checkPassword(body.user_id,body.pwd);
    }
//    @PostMapping("/checkPasswordByMail")
//    @ApiGroup(group = {"user"})
//    @ApiOperation(value = "校验用户邮箱和密码",notes = "用户mail，密码")
//    public Integer checkPasswordByMail(@RequestParam("mail") String mail,@RequestParam("pwd") String pwd){
//        return tmp.checkPasswordByMail(mail,pwd);
//    }

    @PostMapping("/checkPasswordByMail")
    @ApiGroup(group = {"user"})
    @ApiOperation(value = "校验用户邮箱和密码",notes = "用户mail，密码")
    public Result checkPasswordByMail(@RequestBody checkMailBody body){
        return tmp.checkPasswordByMail(body.mail,body.pwd);
    }

    @GetMapping("/getCredit/{user_id}")
    @ApiGroup(group = {"user"})
    @ApiOperation(value = "查询用户信用评分",notes = "用户id")
    public Result getCredit(@PathVariable String user_id){
        return tmp.getCredit(user_id);
    }


    @Transactional
    @PostMapping(value = "/register")
    @ApiGroup(group = {"user"})
    @ApiOperation(value="注册",notes="用户名字，邮箱（可无），手机，密码")
    public Result register(@RequestParam("name") String name,
                           @RequestParam("mail") String mail,
                           @RequestParam("phone") String phone,
                           @RequestParam("pwd") String pwd) {
        return tmp.addUser(name,mail,phone,pwd);
    }

    @Transactional
    @PostMapping(value = "/setUserName")
    @ApiGroup(group = {"user"})
    @ApiOperation(value = "设置用户名字",notes="用户id，用户新名字")
    public void setUserName(@RequestParam("user_id") String user_id,
                            @RequestParam("name") String name){
        tmp.setUserName(user_id,name);
    }

    @Transactional
    @PostMapping(value = "/setUserAge")
    @ApiGroup(group = {"user"})
    @ApiOperation(value = "设置用户年龄",notes="用户id，用户年龄")
    public void setUserAge(@RequestParam("user_id") String user_id,
                           @RequestParam("age") Integer age) {
        tmp.setUserAge(user_id,age);
    }

    @PostMapping(value = "/setUserSex")
    @ApiGroup(group = {"user"})
    @ApiOperation(value = "设置用户性别",notes = "用户id，性别")
    public void setUserSex(@RequestParam("user_id") String user_id,
                           @RequestParam("sex") String sex) {
        tmp.setUserSex(user_id,sex);
    }

    @Transactional
    @PostMapping(value = "/setUserMail")
    @ApiGroup(group = {"user"})
    @ApiOperation(value = "设置用户邮箱",notes = "用户id，邮箱")
    public void setUserMail(@RequestParam("user_id") String user_id,
                            @RequestParam("mail") String mail) {
        tmp.setUserMail(user_id,mail);
    }


    @Transactional
    @PostMapping(value = "/setUserPwd")
    @ApiGroup(group = {"user"})
    @ApiOperation(value = "设置用户密码",notes = "用户id，新密码")
    public void setUserPwd(@RequestParam("user_id") String user_id,
                           @RequestParam("pwd") String pwd) {
        tmp.setUserPwd(user_id,pwd);
    }

    @Transactional
    @PostMapping(value = "/setUserBalance")
    @ApiGroup(group = {"user"})
    @ApiOperation(value = "设置用户余额",notes = "用户id，余额")
    public void setUserBalance(@RequestParam("user_id") String user_id,
                               @RequestParam("balance") double balance) {
        tmp.setUserBalance(user_id,balance);
    }


    @Transactional
    @PostMapping("/deleteUser")
    @ApiGroup(group = {"user"})
    @ApiOperation(value="注销用户",notes="用户id")//还需要完成后续操作，下架商品，删除订单，删除浏览、收藏、关注
    public Result deleteUser(@RequestParam("user_id") String user_id)
    {
        return tmp.deleteUser(user_id);
    }

    @GetMapping("/getAllAddress/{user_id}")
    @ApiGroup(group = {"user"})
    @ApiOperation(value = "获取用户的所有地址",notes="用户id")
    public Result getAllAddress(@PathVariable String user_id){
        return tmp.getAllAddress(user_id);
    }

    @Transactional
    @PostMapping("/newAddress")
    @ApiGroup(group = {"user"})
    @ApiOperation(value = "新增地址",notes = "用户id，新地址")
    public void newAddress(@RequestParam("user_id") String user_id,
                           @RequestParam("address") String address){
        tmp.newAddress(user_id,address);
    }

    @Transactional
    @PostMapping("/removeAddress")
    @ApiGroup(group = {"user"})
    @ApiOperation(value = "移除地址",notes = "用户id，新地址")
    public void removeAddress(@RequestParam("user_id") String user_id,
                           @RequestParam("address") String address){
        tmp.removeOneAddress(user_id,address);
    }
}
