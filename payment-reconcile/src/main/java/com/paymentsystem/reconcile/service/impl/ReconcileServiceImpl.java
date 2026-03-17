package com.paymentsystem.reconcile.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.paymentsystem.reconcile.entity.ReconcileRecord;
import com.paymentsystem.reconcile.mapper.ReconcileRecordMapper;
import com.paymentsystem.reconcile.service.ReconcileService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReconcileServiceImpl implements ReconcileService {

    private final ReconcileRecordMapper reconcileRecordMapper;

    public ReconcileServiceImpl(ReconcileRecordMapper reconcileRecordMapper) {
        this.reconcileRecordMapper = reconcileRecordMapper;
    }

    @Override
    public List<ReconcileRecord> listAll() {
        return reconcileRecordMapper.selectList(
                new LambdaQueryWrapper<ReconcileRecord>()
                        .orderByDesc(ReconcileRecord::getId)
        );
    }

    @Override
    public ReconcileRecord getByTradeNo(String tradeNo) {
        return reconcileRecordMapper.selectOne(
                new LambdaQueryWrapper<ReconcileRecord>()
                        .eq(ReconcileRecord::getTradeNo, tradeNo)
        );
    }

    @Override
    public List<ReconcileRecord> listByStatus(Integer status) {
        return reconcileRecordMapper.selectList(
                new LambdaQueryWrapper<ReconcileRecord>()
                        .eq(ReconcileRecord::getStatus, status)
                        .orderByDesc(ReconcileRecord::getId)
        );
    }
}
