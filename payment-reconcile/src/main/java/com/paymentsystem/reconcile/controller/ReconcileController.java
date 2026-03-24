package com.paymentsystem.reconcile.controller;

import com.paymentsystem.common.result.Result;
import com.paymentsystem.reconcile.entity.ReconcileRecord;
import com.paymentsystem.reconcile.service.ReconcileService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.paymentsystem.reconcile.vo.ReconcileSummaryVO;
import org.springframework.web.bind.annotation.PostMapping;
import com.paymentsystem.reconcile.vo.ReconcilePageVO;
import com.paymentsystem.reconcile.dto.ReconcilePageQuery;
import jakarta.validation.Valid;

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

    @GetMapping("/summary")
    public Result<ReconcileSummaryVO> summary() {
        return Result.success(reconcileService.summary());
    }

    @PostMapping("/{tradeNo}/exception")
    public Result<String> markException(@PathVariable String tradeNo) {
        String result = reconcileService.markException(tradeNo);
        if ("标记异常成功".equals(result)) {
            return Result.success(result, result);
        }
        return Result.fail(400, result);
    }

    @PostMapping("/{tradeNo}/recover")
    public Result<String> recover(@PathVariable String tradeNo) {
        String result = reconcileService.recover(tradeNo);
        if ("恢复成功".equals(result)) {
            return Result.success(result, result);
        }
        return Result.fail(400, result);
    }

    @PostMapping("/compensate")
    public Result<String> compensate() {
        String result = reconcileService.compensate();
        if ("当前没有异常记录需要补偿".equals(result)) {
            return Result.fail(400, result);
        }
        return Result.success(result, result);
    }

    @GetMapping("/page")
    public Result<ReconcilePageVO> pageList(@Valid ReconcilePageQuery query) {
        return Result.success(reconcileService.pageList(query));
    }
}
