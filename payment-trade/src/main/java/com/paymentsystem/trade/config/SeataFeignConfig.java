package com.paymentsystem.trade.config;

import feign.RequestInterceptor;
import io.seata.core.context.RootContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
public class SeataFeignConfig {

    @Bean
    public RequestInterceptor seataFeignRequestInterceptor() {
        return requestTemplate -> {
            String xid = RootContext.getXID();
            if (StringUtils.hasText(xid)) {
                requestTemplate.header(RootContext.KEY_XID, xid);
            }
        };
    }
}
