package com.paymentsystem.reconcile.consumer;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.paymentsystem.common.dto.TradeSuccessMessage;
import com.paymentsystem.reconcile.entity.ReconcileRecord;
import com.paymentsystem.reconcile.entity.TradeOrder;
import com.paymentsystem.reconcile.mapper.ReconcileRecordMapper;
import com.paymentsystem.reconcile.mapper.TradeOrderMapper;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.mq.enabled", havingValue = "true")
@RocketMQMessageListener(
        topic = "trade-success-topic",
        consumerGroup = "trade-success-consumer-group",
        messageModel = MessageModel.CLUSTERING
)
public class TradeSuccessConsumer implements RocketMQListener<String> {

    private final ObjectMapper objectMapper;
    private final ReconcileRecordMapper reconcileRecordMapper;
    private final TradeOrderMapper tradeOrderMapper;

    public TradeSuccessConsumer(ObjectMapper objectMapper,
                                ReconcileRecordMapper reconcileRecordMapper,
                                TradeOrderMapper tradeOrderMapper) {
        this.objectMapper = objectMapper;
        this.reconcileRecordMapper = reconcileRecordMapper;
        this.tradeOrderMapper = tradeOrderMapper;
    }

    @Override
    public void onMessage(String message) {
        try {
            System.out.println("========== Reconcile 收到交易成功消息 ==========");
            System.out.println(message);

            TradeSuccessMessage dto = objectMapper.readValue(message, TradeSuccessMessage.class);

            ReconcileRecord record = new ReconcileRecord();
            record.setTradeNo(dto.getTradeNo());
            record.setFromUserId(dto.getFromUserId());
            record.setToUserId(dto.getToUserId());
            record.setAmount(dto.getAmount());
            record.setStatus(0);
            record.setMessageBody(message);

            reconcileRecordMapper.insert(record);

            TradeOrder tradeOrder = tradeOrderMapper.selectOne(
                    new LambdaQueryWrapper<TradeOrder>()
                            .eq(TradeOrder::getTradeNo, dto.getTradeNo())
            );

            boolean match = tradeOrder != null
                    && tradeOrder.getFromUserId().equals(dto.getFromUserId())
                    && tradeOrder.getToUserId().equals(dto.getToUserId())
                    && tradeOrder.getAmount().compareTo(dto.getAmount()) == 0
                    && tradeOrder.getStatus().equals(1);

            ReconcileRecord updateRecord = new ReconcileRecord();
            updateRecord.setId(record.getId());
            updateRecord.setStatus(match ? 1 : 2);
            reconcileRecordMapper.updateById(updateRecord);

            System.out.println("========== 对账完成，结果: " + (match ? "成功" : "异常") + " ==========");

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("消费交易成功消息失败", e);
        }
    }
}