package cn.bugstack.test;

import cn.bugstack.infrastructure.persistent.redis.IRedisService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApiTest {

    @Autowired
    private IRedisService redisService;
    @Test
    public void test() {
        log.info("测试完成");
        Object k1 = redisService.getValue("k2");
        redisService.setValue("k2", 1);
        long k2 = redisService.incr("k2");
        System.out.println(k2);

    }

}
