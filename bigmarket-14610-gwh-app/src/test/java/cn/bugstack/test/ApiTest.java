package cn.bugstack.test;

import cn.bugstack.infrastructure.dao.IRaffleActivityAccountDao;
import cn.bugstack.infrastructure.dao.IUserAwardRecordDao;
import cn.bugstack.infrastructure.dao.IUserCreditAccountDao;
import cn.bugstack.infrastructure.dao.po.RaffleActivityAccount;
import cn.bugstack.infrastructure.dao.po.Task;
import cn.bugstack.infrastructure.dao.po.UserAwardRecord;
import cn.bugstack.infrastructure.redis.IRedisService;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.A;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.RedissonBlockingQueue;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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

        for (int i = 1; i <= 100000; i++) {
            int finalI = i;
            executor.execute(() -> {
                try {
                    String userId = "lubenwei" + finalI;
                    RaffleActivityAccount raffleActivityAccount = new RaffleActivityAccount();
                    raffleActivityAccount.setUserId(userId);
                    raffleActivityAccount.setActivityId(100301L);
                    raffleActivityAccount.setTotalCount(100000);
                    raffleActivityAccount.setTotalCountSurplus(100000);
                    raffleActivityAccount.setMonthCount(100000);
                    raffleActivityAccount.setMonthCountSurplus(100000);
                    raffleActivityAccount.setDayCount(100000);
                    raffleActivityAccount.setDayCountSurplus(100000);
//                    raffleActivityAccountDao.insert(raffleActivityAccount);
                    raffleActivityAccountDao.updateAccountQuota(raffleActivityAccount);
                } catch (Exception e) {
                    log.error(e.getMessage());
                    e.printStackTrace();  // 记录异常日志
                }
            });


        }
    }

    @Autowired
    private IUserAwardRecordDao userAwardRecordDao;




    @Test
    public void test_raffle_account(){
        RaffleActivityAccount raffleActivityAccountEntityReq = new RaffleActivityAccount();
        raffleActivityAccountEntityReq.setUserId("xfg239");
        raffleActivityAccountEntityReq.setActivityId(100301L);
        RaffleActivityAccount raffleActivityAccountEntity = raffleActivityAccountDao.queryActivityAccount(raffleActivityAccountEntityReq);
        System.out.println("hello--"+raffleActivityAccountEntity.toString());

        System.out.println("hello");
    }

    @Test
    public void test_userAwardRecord(){
        UserAwardRecord userAwardRecord = new UserAwardRecord();
        userAwardRecord.setUserId("xfg16");
        userAwardRecord.setOrderId("qBu3RomBdpjz");
        UserAwardRecord userAwardRecord1 = userAwardRecordDao.queryUserAwardRecord(userAwardRecord);
        System.out.println("hello+"+userAwardRecord1.toString());
    }

    @Test
    public void test_DelayQueue(){
        RBlockingQueue<Object> blockingQueue = redisService.getBlockingQueue("k2");
        RDelayedQueue<Object> delayedQueue = redisService.getDelayedQueue(blockingQueue);
        new Thread(() -> {
            while (true) {
                //一直取
                Object poll = delayedQueue.poll();
                if(poll != null){
                    System.out.println(poll);
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        }).start();
        int i=0;
        while(true){
            //一直存
            i++;
            delayedQueue.offer(new Object()+""+i,100, TimeUnit.MILLISECONDS);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }


}
