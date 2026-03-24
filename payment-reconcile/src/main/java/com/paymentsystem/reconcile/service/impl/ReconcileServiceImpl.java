package com.paymentsystem.reconcile.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.paymentsystem.reconcile.entity.ReconcileRecord;
import com.paymentsystem.reconcile.mapper.ReconcileRecordMapper;
import com.paymentsystem.reconcile.service.ReconcileService;
import org.springframework.stereotype.Service;
import com.paymentsystem.reconcile.vo.ReconcileSummaryVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.paymentsystem.reconcile.vo.ReconcilePageVO;
import com.paymentsystem.reconcile.dto.ReconcilePageQuery;
import com.paymentsystem.reconcile.vo.ReconcileRecordVO;
import java.util.List;

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
    public String markException(String tradeNo) {
        ReconcileRecord record = reconcileRecordMapper.selectOne(
                new LambdaQueryWrapper<ReconcileRecord>()
                        .eq(ReconcileRecord::getTradeNo, tradeNo)
        );

        if (record == null) {
            return "对账记录不存在";
        }

        if (record.getStatus() != null && record.getStatus() == 2) {
            return "当前记录已是异常状态";
        }

        ReconcileRecord updateRecord = new ReconcileRecord();
        updateRecord.setId(record.getId());
        updateRecord.setStatus(2);

        boolean success = reconcileRecordMapper.updateById(updateRecord) > 0;
        return success ? "标记异常成功" : "标记异常失败";
    }

    @Override
    public String recover(String tradeNo) {
        ReconcileRecord record = reconcileRecordMapper.selectOne(
                new LambdaQueryWrapper<ReconcileRecord>()
                        .eq(ReconcileRecord::getTradeNo, tradeNo)
        );

        if (record == null) {
            return "对账记录不存在";
        }

        if (record.getStatus() == null || record.getStatus() != 2) {
            return "当前记录不是异常状态，不能恢复";
        }

        ReconcileRecord updateRecord = new ReconcileRecord();
        updateRecord.setId(record.getId());
        updateRecord.setStatus(1);

        boolean success = reconcileRecordMapper.updateById(updateRecord) > 0;
        return success ? "恢复成功" : "恢复失败";
    }

    @Override
    public String compensate() {
        List<ReconcileRecord> exceptionRecords = reconcileRecordMapper.selectList(
                new LambdaQueryWrapper<ReconcileRecord>()
                        .eq(ReconcileRecord::getStatus, 2)
        );

        if (exceptionRecords == null || exceptionRecords.isEmpty()) {
            return "当前没有异常记录需要补偿";
        }

        int successCount = 0;

        for (ReconcileRecord record : exceptionRecords) {
            ReconcileRecord updateRecord = new ReconcileRecord();
            updateRecord.setId(record.getId());
            updateRecord.setStatus(1);

            boolean success = reconcileRecordMapper.updateById(updateRecord) > 0;
            if (success) {
                successCount++;
            }
        }

        return "补偿完成，成功处理 " + successCount + " 条异常记录";
    }

    @Override
    public ReconcilePageVO pageList(ReconcilePageQuery query) {
        Page<ReconcileRecord> page = new Page<>(query.getPageNum(), query.getPageSize());

        LambdaQueryWrapper<ReconcileRecord> wrapper = new LambdaQueryWrapper<ReconcileRecord>()
                .like(query.getTradeNo() != null && !query.getTradeNo().trim().isEmpty(),
                        ReconcileRecord::getTradeNo, query.getTradeNo())
                .eq(query.getStatus() != null,
                        ReconcileRecord::getStatus, query.getStatus())
                .eq(query.getFromUserId() != null,
                        ReconcileRecord::getFromUserId, query.getFromUserId())
                .eq(query.getToUserId() != null,
                        ReconcileRecord::getToUserId, query.getToUserId())
                .orderByDesc(ReconcileRecord::getId);

        Page<ReconcileRecord> resultPage = reconcileRecordMapper.selectPage(page, wrapper);

        ReconcilePageVO vo = new ReconcilePageVO();
        vo.setTotal(resultPage.getTotal());
        vo.setPageNum(resultPage.getCurrent());
        vo.setPageSize(resultPage.getSize());

        List<ReconcileRecordVO> recordVOList = resultPage.getRecords().stream()
                .map(this::convertToRecordVO)
                .toList();

        vo.setRecords(recordVOList);
        return vo;
    }

    private ReconcileRecordVO convertToRecordVO(ReconcileRecord record) {
        ReconcileRecordVO vo = new ReconcileRecordVO();
        vo.setId(record.getId());
        vo.setTradeNo(record.getTradeNo());
        vo.setFromUserId(record.getFromUserId());
        vo.setToUserId(record.getToUserId());
        vo.setAmount(record.getAmount());
        vo.setStatus(record.getStatus());
        vo.setStatusDesc(getStatusDesc(record.getStatus()));
        vo.setCreateTime(record.getCreateTime());
        vo.setUpdateTime(record.getUpdateTime());
        return vo;
    }

    private String getStatusDesc(Integer status) {
        if (status == null) {
            return "未知";
        }
        return switch (status) {
            case 0 -> "待处理";
            case 1 -> "对账成功";
            case 2 -> "对账异常";
            default -> "未知";
        };
    }
}
