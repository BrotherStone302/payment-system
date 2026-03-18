package com.paymentsystem.reconcile.service;

import com.paymentsystem.reconcile.entity.ReconcileRecord;
import com.paymentsystem.reconcile.vo.ReconcileSummaryVO;

import java.util.List;

public interface ReconcileService {

    List<ReconcileRecord> listAll();

    ReconcileRecord getByTradeNo(String tradeNo);

    List<ReconcileRecord> listByStatus(Integer status);

    ReconcileSummaryVO summary();

    String markException(String tradeNo);

    String recover(String tradeNo);

    String compensate();
}