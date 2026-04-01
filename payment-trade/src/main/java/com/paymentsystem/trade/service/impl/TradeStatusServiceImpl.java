package com.paymentsystem.trade.service.impl;

import com.paymentsystem.trade.entity.TradeOrder;
import com.paymentsystem.trade.mapper.TradeOrderMapper;
import com.paymentsystem.trade.service.TradeStatusService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TradeStatusServiceImpl implements TradeStatusService {

    private final TradeOrderMapper tradeOrderMapper;

    public TradeStatusServiceImpl(TradeOrderMapper tradeOrderMapper) {
        this.tradeOrderMapper = tradeOrderMapper;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateToSuccess(Long id) {
        TradeOrder order = new TradeOrder();
        order.setId(id);
        order.setStatus(TradeOrder.STATUS_SUCCESS);
        tradeOrderMapper.updateById(order);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateToFail(Long id) {
        TradeOrder order = new TradeOrder();
        order.setId(id);
        order.setStatus(TradeOrder.STATUS_FAIL);
        tradeOrderMapper.updateById(order);
    }
}