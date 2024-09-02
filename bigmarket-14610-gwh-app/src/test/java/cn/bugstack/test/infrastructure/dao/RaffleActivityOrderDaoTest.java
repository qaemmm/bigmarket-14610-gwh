package cn.bugstack.test.infrastructure.dao;

import cn.bugstack.infrastructure.persistent.dao.IRaffleActivityOrderDao;
import cn.bugstack.infrastructure.persistent.po.RaffleActivityOrder;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author fuzhouling
 * @date 2024/09/02
 * @program bigmarket-14610-gwh
 * @description todo desc...
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class RaffleActivityOrderDaoTest {
    @Resource
    private IRaffleActivityOrderDao raffleActivityOrderDao;

    @Test
    public void test_query(){

        List<RaffleActivityOrder> vLhpfQGTMDYpsBZxvfBoeygb = raffleActivityOrderDao.queryRaffleActivityOrderByUserId("gwh");
        log.info("测试结果 {}", JSON.toJSONString(vLhpfQGTMDYpsBZxvfBoeygb));
    }

    @Test
    public void test_insert(){
        RaffleActivityOrder raffleActivityOrder = new RaffleActivityOrder();
        raffleActivityOrder.setUserId("gwh");
        raffleActivityOrder.setActivityId(6L);
        raffleActivityOrder.setActivityName("测试活动");
        raffleActivityOrder.setStrategyId(100006L);
        raffleActivityOrder.setOrderId(RandomStringUtils.randomAlphanumeric(12));
        raffleActivityOrder.setOrderTime(new Date());
        raffleActivityOrder.setState("OKK");
        raffleActivityOrderDao.insert(raffleActivityOrder);
    }

    @Test
    public void test_insert_random() {
        RaffleActivityOrder raffleActivityOrder = new RaffleActivityOrder();
        raffleActivityOrder.setUserId("gwh");
//        raffleActivityOrder.setUserId(easyRandom.nextObject(String.class));
        raffleActivityOrder.setActivityId(6L);
        raffleActivityOrder.setActivityName("测试活动");
        raffleActivityOrder.setStrategyId(100006L);
        raffleActivityOrder.setOrderId(RandomStringUtils.randomAlphanumeric(12));
        raffleActivityOrder.setOrderTime(new Date());
        raffleActivityOrder.setState("OKK");
        raffleActivityOrderDao.insert(raffleActivityOrder);
    }
}
