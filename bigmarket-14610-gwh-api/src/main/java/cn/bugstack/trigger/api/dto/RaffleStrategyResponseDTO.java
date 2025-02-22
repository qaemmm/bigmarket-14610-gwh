package cn.bugstack.trigger.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author fuzhouling
 * @date 2024/08/28
 * @program bigmarket-14610-gwh
 * @description 抽奖应答结果
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RaffleStrategyResponseDTO implements Serializable {
    //奖品id
    private Integer awardId;
    //排序编号【策略奖品配置的奖品顺序编号】
    private Integer awardIndex;
}
