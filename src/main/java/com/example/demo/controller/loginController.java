package com.example.demo.controller;

import com.example.demo.config.ApiGroup;
import com.example.demo.result.Result;
import com.example.demo.result.ResultFactory;
import com.example.demo.service.AdminService;
import com.example.demo.service.UserService;
import com.example.demo.service.VerificationService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins="*")
@RestController("login")
@RequestMapping("/login")
public class loginController {
    @Autowired
    AdminService adminService;
    @Autowired
    UserService userService;
    @Autowired
    VerificationService msgService;

    @PostMapping("/checkPasswordById")
    @ApiGroup(group = {"user","admin"})
    @ApiOperation(value = "统一登录认证接口", notes = "账号id，密码\n如果是用户返回code：200\n如果是管理员，返回code：201")
    public Result checkPasswordById(@RequestParam("id") String id,
                                @RequestParam("password") String password){
        if(userService.existsUser(id)){
            return userService.checkPasswordById(id,password);
        }else if(adminService.existsAdmin(id)){
            return adminService.checkPasswordById(id,password);
        }else{
            return ResultFactory.buildFailResult("不存在该id");
        }
    }

    @PostMapping("/checkPasswordByMail")
    @ApiGroup(group = {"user","admin"})
    @ApiOperation(value = "统一登录认证接口", notes = "邮箱，密码\n如果是用户返回code：200\n如果是管理员，返回code：201")
    public Result checkPasswordByMail(@RequestParam("mail") String mail,
                                    @RequestParam("password") String password){
        if(userService.existsMail(mail)){
            return userService.checkPasswordByMail(mail,password);
        }else if(adminService.existsMail(mail)){
            return adminService.checkPasswordByMail(mail,password);
        }else{
            return ResultFactory.buildFailResult("该邮箱未绑定账号");
        }
    }

    @PostMapping("/sendMessage")
    @ApiGroup(group = {"user"})
    @ApiOperation(value = "发送短信验证码", notes = "手机号")
    public Result sendMessage(@RequestParam("phone") String phone){
        return msgService.sendMessage(phone);
    }

    @PostMapping("/checkPhone")
    @ApiGroup(group = {"user"})
    @ApiOperation(value = "校验短信验证码，如果手机号未绑定就注册新账号", notes = "手机号")
    public Result checkPhone(@RequestParam("phone") String phone,
                             @RequestParam("code") String code,
                             @RequestParam("name") String name){
        return msgService.checkPhone(phone,code,name);
    }
}
