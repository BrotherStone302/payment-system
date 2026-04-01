package com.paymentsystem.account.controller;

import com.paymentsystem.account.dto.AccountCreateRequest;
import com.paymentsystem.account.entity.Account;
import com.paymentsystem.account.service.AccountService;
import com.paymentsystem.account.vo.AccountFlowVO;
import com.paymentsystem.account.vo.AccountVO;
import com.paymentsystem.common.result.Result;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import com.paymentsystem.common.dto.AccountChangeRequest;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public Result<Void> createAccount(@Valid @RequestBody AccountCreateRequest request) {
        accountService.createAccount(request);
        return Result.success("开户成功", null);
    }

    @GetMapping("/user/{userId}")
    public Result<AccountVO> getByUserId(@PathVariable Long userId) {
        Account account = accountService.getByUserId(userId);

        AccountVO vo = new AccountVO();
        vo.setId(account.getId());
        vo.setUserId(account.getUserId());
        vo.setBalance(account.getBalance());
        vo.setFrozenAmount(account.getFrozenAmount());
        vo.setStatus(account.getStatus());
        vo.setCreateTime(account.getCreateTime());

        return Result.success(vo);
    }

    @GetMapping("/user/{userId}/flows")
    public Result<List<AccountFlowVO>> listFlows(@PathVariable Long userId) {
        List<com.paymentsystem.account.entity.AccountFlow> flows = accountService.listFlowsByUserId(userId);

        List<AccountFlowVO> result = new ArrayList<>();
        for (com.paymentsystem.account.entity.AccountFlow flow : flows) {
            AccountFlowVO vo = new AccountFlowVO();
            vo.setId(flow.getId());
            vo.setTradeNo(flow.getTradeNo());
            vo.setChangeType(flow.getChangeType());
            vo.setAmount(flow.getAmount());
            vo.setBalanceBefore(flow.getBalanceBefore());
            vo.setBalanceAfter(flow.getBalanceAfter());
            vo.setCreateTime(flow.getCreateTime());
            result.add(vo);
        }

        return Result.success(result);
    }

    @PostMapping("/recharge")
    public Result<Void> recharge(@Valid @RequestBody AccountChangeRequest request) {
        accountService.credit(request.getUserId(), request.getAmount(), request.getTradeNo());
        return Result.success("充值成功", null);
    }

    @PostMapping("/debit")
    public Result<Void> debit(@Valid @RequestBody AccountChangeRequest request) {
        accountService.debit(request.getUserId(), request.getAmount(), request.getTradeNo());
        return Result.success("扣款成功", null);
    }

    @PostMapping("/credit")
    public Result<Void> credit(@Valid @RequestBody AccountChangeRequest request) {
        accountService.credit(request.getUserId(), request.getAmount(), request.getTradeNo());
        return Result.success("入账成功", null);
    }
}