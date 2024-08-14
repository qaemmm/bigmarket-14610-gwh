package cn.bugstack.test.domain;

import cn.bugstack.domain.strategy.service.armory.IStrategyArmory;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
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

    @Test
    public void test_StrategyArmory(){
        boolean b = strategyArmory.assembleLotteryStrategy(100002L);
        log.info("测试结果:{}",b);
    }

    @Test
    public void test_getAssembleRandmVal(){
        log.info("测试结果：{} - 奖品ID值", strategyArmory.getRandomAwardId(100002L));
    }
}
