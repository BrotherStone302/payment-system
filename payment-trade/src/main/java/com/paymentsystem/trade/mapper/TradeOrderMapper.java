package com.paymentsystem.trade.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.paymentsystem.trade.entity.TradeOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface TradeOrderMapper extends BaseMapper<TradeOrder> {

    @Select("select * from trade_order where trade_no = #{tradeNo} limit 1")
    TradeOrder selectByTradeNo(@Param("tradeNo") String tradeNo);
}