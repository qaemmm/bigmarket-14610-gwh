package cn.bugstack.test.infrastructure;

import cn.bugstack.domain.strategy.model.valobj.RuleTreeVO;
import cn.bugstack.domain.strategy.repository.IStrategyRepository;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author fuzhouling
 * @date 2024/08/21
 * @program bigmarket-14610-gwh
 * @description todo desc...
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class StrategyRepositoryTest {

     @Autowired
     private IStrategyRepository strategyRepository;

     @Test
    public void testRuleTree(){
         RuleTreeVO ruleTreeVO = strategyRepository.queryRuleTreeVoByTreeId("tree_lock");
         log.info("ruleTreeVO:{}", JSON.toJSONString(ruleTreeVO));
    }

}
