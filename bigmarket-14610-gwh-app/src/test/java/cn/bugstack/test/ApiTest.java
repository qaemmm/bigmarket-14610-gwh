package cn.bugstack.test;

import cn.bugstack.infrastructure.dao.IRaffleActivityAccountDao;
import cn.bugstack.infrastructure.dao.po.RaffleActivityAccount;
import cn.bugstack.infrastructure.redis.IRedisService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApiTest {

    @Autowired
    private IRedisService redisService;
    @Test
    public void test() {
        log.info("测试完成");
        Integer value = (Integer)redisService.getValue("k2");
        log.info("测试{}", value);
        redisService.setValue("k2", 1);
        long k2 = redisService.incr("k2");
        System.out.println(k2);

    }
    @Autowired
    private IRaffleActivityAccountDao raffleActivityAccountDao;
    @Resource
    private ThreadPoolExecutor executor;

    @Test
    public void insert_user_account(){

        for (int i = 1; i <= 10000; i++) {
            int finalI = i;
            executor.execute(() -> {
                try {
                    String userId = "gwh" + finalI;
                    RaffleActivityAccount raffleActivityAccount = new RaffleActivityAccount();
                    raffleActivityAccount.setUserId(userId);
                    raffleActivityAccount.setActivityId(100301L);
                    raffleActivityAccount.setTotalCount(100);
                    raffleActivityAccount.setTotalCountSurplus(100);
                    raffleActivityAccount.setMonthCount(100);
                    raffleActivityAccount.setMonthCountSurplus(100);
                    raffleActivityAccount.setDayCount(100);
                    raffleActivityAccount.setDayCountSurplus(100);
                    raffleActivityAccountDao.insert(raffleActivityAccount);
                } catch (Exception e) {
                    log.error(e.getMessage());
                    e.printStackTrace();  // 记录异常日志
                }
            });


        }
    }

}
