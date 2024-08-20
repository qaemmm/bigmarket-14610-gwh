package cn.bugstack.domain.strategy.service.raffle;

import cn.bugstack.domain.strategy.model.entity.RaffleAwardEntity;
import cn.bugstack.domain.strategy.model.entity.RaffleFactorEntity;
import cn.bugstack.domain.strategy.model.entity.RuleActionEntity;
import cn.bugstack.domain.strategy.model.entity.StrategyEntity;
import cn.bugstack.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import cn.bugstack.domain.strategy.model.valobj.StrategyAwardRuleModelVO;
import cn.bugstack.domain.strategy.repository.IStrategyRepository;
import cn.bugstack.domain.strategy.service.IRaffleStrategy;
import cn.bugstack.domain.strategy.service.armory.IStrategyDispatch;
import cn.bugstack.domain.strategy.service.rule.factory.DefaultLogicFactory;
import cn.bugstack.types.enums.ResponseCode;
import cn.bugstack.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author fuzhouling
 * @date 2024/08/16
 * @program bigmarket-14610-gwh
 * @description todo desc...
 **/
@Slf4j
public abstract class AbstractRaffleStrategy implements IRaffleStrategy {
    //策略仓储服务 -> domain像一个大厨，仓储提供米面粮油
    protected IStrategyRepository repository;
    // 策略调度服务 -> 只负责抽奖处理，通过新增接口的方式，隔离职责，不需要使用方关心或者调用抽奖的初始化
    protected IStrategyDispatch strategyDispatch ;

    public AbstractRaffleStrategy(IStrategyRepository repository,IStrategyDispatch strategyDispatch){
        this.repository = repository;
        this.strategyDispatch =strategyDispatch;
    }

    // TODO -- 抽象类里能这么写吗？？？？？
    // 抽象类重写抽奖接口，通过这样的方式来定义抽奖的标准流程。之后过滤抽奖规则，并根据结果处理奖品结果
    @Override
    public RaffleAwardEntity performRaffle(RaffleFactorEntity raffleFactorEntity){

        // 1. 参数校验
        Long strategyId = raffleFactorEntity.getStrategyId();
        String userId = raffleFactorEntity.getUserId();
        if(null == strategyId || StringUtils.isBlank(userId)){
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
        }
        // 2. 策略查询
        StrategyEntity strategy = repository.queryStrategyEntityByStrategyId(strategyId);

        //TODO 太难看懂了 3. 抽奖前 - 规则过滤
        RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> ruleActionEntity =
                this.doCheckRaffleBeforeLogic(RaffleFactorEntity
                        .builder()
                        .userId(userId)
                        .strategyId(strategyId)
                        .build()
                        , strategy.ruleModels());

        if(RuleLogicCheckTypeVO.TAKE_OVER.getCode().equals(ruleActionEntity.getCode())){
            if(DefaultLogicFactory.LogicModel.RULE_BLACKLIST.getCode().equals(ruleActionEntity.getRuleModel())){
                // 如果是黑名单返回固定的奖品ID
                return RaffleAwardEntity.builder()
                        .awardId(ruleActionEntity.getData().getAwardId())
                        .build();
                // 如果是权重过滤则返回对应
            }else if(DefaultLogicFactory.LogicModel.RULE_WIGHT.getCode().equals(ruleActionEntity.getRuleModel())){
                RuleActionEntity.RaffleBeforeEntity raffleBeforeEntity = ruleActionEntity.getData();
                Integer awardId = strategyDispatch.getRandomAwardId(raffleBeforeEntity.getStrategyId()
                        //
                        ,raffleBeforeEntity.getRuleWeightValueKey());
                return RaffleAwardEntity.builder()
                        .awardId(awardId)
                        .build();
            }
        }
        // 4. 默认抽奖流程
        Integer awardId = strategyDispatch.getRandomAwardId(strategyId);

        // 5. 查询奖品规则「抽奖中（拿到奖品ID时，过滤规则）、抽奖后（扣减完奖品库存后过滤，抽奖中拦截和无库存则走兜底）」
        StrategyAwardRuleModelVO strategyAwardRuleModelVO = repository.queryStrategyAwardRuleModel(strategyId,awardId);
        // 6. 抽奖中 - 规则过滤
        RuleActionEntity<RuleActionEntity.RaffleCenterEntity> ruleActionEntityCenter =
                this.doCheckRaffleCenterLogic(RaffleFactorEntity
                        .builder()
                        .userId(userId)
                        .awardId(awardId)
                        .strategyId(strategyId)
                        .build()
                        , strategyAwardRuleModelVO.raffleCenterRuleModelList());
        if(RuleLogicCheckTypeVO.TAKE_OVER.getCode().equals(ruleActionEntityCenter.getCode())){
            log.info("[临时日志]中奖中规则拦截，通过抽奖后规则rule_luck_award 走兜底奖励");
            return RaffleAwardEntity
                    .builder()
                    .awardDesc("中奖中规则拦截，通过抽奖后规则 rule_luck_award 走兜底奖励。")
                    .build();
        }

        return RaffleAwardEntity.builder()
                .awardId(awardId)
                .build();
    }

    protected abstract RuleActionEntity<RuleActionEntity.RaffleBeforeEntity>
    doCheckRaffleBeforeLogic(RaffleFactorEntity raffleFactorEntity,String ...logics);

    protected abstract RuleActionEntity<RuleActionEntity.RaffleCenterEntity>
    doCheckRaffleCenterLogic(RaffleFactorEntity raffleFactorEntity, String... logics);
}
