package cn.bugstack.infrastructure.persistent.dao;

import cn.bugstack.infrastructure.persistent.po.Award;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 奖品表DAO
 * @create 2023-12-16 13:23
 */
@Mapper
public interface IAwardDao {

    List<Award> queryAwardList();

    Award queryAwardById(@Param("awardId")Integer awardId);

    void deleteAwardById(@Param("award_id")Integer award_id);
}
