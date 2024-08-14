package cn.bugstack.domain.strategy.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fuzhouling
 * @date 2024/08/14
 * @program bigmarket-14610-gwh
 * @description todo desc...
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AwardEntity {
    /**
     * 自增ID
     */
    private Long id;
    /**
     * 抽奖奖品ID - 内部流转使用
     */
    private Integer awardId;
}
