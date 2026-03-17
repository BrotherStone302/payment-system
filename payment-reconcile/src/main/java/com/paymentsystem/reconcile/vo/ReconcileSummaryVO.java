package com.paymentsystem.reconcile.vo;

public class ReconcileSummaryVO {

    private Long total;
    private Long successCount;
    private Long pendingCount;
    private Long exceptionCount;

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(Long successCount) {
        this.successCount = successCount;
    }

    public Long getPendingCount() {
        return pendingCount;
    }

    public void setPendingCount(Long pendingCount) {
        this.pendingCount = pendingCount;
    }

    public Long getExceptionCount() {
        return exceptionCount;
    }

    public void setExceptionCount(Long exceptionCount) {
        this.exceptionCount = exceptionCount;
    }
}