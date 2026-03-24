package com.paymentsystem.reconcile.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class ReconcilePageQuery {

    @Min(value = 1, message = "pageNum必须大于等于1")
    private Long pageNum = 1L;

    @Min(value = 1, message = "pageSize必须大于等于1")
    @Max(value = 100, message = "pageSize不能大于100")
    private Long pageSize = 10L;

    private String tradeNo;
    private Integer status;
    private Long fromUserId;
    private Long toUserId;

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