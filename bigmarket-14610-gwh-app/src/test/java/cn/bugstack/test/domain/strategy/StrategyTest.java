package cn.bugstack.test.domain.strategy;

import cn.bugstack.domain.strategy.service.armory.IStrategyArmory;
import cn.bugstack.domain.strategy.service.armory.IStrategyDispatch;
import cn.bugstack.infrastructure.dao.IStrategyAwardDao;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author fuzhouling
 * @date 2024/08/14
 * @program bigmarket-14610-gwh
 * @description todo desc...
 **/
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class StrategyTest {
    @Resource
    private IStrategyArmory strategyArmory;
    @Resource
    private IStrategyDispatch strategyDispatch;

    @Before
    public void test_StrategyArmory(){
        boolean b = strategyArmory.assembleLotteryStrategy(100001L);
        log.info("测试结果:{}",b);
    }
    @Autowired
    private IStrategyAwardDao strategyAwardDao;

    @Test
    public void test_strategyDispatch(){
        log.info("测试结果：{} - 奖品ID值", strategyDispatch.getRandomAwardId(100001L));
    }

    @Test
    public void test_getRandomAwardId_ruleWeightValue(){
        log.info("测试结果：{} - 4000奖品ID值", strategyDispatch.getRandomAwardId(100001L, "4000:102,103,104,105"));
        log.info("测试结果：{} - 5000奖品ID值", strategyDispatch.getRandomAwardId(100001L, "5000:102,103,104,105,106,107"));
        log.info("测试结果：{} - 6000奖品ID值", strategyDispatch.getRandomAwardId(100001L, "6000:102,103,104,105,106,107,108,109"));
    }

    @Test
    public void test_getAssembleRandmVal(){
        strategyArmory.assembleLotteryStrategy(100001L);
//        log.info("测试结果：{} - 奖品ID值", strategyArmory.getRandomAwardId(100001L));
    }
}
