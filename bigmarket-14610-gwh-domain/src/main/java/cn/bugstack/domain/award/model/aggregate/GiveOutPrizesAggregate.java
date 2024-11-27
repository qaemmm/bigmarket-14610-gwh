package cn.bugstack.domain.award.model.aggregate;


import cn.bugstack.domain.award.model.entity.UserAwardRecordEntity;
import cn.bugstack.domain.award.model.entity.UserCreditAwardEntity;
import cn.bugstack.domain.award.model.valobj.AwardStateVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 用户中奖记录实体对象
 * @create 2024-04-06 09:06
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GiveOutPrizesAggregate {

    /** 用户ID */
    private String userId;
    /** 用户发奖记录 */
    private UserAwardRecordEntity userAwardRecordEntity;
    /** 用户积分奖品 */
    private UserCreditAwardEntity userCreditAwardEntity;

    public static UserAwardRecordEntity buildDistributeUserAwardRecordEntity(String userId, String orderId, Integer awardId, AwardStateVO awardState){
        return UserAwardRecordEntity.builder()
                .userId(userId)
                .orderId(orderId)
                .awardId(awardId)
                .awardState(awardState)
                .build();
    }

    public static UserCreditAwardEntity buildUserCreditAwardEntity(String userId, BigDecimal creditAmount){
        return UserCreditAwardEntity.builder()
                .userId(userId)
                .creditAmount(creditAmount)
                .build();
    }

}
