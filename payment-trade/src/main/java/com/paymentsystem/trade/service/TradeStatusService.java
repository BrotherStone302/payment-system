/*package com.paymentsystem.trade.service;

public interface TradeStatusService {

    void updateToSuccess(Long tradeId);

    void updateToFail(Long tradeId);

    void updateStatus(Long tradeId, Integer status);
}*/
package com.paymentsystem.trade.service;

public interface TradeStatusService {
    void updateToSuccess(Long id);
    void updateToFail(Long id);
}