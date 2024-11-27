package cn.bugstack.trigger;

import cn.bugstack.trigger.api.IRaffleActivityService;
import cn.bugstack.trigger.api.dto.ActivityDrawRequestDTO;
import cn.bugstack.trigger.api.dto.ActivityDrawResponseDTO;
import cn.bugstack.trigger.api.dto.UserActivityAccountRequestDTO;
import cn.bugstack.trigger.api.dto.UserActivityAccountResponseDTO;
import cn.bugstack.types.model.Response;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.CountDownLatch;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class RaffleActivityControllerTest {
    @Autowired
    private IRaffleActivityService raffleActivityService;
    @Test
    public void test_calendarSignRebate(){
        Response<Boolean> response = raffleActivityService.calendarSignRebate("liergou3");
        log.info("测试结果：{}", JSON.toJSONString(response));
    }


    @Test
    public void test_isCalendarSignRebate() {
        Response<Boolean> response = raffleActivityService.isCalendarSignRebate("xiaofuge");
        log.info("测试结果：{}", JSON.toJSONString(response));
    }

    @Test
    public void test_queryUserActivityAccount() {
        UserActivityAccountRequestDTO request = new UserActivityAccountRequestDTO();
        request.setActivityId(100301L);
        request.setUserId("xiaofuge");
        // 查询数据
        Response<UserActivityAccountResponseDTO> response = raffleActivityService.queryUserActivityAccount(request);
        log.info("请求参数：{}", JSON.toJSONString(request));
        log.info("测试结果：{}", JSON.toJSONString(response));
    }

    @Test
    public void test_blacklist_draw() throws InterruptedException {
        ActivityDrawRequestDTO request = new ActivityDrawRequestDTO();
        request.setActivityId(100301L);
        request.setUserId("user003");
        Response<ActivityDrawResponseDTO> response = raffleActivityService.draw(request);

        log.info("请求参数：{}", JSON.toJSONString(request));
        log.info("测试结果：{}", JSON.toJSONString(response));

        // 让程序挺住方便测试，也可以去掉
        new CountDownLatch(1).await();
    }

//現在問題很大，執行一個抽奖逻辑的时候，总账户额度--日月对不上；执行一次签到之后发现一次会给账户+10???同时在运行mq的时候会报错。

}
