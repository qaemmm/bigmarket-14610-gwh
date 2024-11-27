package cn.bugstack.domain.award.service.distribute.impl;

import cn.bugstack.domain.award.model.aggregate.GiveOutPrizesAggregate;
import cn.bugstack.domain.award.model.entity.DistributeAwardEntity;
import cn.bugstack.domain.award.model.entity.UserAwardRecordEntity;
import cn.bugstack.domain.award.model.entity.UserCreditAwardEntity;
import cn.bugstack.domain.award.model.valobj.AwardStateVO;
import cn.bugstack.domain.award.repository.IAwardRepository;
import cn.bugstack.domain.award.service.distribute.IDistributeAward;
import cn.bugstack.types.common.Constants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.MathContext;

@Component("user_credit_random")
public class UserCreditRandomAward implements IDistributeAward {

    @Resource
    private IAwardRepository repository;

    @Override
    public void giveOutPrizes(DistributeAwardEntity distributeAwardEntity) {

        Integer awardId = distributeAwardEntity.getAwardId();

        String awardConfig = distributeAwardEntity.getAwardConfig();

        if(StringUtils.isBlank(awardConfig)){
            awardConfig = repository.queryAwardConfig(awardId);
        }

        String[] creditRange = awardConfig.split(Constants.SPLIT);
        if (creditRange.length!=2){
            throw new RuntimeException("award_config 「" + awardConfig + "」配置不是一个范围值，如 1,100");
        }
        // 构建聚合对象--这一块之所以是complete，我们认为订单完成之后才能够进行奖品发放
        UserAwardRecordEntity userAwardRecordEntity = GiveOutPrizesAggregate.buildDistributeUserAwardRecordEntity(
                distributeAwardEntity.getUserId(),
                distributeAwardEntity.getOrderId(),
                distributeAwardEntity.getAwardId(),
                AwardStateVO.complete
        );


        BigDecimal creditAmount = generateRandom(new BigDecimal(creditRange[0]), new BigDecimal(creditRange[1]));

        UserCreditAwardEntity userCreditAwardEntity = GiveOutPrizesAggregate.buildUserCreditAwardEntity(distributeAwardEntity.getUserId(), creditAmount);

        GiveOutPrizesAggregate giveOutPrizesAggregate = new GiveOutPrizesAggregate();
        giveOutPrizesAggregate.setUserId(distributeAwardEntity.getUserId());
        giveOutPrizesAggregate.setUserAwardRecordEntity(userAwardRecordEntity);
        giveOutPrizesAggregate.setUserCreditAwardEntity(userCreditAwardEntity);


        repository.saveGiveOutPrizesAggregate(giveOutPrizesAggregate);

    }

    private BigDecimal generateRandom(BigDecimal min,BigDecimal max){
        if(min.equals(max))return min;
        BigDecimal multiply = BigDecimal.valueOf(Math.random()).multiply(max.subtract(min));
        return multiply.round(new MathContext(3));
    }
}
