package com.example.demo.controller;

import com.example.demo.config.ApiGroup;
import com.example.demo.result.Result;
import com.example.demo.service.AdminService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@CrossOrigin(origins="*")
@RestController("admin")
@RequestMapping("/admin")
public class adminController {
    @Autowired
    AdminService tmp;

    @PostMapping("/addAdmin")
    @ApiGroup(group = {"admin"})
    @ApiOperation(value = "管理员注册,仅供测试", notes = "姓名，邮箱（可无），手机，密码")
    public Result admin(@RequestParam("name") String name,
                        @RequestParam("mail") String mail,
                        @RequestParam("phone") String phone,
                        @RequestParam("password") String password){
        return tmp.addAdmin(name,mail,phone,password);
    }

//    @PostMapping("/checkPassword")
//    @ApiGroup(group = {"admin"})
//    @ApiOperation(value = "管理员登录", notes = "管理员id，密码")
//    public Result checkPassword(@RequestParam("admin_id") String admin_id,
//                        @RequestParam("password") String password){
//        return tmp.checkPassword(admin_id,password);
//    }

    @GetMapping("/getAdmin/{admin_id}")
    @ApiGroup(group = {"admin"})
    @ApiOperation(value = "获取管理员信息", notes = "管理员id，密码")
    public Result checkPassword(@PathVariable("admin_id") String admin_id){
        return tmp.getAdmin(admin_id);
    }
}
