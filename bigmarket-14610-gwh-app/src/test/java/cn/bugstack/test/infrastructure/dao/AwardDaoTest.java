package cn.bugstack.test.infrastructure.dao;

import cn.bugstack.infrastructure.dao.IAwardDao;
import cn.bugstack.infrastructure.dao.IStrategyAwardDao;
import cn.bugstack.infrastructure.dao.po.Award;
import cn.bugstack.infrastructure.dao.po.StrategyAward;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author fuzhouling
 * @date 2024/08/14
 * @program bigmarket-14610-gwh
 * @description todo desc...
 **/
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class AwardDaoTest {

    @Resource
    private IAwardDao awardDao;

    @Test
    public void test_queryAwardList() {
        List<Award> awards = awardDao.queryAwardList();
        log.info("测试结果：{}", JSON.toJSONString(awards));
        Award award = awardDao.queryAwardById(102);
        log.info("测试结果：{}", award);


    }

    @Resource
    private IStrategyAwardDao strategyAwardDao;

    @Test
    public void test_strategyAwardDao(){
        List<StrategyAward> strategyListByStrategyId = strategyAwardDao.getStrategyListByStrategyId(100001L);
        for (StrategyAward strategyAward : strategyListByStrategyId){
            log.info("测试结果：{}", JSON.toJSONString(strategyAward.getAwardId()));
            log.info("测试结果：{}", JSON.toJSONString(strategyAward.getAwardRate()));
        }
        log.info("测试结果：{}", JSON.toJSONString(strategyListByStrategyId));
    }
}