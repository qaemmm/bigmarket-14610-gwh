package cn.bugstack.trigger.http;

import cn.bugstack.domain.strategy.model.entity.RaffleAwardEntity;
import cn.bugstack.domain.strategy.model.entity.RaffleFactorEntity;
import cn.bugstack.domain.strategy.model.entity.StrategyAwardEntity;
import cn.bugstack.domain.strategy.service.IRaffleAward;
import cn.bugstack.domain.strategy.service.IRaffleStrategy;
import cn.bugstack.domain.strategy.service.armory.IStrategyArmory;
import cn.bugstack.trigger.api.IRaffleStrategyService;
import cn.bugstack.trigger.api.dto.RaffleAwardListRequestDTO;
import cn.bugstack.trigger.api.dto.RaffleAwardListResponseDTO;
import cn.bugstack.trigger.api.dto.RaffleStrategyRequestDTO;
import cn.bugstack.trigger.api.dto.RaffleStrategyResponseDTO;
import cn.bugstack.types.enums.ResponseCode;
import cn.bugstack.types.exception.AppException;
import cn.bugstack.types.model.Response;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author fuzhouling
 * @date 2024/08/28
 * @program bigmarket-14610-gwh
 * @description 抽奖服务
 **/
@RestController
@Slf4j
@CrossOrigin("${app.config.cross-origin}")
@RequestMapping("/api/${app.config.api-version}/raffle/Strategy/")
public class IRaffleStrategyController implements IRaffleStrategyService {

    @Resource
    private IStrategyArmory strategyArmory;

    @Resource
    private IRaffleAward raffleAward;

    @Resource
    private IRaffleStrategy raffleStrategy;

    /**
     * 策略装配，将策略信息装配到缓存中
     * <a href="http://localhost:8091/api/v1/raffle/strategy_armory">/api/v1/raffle/strategy_armory</a>
     *
     * @param strategyId 策略ID
     * @return 装配结果
     */
    @RequestMapping(value = "strategy_armory",method = RequestMethod.GET)
    @Override
    public Response<Boolean> strategyArmory(@RequestParam Long strategyId) {
        try{
            log.info("抽奖策略装配开始:{}",strategyId);
            boolean armoryStatus = strategyArmory.assembleLotteryStrategy(strategyId);
            Response response = Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(armoryStatus)
                    .build();
            log.info("抽奖策略装配完成:{}", response);
            return response;
        }catch(Exception e){
            log.error("抽奖策略装配异常 strategyId{}",strategyId);
            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }

    /**
     * 查询奖品列表
     * <a href="http://localhost:8091/api/v1/raffle/query_raffle_award_list">/api/v1/raffle/query_raffle_award_list</a>
     * 请求参数 raw json
     *
     * @param raffleAwardListRequestDTO {"strategyId":1000001}
     * @return 奖品列表
     */
    @RequestMapping(value = "query_raffle_award_list", method = RequestMethod.POST)
    @Override
    public Response<List<RaffleAwardListResponseDTO>> queryRaffleAwardList(@RequestBody RaffleAwardListRequestDTO raffleAwardListRequestDTO) {

        try{
            log.info("查询抽奖奖品列表配置开始:{}", raffleAwardListRequestDTO.getStrategyId());
            List<StrategyAwardEntity> strategyAwardEntities = raffleAward.queryRaffleAwardList(raffleAwardListRequestDTO.getStrategyId());
            List<RaffleAwardListResponseDTO> raffleAwardListResponseDTOList = new ArrayList<>(strategyAwardEntities.size());
            for (StrategyAwardEntity strategyAwardEntity : strategyAwardEntities) {
                RaffleAwardListResponseDTO raffleAwardListResponseDTO = RaffleAwardListResponseDTO.builder()
                        .awardId(strategyAwardEntity.getAwardId())
                        .awardTitle(strategyAwardEntity.getAwardTitle())
                        .awardSubtitle(strategyAwardEntity.getAwardSubtitle())
                        .sort(strategyAwardEntity.getSort())
                        .build();
                raffleAwardListResponseDTOList.add(raffleAwardListResponseDTO);
            }
            Response<List<RaffleAwardListResponseDTO>> response = Response.<List<RaffleAwardListResponseDTO>>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(raffleAwardListResponseDTOList)
                    .build();
            log.info("查询抽奖奖品列表配置完成response:{}", JSON.toJSONString(response));
            return response;
        }catch (Exception e){
            log.error("查询抽奖奖品列表配置异常 strategyId: {} ", raffleAwardListRequestDTO.getStrategyId(),e);
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
        try{
            log.info("随机抽奖开始 strategyId: {}", requestDTO.getStrategyId());
            RaffleFactorEntity rateFactorEntity = RaffleFactorEntity.builder()
                    .userId("system")
                    .strategyId(requestDTO.getStrategyId())
                    .build();
            RaffleAwardEntity raffleAwardEntity = raffleStrategy.performRaffle(rateFactorEntity);
            Response<RaffleStrategyResponseDTO> response = Response.<RaffleStrategyResponseDTO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(RaffleStrategyResponseDTO.builder()
                            .awardId(raffleAwardEntity.getAwardId())
                            .awardIndex(raffleAwardEntity.getSort())
                            .build())
                    .build();
            log.info("随机抽奖完成 response:{}", JSON.toJSONString(response));
            return response;
        }catch (AppException e){
         log.error("随机抽奖异常 strategyId: {} 异常：{}", requestDTO.getStrategyId(),e.getInfo());
            return Response.<RaffleStrategyResponseDTO>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        }catch (Exception e){
            log.error("随机抽奖异常 strategyId: {} 异常：{} ", requestDTO.getStrategyId(),e);
            return Response.<RaffleStrategyResponseDTO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }
}
