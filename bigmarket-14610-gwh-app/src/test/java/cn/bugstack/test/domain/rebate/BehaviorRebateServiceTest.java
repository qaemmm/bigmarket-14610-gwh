package cn.bugstack.test.domain.rebate;

import cn.bugstack.domain.activity.service.armory.IActivityArmory;
import cn.bugstack.domain.rebate.model.entity.BehaviorEntity;
import cn.bugstack.domain.rebate.model.valobj.BehaviorTypeVO;
import cn.bugstack.domain.rebate.service.IBehaviorRebateService;
import cn.bugstack.domain.strategy.service.armory.IStrategyArmory;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author fuzhouling
 * @date 2024/09/30
 * @program bigmarket-14610-gwh
 * @description 抽奖行为返利服务测试
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class BehaviorRebateServiceTest {

    @Resource
    private IBehaviorRebateService behaviorRebateService;
    @Resource
    private IActivityArmory activityArmory;
    @Resource
    private IStrategyArmory strategyArmory;


    @Test
//    @Before
    public void setup() {
        log.info("测试开始");
        Long activityId = 100301L;
        //活动装配-activitySkuEntity
        activityArmory.assembleActivityByActivityId(activityId);
        //策略装配-StrategyAwardEntity
        strategyArmory.assembleLotteryStrategyByActivityId(activityId);
    }
    @Test
    public void test_createOrder() throws InterruptedException {
        BehaviorEntity behaviorEntity = new BehaviorEntity();
        behaviorEntity.setUserId("xiaofuge");
        behaviorEntity.setBehaviorTypeVO(BehaviorTypeVO.SIGN);
        behaviorEntity.setOutBusinessNo("20241210");
        List<String> order = behaviorRebateService.createOrder(behaviorEntity);
        for (int i = 0; i < order.size(); i++) {
            log.info("生成订单号：{}", order.get(i));
        }
        //这一块就是先等等监听器，不然测试完了就结束了
        new CountDownLatch(1).await();
    }
}
