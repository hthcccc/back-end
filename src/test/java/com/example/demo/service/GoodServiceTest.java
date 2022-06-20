package com.example.demo.service;

import com.example.demo.DemoApplication;
import com.example.demo.model.Good;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = DemoApplication.class)
class GoodServiceTest {

    @Autowired
    GoodService goodService;
    @Autowired
    HistoryService historyService;

    @Test
    void getById() {
        Map<String,Object> result = (Map<String,Object>)goodService.getById("0727295953745280").getObject();
        String name=result.get("name").toString();
        Assert.assertEquals("娃哈哈大礼包",name);

        result = (Map<String,Object>)goodService.getById("0214883378037189").getObject();
        name=result.get("name").toString();
        Assert.assertEquals("黑白女士大衣",name);
    }

    @Test
    void getAllGoodsToBeAudited() {
        List<Good> goods = (List<Good>) goodService.getAllGoodsToBeAudited().getObject();
        for(Good good : goods){
            Assert.assertEquals("待审核",good.getGoodState());
        }
    }

    /**
     集成测试
     */
    @Test
    void browseGood() {
        //获取当前时间戳
        Instant testTime = Instant.now().plusMillis(TimeUnit.HOURS.toMillis(8));
        System.out.println("testTime= "+testTime);

        //测试是否获取商品信息
        Map<String,Object> good = (Map<String,Object>)goodService.browseGood("hth","0762113045860999").getObject();
        Assert.assertEquals("0762113045860999",good.get("good_id").toString());
        Assert.assertEquals("孔雀蓝裤子",good.get("name").toString());

        //测试历史记录有无更新
        boolean isUpdated = false;
        List<Map<String,Object>> histories = (List<Map<String,Object>>)historyService.getHistory("hth").getObject();
        for(Map<String,Object> history : histories){
            if(history.get("good_id").toString().equals("0762113045860999")){
                Instant date = (Instant) history.get("date");
                if(testTime.plusMillis(TimeUnit.SECONDS.toMillis(60)).isAfter(date)){
                    isUpdated=true;
                    break;
                }
            }else {
                continue;
            }
        }
        Assert.assertTrue(isUpdated);
    }

    @Test
    void getGoodByUser() {
        List<Good> goods = (List<Good>) goodService.getGoodByUser("hth").getObject();
        for(Good good : goods){
            Assert.assertEquals("hth",good.getSellerId());
        }
    }

    @Test
    void getGoodOnShellByPart() {
    }

    @Test
    void releaseGood() {
    }

    @Test
    void calculateSum() {
    }
}