package cn.bugstack.trigger.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fuzhouling
 * @date 2024/08/28
 * @program bigmarket-14610-gwh
 * @description 抽奖奖品列表，
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RaffleAwardListResponseDTO {
    //奖品id
    private Integer awardId;
    //奖品名称
    private String awardTitle;
    //奖品副标题
    private String awardSubtitle;
    //奖品排序
    private Integer sort;
    //需要在原本的基础上封装 奖品规则次数-用户抽奖n次-规则树-根据strategy_award表中的rule_models
    private Integer awardRuleLockCount;
    //奖品是否解锁，true-已解锁；false-未解锁 有的话前端展示就小锁头
    private boolean isAwardUnlock;
    //等待解锁次数、规则抽奖n次解锁-用户已经抽奖的次数
    private Integer waitUnlockCount;
}
