package com.paymentsystem.account;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.seata.spring.annotation.datasource.EnableAutoDataSourceProxy;

@MapperScan("com.paymentsystem.account.mapper")
@SpringBootApplication(scanBasePackages = "com.paymentsystem")
public class AccountApplication {
    public static void main(String[] args) {
        SpringApplication.run(AccountApplication.class, args);
    }
}
