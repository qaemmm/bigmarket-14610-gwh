package cn.bugstack.infrastructure.persistent.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author fuzhouling
 * @date 2024/09/12
 * @program bigmarket-14610-gwh
 * @description 抽奖活动账户表日表
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RaffleActivityAccountDay {

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    //自增ID
    private Long id;
    //用户ID
    private String userId;
    //活动ID
    private Long activityId;
    //日期（yyyy-mm-dd）
    private String day;
    //日次数
    private Integer dayCount;
    //日次数-剩余
    private Integer dayCountSurplus;
    //创建时间
    private Date createTime;
    //更新时间
    private Date updateTime;

    public String currentDay(){
        return sdf.format(new Date());
    }

}
