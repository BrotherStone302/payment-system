package com.paymentsystem.reconcile.vo;

import java.util.List;

public class ReconcilePageVO {

    private Long total;
    private Long pageNum;
    private Long pageSize;
    private List<ReconcileRecordVO> records;

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getPageNum() {
        return pageNum;
    }

    public void setPageNum(Long pageNum) {
        this.pageNum = pageNum;
    }

    public Long getPageSize() {
        return pageSize;
    }

    public void setPageSize(Long pageSize) {
        this.pageSize = pageSize;
    }

    public List<ReconcileRecordVO> getRecords() {
        return records;
    }

    public void setRecords(List<ReconcileRecordVO> records) {
        this.records = records;
    }
}