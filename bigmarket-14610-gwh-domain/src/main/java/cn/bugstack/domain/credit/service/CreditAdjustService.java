package cn.bugstack.domain.credit.service;

import cn.bugstack.domain.credit.model.aggregate.TradeAggregate;
import cn.bugstack.domain.credit.model.entity.CreditAccountEntity;
import cn.bugstack.domain.credit.model.entity.CreditOrderEntity;
import cn.bugstack.domain.credit.model.entity.TradeEntity;
import cn.bugstack.domain.credit.repository.ICreditRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service
@Slf4j
public class CreditAdjustService implements ICreditAdjustService{

    @Resource
    private ICreditRepository creditRepository;


    @Override
    public String createOrder(TradeEntity tradeEntity) {
        log.info("增加账户积分额度开始 userId:{} tradeName:{} amount:{}", tradeEntity.getUserId(), tradeEntity.getTradeName(), tradeEntity.getAmount());
        TradeAggregate tradeAggregate = new TradeAggregate();
        tradeAggregate.setUserId(tradeEntity.getUserId());

        CreditAccountEntity creditAccountEntity =
                TradeAggregate.createCreditAccountEntity(tradeEntity.getUserId(), tradeEntity.getAmount());

        CreditOrderEntity creditOrderEntity =
                TradeAggregate.createCreditOrderEntity(tradeEntity.getUserId(), tradeEntity.getTradeName(), tradeEntity.getTradeType(), tradeEntity.getAmount(), tradeEntity.getOutBusinessNo());

        tradeAggregate.setCreditAccountEntity(creditAccountEntity);
        tradeAggregate.setCreditOrderEntity(creditOrderEntity);

        // 4. 保存积分交易订单
        creditRepository.saveUserCreditTradeOrder(tradeAggregate);
        log.info("增加账户积分额度完成 userId:{} orderId:{}", tradeEntity.getUserId(), creditOrderEntity.getOrderId());

        return creditOrderEntity.getOrderId();
    }
}
