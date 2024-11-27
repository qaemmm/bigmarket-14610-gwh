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
 * @description 抽奖活动账户表月表
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RaffleActivityAccountMonth {

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");

    //自增ID
    private Long id;
    //用户ID
    private String userId;
    //活动ID'
    private Long activityId;
    //月（yyyy-mm）
    private String month;
    //月次数
    private Integer monthCount;
    //月次数-剩余
    private Integer monthCountSurplus;
    //创建时间
    private Date createTime;
    //更新时间
    private Date updateTime;

    public String currentMonth() {
        return sdf.format(new Date());
    }
}
