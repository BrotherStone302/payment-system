package com.paymentsystem.reconcile.service;

import com.paymentsystem.reconcile.entity.ReconcileRecord;

import java.util.List;

public interface ReconcileService {

    List<ReconcileRecord> listAll();

    ReconcileRecord getByTradeNo(String tradeNo);

    List<ReconcileRecord> listByStatus(Integer status);
}
