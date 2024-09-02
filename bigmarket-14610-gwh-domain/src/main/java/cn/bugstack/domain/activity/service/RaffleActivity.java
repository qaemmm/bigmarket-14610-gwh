package cn.bugstack.domain.activity.service;

import cn.bugstack.domain.activity.repository.IActivityRepository;
import org.springframework.stereotype.Service;

/**
 * @author fuzhouling
 * @date 2024/09/02
 * @program bigmarket-14610-gwh
 * @description todo desc...
 **/
@Service
public class RaffleActivity extends AbstractRaffleActivity{
    public RaffleActivity(IActivityRepository activityRepository) {
        super(activityRepository);
    }

}
