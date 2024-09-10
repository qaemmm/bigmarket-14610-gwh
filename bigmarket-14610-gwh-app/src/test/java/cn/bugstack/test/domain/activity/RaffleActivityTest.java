package cn.bugstack.test.domain.activity;

import cn.bugstack.domain.activity.model.entity.ActivityOrderEntity;
import cn.bugstack.domain.activity.model.entity.ActivityShopCartEntity;
import cn.bugstack.domain.activity.model.entity.SkuRechargeEntity;
import cn.bugstack.domain.activity.service.IRaffleOrder;
import cn.bugstack.domain.activity.service.RaffleActivityService;
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
    private RaffleActivityService raffleActivity;
    @Resource
    private IRaffleOrder raffleOrder;

    @Test
    public void test_raffle_activity_order(){
        ActivityShopCartEntity activityShopCartEntity = new ActivityShopCartEntity();
        activityShopCartEntity.setUserId("gwh");
        activityShopCartEntity.setSku(9011L);
        ActivityOrderEntity activityOrderEntity = raffleActivity.createRaffleActivityOrder(activityShopCartEntity);
        log.info("测试结果 {}", activityOrderEntity);
    }

    @Test
    public void test_createSkuRechargeOrder(){
        SkuRechargeEntity skuRechargeEntity = new SkuRechargeEntity();
        skuRechargeEntity.setUserId("xiaofuge");
        skuRechargeEntity.setSku(9011L);
        // outBusinessNo 作为幂等仿重使用，同一个业务单号2次使用会抛出索引冲突 Duplicate entry '700091009111' for key 'uq_out_business_no' 确保唯一性。
        skuRechargeEntity.setOutBusinessNo("7000910091116");
        String skuRechargeOrder = raffleOrder.createSkuRechargeOrder(skuRechargeEntity);
        log.info("测试结果 {}", skuRechargeOrder);
    }
}
