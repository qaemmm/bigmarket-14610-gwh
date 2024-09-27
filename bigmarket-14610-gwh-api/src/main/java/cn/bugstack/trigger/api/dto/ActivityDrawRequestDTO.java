package cn.bugstack.trigger.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fuzhouling
 * @date 2024/09/26
 * @program bigmarket-14610-gwh
 * @description todo desc...
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActivityDrawRequestDTO {
    private String userId;
    private Long activityId;

}
