package cn.bugstack.test.infrastructure.dao;

import cn.bugstack.infrastructure.dao.IRaffleActivityDao;
import cn.bugstack.infrastructure.dao.po.RaffleActivity;
import com.alibaba.fastjson2.JSON;
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
@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class RaffleActivityDaoTest {
    @Resource
    private IRaffleActivityDao raffleActivityDao;

    @Test
    public void test_query(){
        RaffleActivity raffleActivity = raffleActivityDao.selectByActivityId(100301L);
        log.info("测试结果 {}", JSON.toJSONString(raffleActivity));
    }
}
