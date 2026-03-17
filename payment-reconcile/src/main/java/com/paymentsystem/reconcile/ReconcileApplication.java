package com.paymentsystem.reconcile;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.paymentsystem.reconcile.mapper")
@SpringBootApplication
public class ReconcileApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReconcileApplication.class, args);
    }
}