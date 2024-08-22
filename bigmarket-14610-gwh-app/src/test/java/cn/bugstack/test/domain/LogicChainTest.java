package cn.bugstack.test.domain;

import cn.bugstack.domain.strategy.service.rule.chain.ILogicChain;
import cn.bugstack.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import cn.bugstack.domain.strategy.service.rule.chain.impl.RuleWeightLogicChain;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import javax.annotation.Resource;

/**
 * @author fuzhouling
 * @date 2024/08/20
 * @program bigmarket-14610-gwh
 * @description 责任链模式测试
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class LogicChainTest {

    @Resource
    private DefaultChainFactory chainFactory;

    @Resource
    private RuleWeightLogicChain ruleWeightLogicChain;
    @Test
    public void test_LogicChain_rule_blacklist(){

        ILogicChain iLogicChain = chainFactory.openLogicChain(100001L);
        Integer awardId = iLogicChain.logic("user001", 100001L).getAwardId();
        log.info("测试结果:{}",awardId);
    }

    @Test
    public void test_LogicChain_rule_weight() {
        ReflectionTestUtils.setField(ruleWeightLogicChain, "userScore", 40500L);
        ILogicChain iLogicChain = chainFactory.openLogicChain(100001L);
        DefaultChainFactory.StrategyAwardVO logic = iLogicChain.logic("gwh", 100001L);
        log.info("测试结果:{}", logic);
    }

    @Test
    public void test_LogicChain_rule_default() {
        ILogicChain logicChain = chainFactory.openLogicChain(100001L);
        DefaultChainFactory.StrategyAwardVO logic = logicChain.logic("gwh", 100001L);
        log.info("测试结果：{}", logic);
    }


}
