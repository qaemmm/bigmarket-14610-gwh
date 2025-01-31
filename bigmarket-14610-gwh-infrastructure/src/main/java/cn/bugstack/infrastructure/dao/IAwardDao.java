package cn.bugstack.infrastructure.dao;

import cn.bugstack.infrastructure.dao.po.Award;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Fuzhengwei 
 * @description 奖品表DAO
 * @create 2023-12-16 13:23
 */
@Mapper
public interface IAwardDao {

    List<Award> queryAwardList();

    Award queryAwardById(@Param("awardId")Integer awardId);

    void deleteAwardById(@Param("award_id")Integer award_id);

    String queryAwardConfig(Integer awardId);

    String queryAwardKey(Integer awardId);
}
