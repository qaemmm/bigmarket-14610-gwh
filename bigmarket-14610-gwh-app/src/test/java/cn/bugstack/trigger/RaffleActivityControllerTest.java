package cn.bugstack.trigger;

import cn.bugstack.trigger.api.IRaffleActivityService;
import cn.bugstack.types.model.Response;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class RaffleActivityControllerTest {
    @Autowired
    private IRaffleActivityService raffleActivityService;
    @Test
    public void test_calendarSignRebate(){
        Response<Boolean> response = raffleActivityService.calenderSignRebate("xiaofuge");
        log.info("测试结果：{}", JSON.toJSONString(response));
    }
}
