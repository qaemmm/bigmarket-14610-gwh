package cn.bugstack.trigger.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

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
public class ActivityDrawResponseDTO implements Serializable {
    private Integer awardId;
    private Integer awardIndex;
    private String awardTitle;

}
