package cn.bugstack.trigger.api;

import cn.bugstack.trigger.api.dto.RaffleAwardListRequestDTO;
import cn.bugstack.trigger.api.dto.RaffleAwardListResponseDTO;
import cn.bugstack.trigger.api.dto.RaffleStrategyRequestDTO;
import cn.bugstack.trigger.api.dto.RaffleStrategyResponseDTO;
import cn.bugstack.types.model.Response;

import java.util.List;

/**
 * @author fuzhouling
 * @date 2024/09/26
 * @program bigmarket-14610-gwh
 * @description todo desc...
 **/
public interface IRaffleStrategyService {
    /**
     * 策略装配接口
     * @param strategyId 策略id
     * @return 返回装配结果
     */
    Response<Boolean> strategyArmory(Long strategyId);

    /**
     * 查询抽奖奖品列表配置
     * @param raffleAwardListRequestDTO 抽奖奖品列表查询请求参数
     * @return 奖品列表数据
     */
    Response<List<RaffleAwardListResponseDTO>> queryRaffleAwardList(RaffleAwardListRequestDTO raffleAwardListRequestDTO);

    /**
     * 随机抽奖
     * @param raffleRequestDTO 请求参数
     * @return 抽奖结果
     */
    Response<RaffleStrategyResponseDTO> randomRaffle(RaffleStrategyRequestDTO raffleRequestDTO);
}
