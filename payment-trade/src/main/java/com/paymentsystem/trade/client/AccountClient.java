package com.paymentsystem.trade.client;

import com.paymentsystem.common.dto.AccountChangeRequest;
import com.paymentsystem.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-account", url = "http://localhost:8082")
public interface AccountClient {

    @PostMapping("/accounts/debit")
    Result<Void> debit(@RequestBody AccountChangeRequest request);

    @PostMapping("/accounts/credit")
    Result<Void> credit(@RequestBody AccountChangeRequest request);
}