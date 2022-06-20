package com.example.demo.Test;

import com.example.demo.controller.userController;
import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class UserTest {
    @Resource
    userController userCtrl;

    @Test
    public void testGetUser()
    {
        User user =(User)userCtrl.getUser("hth").getObject();
        Assert.assertEquals("黄天浩",user.getName());
    }
}
