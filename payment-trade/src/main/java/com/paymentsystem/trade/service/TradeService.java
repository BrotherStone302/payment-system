package com.paymentsystem.trade.service;

import com.paymentsystem.trade.dto.TransferRequest;
import com.paymentsystem.trade.entity.TradeOrder;

public interface TradeService {

    void transfer(TransferRequest request);

    TradeOrder getByTradeNo(String tradeNo);
}
