package cn.bugstack.test.domain.activity;

import cn.bugstack.domain.activity.model.entity.SkuRechargeEntity;
import cn.bugstack.domain.activity.model.valobj.OrderTradeTypeVO;
import cn.bugstack.domain.activity.service.IRaffleActivityAccountQuotaService;
import cn.bugstack.domain.activity.service.quota.RaffleActivityAccountQuotaService;
import cn.bugstack.domain.activity.service.armory.IActivityArmory;
import cn.bugstack.trigger.api.IRaffleActivityService;
import cn.bugstack.types.exception.AppException;
import cn.bugstack.types.model.Response;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;

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
    private RaffleActivityAccountQuotaService raffleActivity;
    @Resource
    private IRaffleActivityAccountQuotaService raffleOrder;
    @Resource
    private IActivityArmory activityArmory;

    @Resource
    private IRaffleActivityService raffleActivityService;



    @Test
    public void test_createSkuRechargeOrder(){
        SkuRechargeEntity skuRechargeEntity = new SkuRechargeEntity();
        skuRechargeEntity.setUserId("liergou2");
        skuRechargeEntity.setSku(9011L);
        skuRechargeEntity.setOrderTradeType(OrderTradeTypeVO.credit_pay_trade);
        // outBusinessNo 作为幂等仿重使用，同一个业务单号2次使用会抛出索引冲突 Duplicate entry '700091009111' for key 'uq_out_business_no' 确保唯一性。
        skuRechargeEntity.setOutBusinessNo("70009100911164");
        String skuRechargeOrder = raffleOrder.createOrder(skuRechargeEntity);
        log.info("测试结果 {}", skuRechargeOrder);
    }

    /**
     * 测试库存消耗和最终一致更新
     * 1. raffle_activity_sku 库表库存可以设置20个
     * 2. 清空 redis 缓存 flushall
     * 3. for 循环20次，消耗完库存，最终数据库剩余库存为0
     */
    @Test
    public void test_raffle_activity_sku() throws Exception {
        activityArmory.assembleActivitySku(9011L);
        for (int i = 0; i < 20; i++) {
            try {
                SkuRechargeEntity skuRechargeEntity = new SkuRechargeEntity();
                skuRechargeEntity.setUserId("xiaofuge");
                skuRechargeEntity.setSku(9011L);
                // outBusinessNo 作为幂等仿重使用，同一个业务单号2次使用会抛出索引冲突 Duplicate entry '700091009111' for key 'uq_out_business_no' 确保唯一性。
                skuRechargeEntity.setOutBusinessNo(RandomStringUtils.randomNumeric(12));
                String orderId = raffleOrder.createOrder(skuRechargeEntity);
                log.info("测试结果：{}", orderId);
            } catch (AppException e) {
                log.warn(e.getInfo());
            }
        }
        new CountDownLatch(1).await();
    }

}
