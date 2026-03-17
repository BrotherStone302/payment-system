package com.paymentsystem.reconcile.consumer;

import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(
        topic = "trade-success-topic",
        consumerGroup = "trade-success-consumer-group",
        messageModel = MessageModel.CLUSTERING
)
public class TradeSuccessConsumer implements RocketMQListener<String> {

    @Override
    public void onMessage(String message) {
        System.out.println("========== Reconcile 收到交易成功消息 ==========");
        System.out.println(message);
    }
}
