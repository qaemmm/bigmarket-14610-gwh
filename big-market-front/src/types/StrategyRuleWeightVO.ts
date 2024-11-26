export interface StrategyRuleWeightVO{
    ruleWeightCount:number;
    userActivityAccountTotalUseCount: number;
    strategyAwards:StrategyAward[];
}

export interface StrategyAward{
    awardId:number;
    awardTitle:string;
}
