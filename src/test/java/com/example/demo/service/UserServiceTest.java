package com.example.demo.service;

import com.example.demo.DemoApplication;
import com.example.demo.model.User;
import javafx.application.Application;
import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = DemoApplication.class)
class UserServiceTest {
    @Autowired
    UserService userService;


    @Test
    void existsUser() {
        Assert.assertEquals(true,userService.existsUser("hth"));
        Assert.assertEquals(true,userService.existsUser("cyf"));
        Assert.assertEquals(false,userService.existsUser("hth1"));
    }

    @Test
    void getById() {
        User user = (User) userService.getById("hth").getObject();
        Assert.assertEquals("黄天浩",user.getName());

        user = (User) userService.getById("hjs").getObject();
        Assert.assertEquals("胡劲松",user.getName());
        Assert.assertTrue(!user.getName().equals("黄天浩"));

        Assert.assertNull(userService.getById("hth1").getObject());
    }

    @Test
    void getUsersByName() {
        List<User> users = (List<User>) userService.getUsersByName("黄天浩").getObject();
        if(users.size()>0) {
            for(User user:users) {
                Assert.assertEquals("黄天浩", user.getName());
            }
        }else{
            Assert.assertTrue(users.size()==0);
        }

        users = (List<User>) userService.getUsersByName("胡劲松").getObject();
        if(users.size()>0) {
            for(User user:users) {
                Assert.assertEquals("胡劲松", user.getName());
                Assert.assertFalse("黄天浩".equals(user.getName()));
            }
        }else{
            Assert.assertTrue(users.size()==0);
        }

        users=(List<User>)userService.getUsersByName("越前龙马").getObject();
        Assert.assertTrue(users.size()==0);
    }

    @Test
    void getBalance() {
    }

    @Test
    void checkPasswordById() {
    }

    @Test
    void getAllAddress() {
    }
}