package cn.bugstack.infrastructure.dao;

import cn.bugstack.infrastructure.dao.po.RuleTree;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IRuleTreeDao {
    RuleTree queryRuleTreeByTreeId(String treeId);
}
