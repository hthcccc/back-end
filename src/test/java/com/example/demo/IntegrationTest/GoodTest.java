package com.example.demo.IntegrationTest;

import com.example.demo.DemoApplication;
import com.example.demo.service.GoodService;
import com.example.demo.service.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = DemoApplication.class)
public class GoodTest {
    @Autowired
    UserService userService;
    @Autowired
    GoodService goodService;

    /**
     * 登录——发布——审核通过
     */
    @Transactional
    @Test
    public void GoodCase1(){
        String user_id = "hth";
        String good_id = goodService.releaseGood(user_id,"仅供测试","服装",10,"仅供测试","上海市",10.0,0.0,null).getObject().toString();
        Map<String,Object> good = (Map<String, Object>) goodService.getById(good_id).getObject();
        String good_state_0 = good.get("good_state").toString();
        Assert.assertEquals("待审核",good_state_0);

        goodService.allowGood(good_id,"1");
        good = (Map<String, Object>) goodService.getById(good_id).getObject();
        String good_state_1 = good.get("good_state").toString();
        Assert.assertEquals("上架中",good_state_1);
    }
}
