package cn.bugstack.domain.award.model.entity;

import cn.bugstack.domain.award.model.valobj.AwardStateVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author fuzhouling
 * @date 2024/09/25
 * @program bigmarket-14610-gwh
 * @description 用户中奖记录实体表
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserAwardRecordEntity {
    //    用户ID
    private String userId;
    //    活动ID
    private Long activityId;
    //    抽奖策略ID
    private Long strategyId;
    //    抽奖订单ID【作为幂等使用】
    private String orderId;
    //    奖品ID
    private Integer awardId;
    //    奖品标题（名称）
    private String awardTitle;
    //    中奖时间
    private Date awardTime;
    //    中奖配置
    private String awardConfig;
    //    奖品状态；create-创建、completed-发奖完成
    private AwardStateVO awardState;
}
