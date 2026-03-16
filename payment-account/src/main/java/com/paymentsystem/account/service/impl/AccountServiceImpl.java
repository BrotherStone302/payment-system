package com.paymentsystem.account.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.paymentsystem.account.dto.AccountCreateRequest;
import com.paymentsystem.account.entity.Account;
import com.paymentsystem.account.entity.AccountFlow;
import com.paymentsystem.account.mapper.AccountFlowMapper;
import com.paymentsystem.account.mapper.AccountMapper;
import com.paymentsystem.account.service.AccountService;
import com.paymentsystem.common.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountMapper accountMapper;
    private final AccountFlowMapper accountFlowMapper;

    public AccountServiceImpl(AccountMapper accountMapper, AccountFlowMapper accountFlowMapper) {
        this.accountMapper = accountMapper;
        this.accountFlowMapper = accountFlowMapper;
    }

    @Override
    public void createAccount(AccountCreateRequest request) {
        LambdaQueryWrapper<Account> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Account::getUserId, request.getUserId());

        Long count = accountMapper.selectCount(queryWrapper);
        if (count != null && count > 0) {
            throw new BusinessException("该用户已开户");
        }

        Account account = new Account();
        account.setUserId(request.getUserId());
        account.setBalance(BigDecimal.ZERO);
        account.setFrozenAmount(BigDecimal.ZERO);
        account.setStatus(1);

        accountMapper.insert(account);
    }

    @Override
    public Account getByUserId(Long userId) {
        LambdaQueryWrapper<Account> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Account::getUserId, userId);

        Account account = accountMapper.selectOne(queryWrapper);
        if (account == null) {
            throw new BusinessException("账户不存在");
        }
        return account;
    }

    @Override
    @Transactional
    public void debit(Long userId, BigDecimal amount, String tradeNo) {
        System.out.println("account debit xid = " + io.seata.core.context.RootContext.getXID());
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("扣款金额必须大于0");
        }

        Account beforeAccount = getByUserId(userId);
        BigDecimal balanceBefore = beforeAccount.getBalance();

        int rows = accountMapper.debitIfEnough(userId, amount);
        if (rows == 0) {
            throw new BusinessException("余额不足或扣款失败");
        }

        Account afterAccount = getByUserId(userId);
        BigDecimal balanceAfter = afterAccount.getBalance();

        AccountFlow flow = new AccountFlow();
        flow.setUserId(userId);
        flow.setTradeNo(tradeNo);
        flow.setChangeType(1);
        flow.setAmount(amount);
        flow.setBalanceBefore(balanceBefore);
        flow.setBalanceAfter(balanceAfter);
        flow.setAccountId(afterAccount.getId());
        accountFlowMapper.insert(flow);
    }

    @Override
    public void credit(Long userId, BigDecimal amount, String tradeNo) {
        System.out.println("account credit xid = " + io.seata.core.context.RootContext.getXID());
        Account account = getByUserId(userId);

        BigDecimal before = account.getBalance();
        BigDecimal after = before.add(amount);

        account.setBalance(after);
        accountMapper.updateById(account);

        AccountFlow flow = new AccountFlow();
        flow.setUserId(userId);
        flow.setAccountId(account.getId());
        flow.setTradeNo(tradeNo);
        flow.setChangeType(2);
        flow.setAmount(amount);
        flow.setBalanceBefore(before);
        flow.setBalanceAfter(after);
        flow.setAccountId(account.getId());
        accountFlowMapper.insert(flow);
    }

    @Override
    public List<AccountFlow> listFlowsByUserId(Long userId) {
        LambdaQueryWrapper<AccountFlow> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AccountFlow::getUserId, userId)
                .orderByDesc(AccountFlow::getId);
        return accountFlowMapper.selectList(queryWrapper);
    }
}
