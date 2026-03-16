package com.paymentsystem.trade.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymentsystem.trade.dto.TradeSuccessMessage;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Component;

@Component
public class TradeProducer {

    private final RocketMQTemplate rocketMQTemplate;
    private final ObjectMapper objectMapper;

    public TradeProducer(RocketMQTemplate rocketMQTemplate, ObjectMapper objectMapper) {
        this.rocketMQTemplate = rocketMQTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendTradeSuccess(TradeSuccessMessage message) {
        try {
            String body = objectMapper.writeValueAsString(message);
            rocketMQTemplate.convertAndSend("trade-success-topic", body);
            System.out.println("========== MQ发送成功: " + body + " ==========");
        } catch (JsonProcessingException e) {
            throw new RuntimeException("交易成功消息序列化失败", e);
        }
    }
}