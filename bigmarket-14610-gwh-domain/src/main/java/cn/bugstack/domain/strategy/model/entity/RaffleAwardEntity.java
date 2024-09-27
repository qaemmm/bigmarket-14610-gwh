package cn.bugstack.domain.strategy.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fuzhouling
 * @date 2024/08/16
 * @program bigmarket-14610-gwh
 * @description todo desc...
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RaffleAwardEntity {
    /**
     * 奖品ID
     */
    private Integer awardId;
    /**
     * 奖品配置信息
     */
    private String awardConfig;
    /**
     * 奖品标题
     */
    private String awardTitle;

    /**
     * 奖品顺序号
     */
    private Integer sort;

}
