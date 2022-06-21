package com.example.demo.service;

import com.example.demo.DemoApplication;
import com.example.demo.model.Good;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;


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


    @Transactional
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
        List<Map<String,Object>> goods = (List<Map<String,Object>>)goodService.getGoodOnShellByPart("服装").getObject();
        for(Map<String,Object> good : goods){
            Assert.assertEquals("上架中",good.get("good_state").toString());
            Assert.assertEquals("服装",good.get("part").toString());
        }
    }

    @Transactional
    @Test
    void releaseGood() {
        String good_id = goodService.releaseGood("lh","仅供测试","玩具",10000,"仅供测试","上海市",10.0,1.0,null).getObject().toString();
        Map<String,Object> result = (Map<String,Object>)goodService.getById(good_id).getObject();
        String name = result.get("name").toString();
        String state = result.get("good_state").toString();
        String seller_id =  result.get("seller_id").toString();
        Assert.assertEquals("仅供测试",name);
        Assert.assertEquals("lh",seller_id);
        Assert.assertEquals("待审核",state);
    }

    @Test
    void calculateSum() {
        Double sum = (Double) goodService.calculateSum("0840289660372971",2).getObject();
        Assert.assertTrue(sum==21);
    }


    @Transactional
    @Test
    void setGood() {
        //测试用例
        String good_id = "0840289660372971";
        //测试前状态
        Map<String,Object> origin = (Map<String,Object>)goodService.getById(good_id).getObject();
        Integer inventory_origin = (Integer) origin.get("inventory");
        //测试后验证
        goodService.setGood(good_id,"仅供测试1","玩具",-1,null,null,-1.0,-1.0,null);
        Map<String,Object> result = (Map<String,Object>)goodService.getById(good_id).getObject();
        String name = result.get("name").toString();
        String state = result.get("good_state").toString();
        String id =  result.get("good_id").toString();
        String part =  result.get("part").toString();
        Integer inventory = (Integer) result.get("inventory");
        Assert.assertEquals("仅供测试1",name);
        Assert.assertEquals(good_id,id);
        Assert.assertEquals("待审核",state);
        Assert.assertEquals("玩具",part);
        Assert.assertTrue(inventory==inventory_origin);
    }

    @Transactional
    @Test
    void allowGood(){
        //测试待审核用例
        String good_id = "8402893660372971";
        //测试上架后状态
        goodService.allowGood(good_id,"1");
        Map<String,Object> result = (Map<String,Object>)goodService.getById(good_id).getObject();
        String good_state = result.get("good_state").toString();
        Assert.assertEquals("上架中",good_state);

        //测试待整改用例
        good_id = "4028936603729721";
        //测试上架后状态
        goodService.allowGood(good_id,"1");
        result = (Map<String,Object>)goodService.getById(good_id).getObject();
        good_state = result.get("good_state").toString();
        Assert.assertEquals("上架中",good_state);
    }

    @Transactional
    @Test
    void takeDownGood(){
        //测试用例
        String good_id = "8402893660372971";
        //测试下架后状态
        goodService.allowGood(good_id,"0");
        Map<String,Object> result = (Map<String,Object>)goodService.getById(good_id).getObject();
        String  good_state =  result.get("good_state").toString();
        Assert.assertEquals("已下架",good_state);

        //测试待整改用例
        good_id = "4028936603729721";
        //测试下架后状态
        goodService.allowGood(good_id,"0");
        result = (Map<String,Object>)goodService.getById(good_id).getObject();
        good_state = result.get("good_state").toString();
        Assert.assertEquals("已下架",good_state);
    }

    @Transactional
    @Test
    void requireGoodForSetting(){
        //测试用例
        String good_id = "8402893660372971";
        //测试整改后状态
        goodService.allowGood(good_id,"2");
        Map<String,Object> result = (Map<String,Object>)goodService.getById(good_id).getObject();
        String  good_state =  result.get("good_state").toString();
        Assert.assertEquals("待整改",good_state);

        //测试待整改用例
        good_id = "4028936603729721";
        //测试整改后状态
        goodService.allowGood(good_id,"2");
        result = (Map<String,Object>)goodService.getById(good_id).getObject();
        good_state = result.get("good_state").toString();
        Assert.assertEquals("待整改",good_state);
    }
}