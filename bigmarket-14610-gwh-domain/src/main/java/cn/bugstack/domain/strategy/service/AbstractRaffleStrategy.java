package cn.bugstack.domain.strategy.service;

import cn.bugstack.domain.strategy.model.entity.RaffleAwardEntity;
import cn.bugstack.domain.strategy.model.entity.RaffleFactorEntity;
import cn.bugstack.domain.strategy.repository.IStrategyRepository;
import cn.bugstack.domain.strategy.service.armory.IStrategyDispatch;
import cn.bugstack.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import cn.bugstack.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import cn.bugstack.types.enums.ResponseCode;
import cn.bugstack.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author fuzhouling
 * @date 2024/08/16
 * @program bigmarket-14610-gwh
 * @description  抽象抽奖策略
 **/
@Slf4j
public abstract class AbstractRaffleStrategy implements IRaffleStrategy, IRaffleStock {
    //策略仓储服务 -> domain像一个大厨，仓储提供米面粮油
    protected IStrategyRepository repository;
    // 策略调度服务 -> 只负责抽奖处理，通过新增接口的方式，隔离职责，不需要使用方关心或者调用抽奖的初始化
    protected IStrategyDispatch strategyDispatch;
    // 抽奖的责任链 -> 从抽奖的规则中，解耦出前置规则为责任链处理
    protected DefaultChainFactory chainFactory;
    // 抽奖的决策树 -> 负责抽奖中到抽奖后的规则过滤，如抽奖到A奖品ID，之后要做次数的判断和库存的扣减等。
    protected DefaultTreeFactory treeFactory;

    public AbstractRaffleStrategy (IStrategyRepository repository,IStrategyDispatch strategyDispatch,DefaultChainFactory chainFactory,DefaultTreeFactory treeFactory) {
        this.repository = repository;
        this.strategyDispatch =strategyDispatch;
        this.chainFactory = chainFactory;
        this.treeFactory = treeFactory;
    }

    // TODO -- 抽象类里能这么写吗？？？？？
    // 抽象类重写抽奖接口，通过这样的方式来定义抽奖的标准流程。之后过滤抽奖规则，并根据结果处理奖品结果
    @Override
    public RaffleAwardEntity performRaffle(RaffleFactorEntity raffleFactorEntity) {

        // 1. 参数校验
        Long strategyId = raffleFactorEntity.getStrategyId();
        String userId = raffleFactorEntity.getUserId();
        if (null == strategyId || StringUtils.isBlank(userId)) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
        }


        // 2. 责任链抽奖计算【这步拿到的是初步的抽奖ID，之后需要根据ID处理抽奖】注意；黑名单、权重等非默认抽奖的直接返回抽奖结果
        DefaultChainFactory.StrategyAwardVO chainStrategyAwardVO = raffleLogicChain(userId,strategyId);
        log.info("责任链{} {} {} {}",strategyId,userId,chainStrategyAwardVO.getAwardId(),chainStrategyAwardVO.getAwardRuleValue());
        log.info("DefaultChainFactory.LogicModel.RULE_DEFAULT:{}", DefaultChainFactory.LogicModel.RULE_DEFAULT);
        if(!DefaultChainFactory.LogicModel.RULE_DEFAULT.getCode().equals(chainStrategyAwardVO.getAwardRuleValue())){
            return RaffleAwardEntity.builder()
                    .awardId(chainStrategyAwardVO.getAwardId())
                    .build();
        }

        DefaultTreeFactory.StrategyAwardVO treeStrategyAwardVO = raffleLogicTree(userId,strategyId,chainStrategyAwardVO.getAwardId());
//        log.info("决策树{} {} {}", strategyId, userId, treeStrategyAwardVO.getAwardRuleValue());


        return RaffleAwardEntity.builder()
                .awardId(treeStrategyAwardVO.getAwardId())
                .awardConfig(treeStrategyAwardVO.getAwardRuleValue())
                .build();
    }

    /**
     * 抽奖计算，责任链抽象方法
     *
     * @param userId     用户ID
     * @param strategyId 策略ID
     * @return 奖品ID
     */
    public abstract DefaultChainFactory.StrategyAwardVO raffleLogicChain(String userId, Long strategyId);

    /**
     * 抽奖结果过滤，决策树抽象方法
     *
     * @param userId     用户ID
     * @param strategyId 策略ID
     * @param awardId    奖品ID
     * @return 过滤结果【奖品ID，会根据抽奖次数判断、库存判断、兜底兜里返回最终的可获得奖品信息】
     */
    public abstract DefaultTreeFactory.StrategyAwardVO raffleLogicTree(String userId, Long strategyId, Integer awardId);

}
