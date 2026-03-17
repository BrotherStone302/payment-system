package com.paymentsystem.reconcile.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.paymentsystem.reconcile.entity.ReconcileRecord;
import com.paymentsystem.reconcile.mapper.ReconcileRecordMapper;
import com.paymentsystem.reconcile.service.ReconcileService;
import org.springframework.stereotype.Service;
import com.paymentsystem.reconcile.vo.ReconcileSummaryVO;

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

    @Override
    public ReconcileSummaryVO summary() {
        Long total = reconcileRecordMapper.selectCount(new LambdaQueryWrapper<>());

        Long successCount = reconcileRecordMapper.selectCount(
                new LambdaQueryWrapper<ReconcileRecord>()
                        .eq(ReconcileRecord::getStatus, 1)
        );

        Long pendingCount = reconcileRecordMapper.selectCount(
                new LambdaQueryWrapper<ReconcileRecord>()
                        .eq(ReconcileRecord::getStatus, 0)
        );

        Long exceptionCount = reconcileRecordMapper.selectCount(
                new LambdaQueryWrapper<ReconcileRecord>()
                        .eq(ReconcileRecord::getStatus, 2)
        );

        ReconcileSummaryVO vo = new ReconcileSummaryVO();
        vo.setTotal(total);
        vo.setSuccessCount(successCount);
        vo.setPendingCount(pendingCount);
        vo.setExceptionCount(exceptionCount);
        return vo;
    }

    @Override
    public boolean markException(String tradeNo) {
        ReconcileRecord record = reconcileRecordMapper.selectOne(
                new LambdaQueryWrapper<ReconcileRecord>()
                        .eq(ReconcileRecord::getTradeNo, tradeNo)
        );

        if (record == null) {
            return false;
        }

        ReconcileRecord updateRecord = new ReconcileRecord();
        updateRecord.setId(record.getId());
        updateRecord.setStatus(2);

        return reconcileRecordMapper.updateById(updateRecord) > 0;
    }
}
