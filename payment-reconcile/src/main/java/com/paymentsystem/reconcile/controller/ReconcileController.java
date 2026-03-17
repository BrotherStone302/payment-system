package com.paymentsystem.reconcile.controller;

import com.paymentsystem.common.result.Result;
import com.paymentsystem.reconcile.entity.ReconcileRecord;
import com.paymentsystem.reconcile.service.ReconcileService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/reconcile")
public class ReconcileController {

    private final ReconcileService reconcileService;

    public ReconcileController(ReconcileService reconcileService) {
        this.reconcileService = reconcileService;
    }

    @GetMapping("/list")
    public Result<List<ReconcileRecord>> list() {
        return Result.success(reconcileService.listAll());
    }

    @GetMapping("/{tradeNo}")
    public Result<ReconcileRecord> getByTradeNo(@PathVariable String tradeNo) {
        return Result.success(reconcileService.getByTradeNo(tradeNo));
    }

    @GetMapping("/status")
    public Result<List<ReconcileRecord>> listByStatus(@RequestParam Integer status) {
        return Result.success(reconcileService.listByStatus(status));
    }
}
