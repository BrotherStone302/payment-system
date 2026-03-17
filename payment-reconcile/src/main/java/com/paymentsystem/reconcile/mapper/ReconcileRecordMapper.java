package com.paymentsystem.reconcile.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.paymentsystem.reconcile.entity.ReconcileRecord;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReconcileRecordMapper extends BaseMapper<ReconcileRecord> {
}
