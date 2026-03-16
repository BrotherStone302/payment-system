package com.paymentsystem.account.service;

import com.paymentsystem.account.dto.AccountCreateRequest;
import com.paymentsystem.account.entity.Account;
import com.paymentsystem.account.entity.AccountFlow;

import java.math.BigDecimal;
import java.util.List;

public interface AccountService {

    void createAccount(AccountCreateRequest request);

    Account getByUserId(Long userId);

    void debit(Long userId, BigDecimal amount, String tradeNo);

    void credit(Long userId, BigDecimal amount, String tradeNo);

    List<AccountFlow> listFlowsByUserId(Long userId);
}