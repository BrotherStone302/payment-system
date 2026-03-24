package com.paymentsystem.reconcile.dto;

public class ReconcilePageQuery {

    private Long pageNum = 1L;
    private Long pageSize = 10L;
    private String tradeNo;
    private Integer status;
    private Long fromUserId;
    private Long toUserId;

    public Long getPageNum() {
        return pageNum;
    }

    public void setPageNum(Long pageNum) {
        if (pageNum == null || pageNum < 1) {
            this.pageNum = 1L;
            return;
        }
        this.pageNum = pageNum;
    }

    public Long getPageSize() {
        return pageSize;
    }

    public void setPageSize(Long pageSize) {
        if (pageSize == null || pageSize < 1) {
            this.pageSize = 10L;
            return;
        }
        if (pageSize > 100) {
            this.pageSize = 100L;
            return;
        }
        this.pageSize = pageSize;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(Long fromUserId) {
        this.fromUserId = fromUserId;
    }

    public Long getToUserId() {
        return toUserId;
    }

    public void setToUserId(Long toUserId) {
        this.toUserId = toUserId;
    }
}