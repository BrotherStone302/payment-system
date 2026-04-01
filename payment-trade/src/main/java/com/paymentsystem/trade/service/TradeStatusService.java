package com.paymentsystem.trade.service;

public interface TradeStatusService {
    void updateToSuccess(Long id);
    void updateToFail(Long id);
}