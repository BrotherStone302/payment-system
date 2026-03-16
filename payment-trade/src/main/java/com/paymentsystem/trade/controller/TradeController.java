package com.paymentsystem.trade.controller;

import com.paymentsystem.common.result.Result;
import com.paymentsystem.trade.dto.TransferRequest;
import com.paymentsystem.trade.service.TradeService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import com.paymentsystem.trade.entity.TradeOrder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/trades")
public class TradeController {

    private final TradeService tradeService;

    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @PostMapping("/transfer")
    public Result<Void> transfer(@Valid @RequestBody TransferRequest request) {
        tradeService.transfer(request);
        return Result.success("转账成功", null);
    }

    @GetMapping("/{tradeNo}")
    public Result<TradeOrder> getByTradeNo(@PathVariable String tradeNo) {
        TradeOrder tradeOrder = tradeService.getByTradeNo(tradeNo);
        if (tradeOrder == null) {
            return Result.fail(404, "交易单不存在");
        }
        return Result.success(tradeOrder);
    }
}