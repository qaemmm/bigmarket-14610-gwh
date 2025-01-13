package cn.bugstack.trigger.http;

import cn.bugstack.domain.activity.service.IRaffleActivityAccountQuotaService;
import cn.bugstack.domain.award.model.valobj.RuleWeightVO;
import cn.bugstack.domain.strategy.model.entity.StrategyAwardEntity;
import cn.bugstack.domain.strategy.service.IRaffleAward;
import cn.bugstack.domain.strategy.service.IRaffleRule;
import cn.bugstack.domain.strategy.service.IRaffleStrategy;
import cn.bugstack.domain.strategy.service.armory.IStrategyArmory;
import cn.bugstack.trigger.api.IRaffleStrategyService;
import cn.bugstack.trigger.api.dto.*;
import cn.bugstack.types.enums.ResponseCode;
import cn.bugstack.types.exception.AppException;
import cn.bugstack.types.model.Response;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author fuzhouling
 * @date 2024/08/28
 * @program bigmarket-14610-gwh
 * @description 抽奖服务
 **/
@RestController
@Slf4j
@CrossOrigin("${app.config.cross-origin}")
@RequestMapping("/api/${app.config.api-version}/raffle/strategy/")
@DubboService(version = "1.0")
public class RaffleStrategyController implements IRaffleStrategyService {

    @Resource
    private IStrategyArmory strategyArmory;

    @Resource
    private IRaffleAward raffleAward;

    @Resource
    private IRaffleStrategy raffleStrategy;

    @Resource
    private IRaffleRule raffleRule;

    @Resource
    private IRaffleActivityAccountQuotaService raffleActivityAccountQuotaService;

//    /**
//     * 策略装配，将策略信息装配到缓存中
//     * <a href="http://localhost:8091/api/v1/raffle/strategy_armory">/api/v1/raffle/strategy_armory</a>
//     *
//     * @param strategyId 策略ID
//     * @return 装配结果 --- 此处的装配抽奖可以在下面找的到对应
//     */
//    @RequestMapping(value = "strategy_armory",method = RequestMethod.GET)
    @Override
    public Response<Boolean> strategyArmory(@RequestParam Long strategyId) {
//        try{
//            log.info("抽奖策略装配开始:{}",strategyId);
//            boolean armoryStatus = strategyArmory.assembleLotteryStrategy(strategyId);
//            Response response = Response.<Boolean>builder()
//                    .code(ResponseCode.SUCCESS.getCode())
//                    .info(ResponseCode.SUCCESS.getInfo())
//                    .data(armoryStatus)
//                    .build();
//            log.info("抽奖策略装配完成:{}", response);
//            return response;
//        }catch(Exception e){
//            log.error("抽奖策略装配异常 strategyId{}",strategyId);
            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
//        }
    }

    /**
     * 查询奖品列表
     * <a href="http://localhost:8091/api/v1/raffle/query_raffle_award_list">/api/v1/raffle/query_raffle_award_list</a>
     * 请求参数 raw json
     *
     * @param request {"userId":"xiaofuge","activityId":100301L}
     * @return 通过用户id和活动id获取奖品列表
     */
    @RequestMapping(value = "query_raffle_award_list", method = RequestMethod.POST)
    @Override
    public Response<List<RaffleAwardListResponseDTO>> queryRaffleAwardList(@RequestBody RaffleAwardListRequestDTO request) {

        try{
            if(StringUtils.isBlank(request.getUserId())||null==request.getActivityId()){
               throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(),ResponseCode.ILLEGAL_PARAMETER.getInfo());
            }
            log.info("查询抽奖奖品列表配置开始userId:{},activityId:{}", request.getUserId(), request.getActivityId());
//            List<StrategyAwardEntity> strategyAwardEntities = raffleAward.queryRaffleAwardList(request.getStrategyId());
            List<StrategyAwardEntity> strategyAwardEntities = raffleAward.queryRaffleAwardListByActivityId(request.getActivityId());
            //3、获取规则配置
            String[] treeIds = strategyAwardEntities.stream()
                    .map(StrategyAwardEntity::getRuleModels)
                    .filter(StringUtils::isNotBlank)
                    .toArray(String[]::new);
            //4、查询规则配置，获取奖品的解锁限制，抽奖n次后解锁的值（key是tree_id,value是rule_value）
            Map<String, Integer> ruleValueByTreeIds = raffleRule.getRuleValueByTreeIds(treeIds);

            //获取到今天用户抽奖次数--额度
            Integer userTodayPartakeCount = raffleActivityAccountQuotaService.getUserTodayPartakeCount(request.getUserId(), request.getActivityId());
            log.info("查询抽奖今天抽奖次数配置完成userId:{},activityId:{} userTodayPartakeCount:{}", request.getUserId(), request.getActivityId(),userTodayPartakeCount);
            List<RaffleAwardListResponseDTO> raffleAwardListResponseDTOList = new ArrayList<>(strategyAwardEntities.size());
            for (StrategyAwardEntity strategyAwardEntity : strategyAwardEntities) {
                Integer awardRuleLockCount = ruleValueByTreeIds.get(strategyAwardEntity.getRuleModels());
                RaffleAwardListResponseDTO raffleAwardListResponseDTO = RaffleAwardListResponseDTO.builder()
                        .awardId(strategyAwardEntity.getAwardId())
                        .awardTitle(strategyAwardEntity.getAwardTitle())
                        .awardSubtitle(strategyAwardEntity.getAwardSubtitle())
                        .sort(strategyAwardEntity.getSort())
                        //如果活动本身没有配置锁次数，或者用户抽奖次数大于等于解锁次数，那么奖品是解锁的
                        .isAwardUnlock(null == awardRuleLockCount || userTodayPartakeCount >= awardRuleLockCount)
                        .awardRuleLockCount(awardRuleLockCount)
                        //活动本身没有配置锁次数，那么就是解锁的，同时用户抽奖次数大于等于解锁次数，那么奖品是解锁的
                        .waitUnlockCount(null== awardRuleLockCount||awardRuleLockCount<= userTodayPartakeCount?0:awardRuleLockCount-userTodayPartakeCount)
                        .build();
                raffleAwardListResponseDTOList.add(raffleAwardListResponseDTO);
                log.info("!!!!!!AwardId{}--isAwardUnlock:{}",raffleAwardListResponseDTO.getAwardId(),raffleAwardListResponseDTO.isAwardUnlock());
            }
            Response<List<RaffleAwardListResponseDTO>> response = Response.<List<RaffleAwardListResponseDTO>>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(raffleAwardListResponseDTOList)
                    .build();
            log.info("查询抽奖奖品列表配置完成userId:{},activityId:{} response:{}", request.getUserId(), request.getActivityId(),JSON.toJSONString(response));
            return response;
        }catch (Exception e){
            log.error("查询抽奖奖品列表配置异常userId:{},activityId:{}", request.getUserId(), request.getActivityId(),e);
            return Response.<List<RaffleAwardListResponseDTO>>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }

    }

    /**
     * 随机抽奖接口
     * <a href="http://localhost:8091/api/v1/raffle/random_raffle">/api/v1/raffle/random_raffle</a>
     *
     * @param requestDTO 请求参数 {"strategyId":1000001}
     * @return 抽奖结果
     */

    @RequestMapping(value = "random_raffle", method = RequestMethod.POST)
    @Override
    public Response<RaffleStrategyResponseDTO> randomRaffle(@RequestBody RaffleStrategyRequestDTO requestDTO) {
//        try{
//            log.info("随机抽奖开始 strategyId: {}", requestDTO.getStrategyId());
//            RaffleFactorEntity rateFactorEntity = RaffleFactorEntity.builder()
//                    .userId("system")
//                    .strategyId(requestDTO.getStrategyId())
//                    .build();
//            RaffleAwardEntity raffleAwardEntity = raffleStrategy.performRaffle(rateFactorEntity);
//            Response<RaffleStrategyResponseDTO> response = Response.<RaffleStrategyResponseDTO>builder()
//                    .code(ResponseCode.SUCCESS.getCode())
//                    .info(ResponseCode.SUCCESS.getInfo())
//                    .data(RaffleStrategyResponseDTO.builder()
//                            .awardId(raffleAwardEntity.getAwardId())
//                            .awardIndex(raffleAwardEntity.getSort())
//                            .build())
//                    .build();
//            log.info("随机抽奖完成 response:{}", JSON.toJSONString(response));
//            return response;
//        }catch (AppException e){
//         log.error("随机抽奖异常 strategyId: {} 异常：{}", requestDTO.getStrategyId(),e.getInfo());
//            return Response.<RaffleStrategyResponseDTO>builder()
//                    .code(e.getCode())
//                    .info(e.getInfo())
//                    .build();
//        }catch (Exception e){
//            log.error("随机抽奖异常 strategyId: {} 异常：{} ", requestDTO.getStrategyId(),e);
//            return Response.<RaffleStrategyResponseDTO>builder()
//                    .code(ResponseCode.UN_ERROR.getCode())
//                    .info(ResponseCode.UN_ERROR.getInfo())
//                    .build();
//        }
        return Response.<RaffleStrategyResponseDTO>builder()
                .code(ResponseCode.UN_ERROR.getCode())
                .info(ResponseCode.UN_ERROR.getInfo())
                .build();
    }

    @RequestMapping(value = "query_raffle_strategy_rule_weight", method = RequestMethod.POST)
    @Override
    public Response<List<RaffleStrategyRuleWeightResponseDTO>> queryRaffleStrategyRuleWeight(@RequestBody RaffleStrategyRuleWeightRequestDTO request) {
      try{ //传过来一个userId、activityId
        log.info("查询抽奖策略权重规则开始userId:{},activityId:{}", request.getUserId(), request.getActivityId());
        // 1、参数校验
        if(StringUtils.isBlank(request.getUserId())||null==request.getActivityId()){
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(),ResponseCode.ILLEGAL_PARAMETER.getInfo());
        }

        // 2、查询用户的抽奖次数
        Integer userPartakeCount = raffleActivityAccountQuotaService.queryUserPartakeCount(request.getUserId(), request.getActivityId());

        // 3、查询规则
        List<RuleWeightVO> ruleWeightVos = raffleRule.queryAwardRuleWeightByActivityId(request.getActivityId());
        List<RaffleStrategyRuleWeightResponseDTO> raffleStrategyRuleWeightList = new ArrayList<>();
        for(RuleWeightVO ruleWeightVO:ruleWeightVos){
            // 转换对象
            List<RaffleStrategyRuleWeightResponseDTO.StrategyAward> strategyAwards = new ArrayList<>();
            List<RuleWeightVO.Award> awardList = ruleWeightVO.getAwardList();
            for(RuleWeightVO.Award award:awardList){
                RaffleStrategyRuleWeightResponseDTO.StrategyAward strategyAward = new RaffleStrategyRuleWeightResponseDTO.StrategyAward();
                strategyAward.setAwardId(award.getAwardId());
                strategyAward.setAwardTitle(award.getAwardTitle());
                strategyAwards.add(strategyAward);
            }
            RaffleStrategyRuleWeightResponseDTO raffleStrategyRuleWeightResponseDTO = new RaffleStrategyRuleWeightResponseDTO();
            raffleStrategyRuleWeightResponseDTO.setUserActivityAccountTotalUseCount(userPartakeCount);
            raffleStrategyRuleWeightResponseDTO.setStrategyAwards(strategyAwards);
            raffleStrategyRuleWeightResponseDTO.setRuleWeightCount(ruleWeightVO.getWeight());
            raffleStrategyRuleWeightList.add(raffleStrategyRuleWeightResponseDTO);
        }
        Response<List<RaffleStrategyRuleWeightResponseDTO>> response = Response.<List<RaffleStrategyRuleWeightResponseDTO>>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .info(ResponseCode.SUCCESS.getInfo())
                .data(raffleStrategyRuleWeightList)
                .build();
        log.info("查询抽奖策略权重规则配置完成 userId:{} activityId：{} response: {}", request.getUserId(), request.getActivityId(), com.alibaba.fastjson.JSON.toJSONString(response));
        return response;
    } catch (Exception e) {
        log.error("查询抽奖策略权重规则配置失败 userId:{} activityId：{}", request.getUserId(), request.getActivityId(), e);
        return Response.<List<RaffleStrategyRuleWeightResponseDTO>>builder()
                .code(ResponseCode.UN_ERROR.getCode())
                .info(ResponseCode.UN_ERROR.getInfo())
                .build();
    }
    }
}
