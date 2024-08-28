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

}
