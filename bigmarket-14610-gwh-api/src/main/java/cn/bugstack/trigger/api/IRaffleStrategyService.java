package cn.bugstack.trigger.api;

import cn.bugstack.trigger.api.dto.*;
import cn.bugstack.types.model.Response;

import java.util.List;

/**
 * @author fuzhouling
 * @date 2024/09/26
 * @program bigmarket-14610-gwh
 * @description 活动策略服务
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

    /**
     * 查询抽奖策略权重规则，给用户展示出抽奖N次后必中奖奖品范围
     *
     * @param request 请求对象
     * @return 权重奖品配置列表「这里会返回全部，前端可按需展示一条已达标的，或者一条要达标的」
     */
    Response<List<RaffleStrategyRuleWeightResponseDTO>> queryRaffleStrategyRuleWeight(RaffleStrategyRuleWeightRequestDTO request);}
