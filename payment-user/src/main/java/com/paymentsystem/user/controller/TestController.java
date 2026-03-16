package com.paymentsystem.user.controller;

import com.paymentsystem.common.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test")
    public Result<String> test() {
        return Result.success("payment-user started");
    }
}