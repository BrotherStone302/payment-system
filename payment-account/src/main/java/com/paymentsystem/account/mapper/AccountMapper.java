package com.paymentsystem.account.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.paymentsystem.account.entity.Account;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;

@Mapper
public interface AccountMapper extends BaseMapper<Account> {

    @Update("""
            update account
            set balance = balance - #{amount}
            where user_id = #{userId}
              and balance >= #{amount}
            """)
    int debitIfEnough(@Param("userId") Long userId, @Param("amount") BigDecimal amount);
}