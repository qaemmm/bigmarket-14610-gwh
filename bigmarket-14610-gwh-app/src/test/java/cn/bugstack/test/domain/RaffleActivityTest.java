package cn.bugstack.test.domain;

import cn.bugstack.domain.activity.model.entity.ActivityOrderEntity;
import cn.bugstack.domain.activity.model.entity.ActivityShopCartEntity;
import cn.bugstack.domain.activity.service.RaffleActivity;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author fuzhouling
 * @date 2024/09/02
 * @program bigmarket-14610-gwh
 * @description todo desc...
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class RaffleActivityTest {
    @Resource
    private RaffleActivity raffleActivity;

    @Test
    public void test_raffle_activity_order(){
        ActivityShopCartEntity activityShopCartEntity = new ActivityShopCartEntity();
        activityShopCartEntity.setUserId("gwh");
        activityShopCartEntity.setSku(9011L);
        ActivityOrderEntity activityOrderEntity = raffleActivity.createRaffleActivityOrder(activityShopCartEntity);
        log.info("测试结果 {}", activityOrderEntity);
    }
}
