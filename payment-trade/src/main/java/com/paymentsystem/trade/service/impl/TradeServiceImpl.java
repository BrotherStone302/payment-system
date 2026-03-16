package com.paymentsystem.trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.paymentsystem.account.dto.AccountChangeRequest;
import com.paymentsystem.common.exception.BusinessException;
import com.paymentsystem.common.result.Result;
import com.paymentsystem.trade.client.AccountClient;
//import com.paymentsystem.trade.dto.TradeSuccessMessage;
import com.paymentsystem.trade.dto.TransferRequest;
import com.paymentsystem.trade.entity.TradeOrder;
import com.paymentsystem.trade.mapper.TradeOrderMapper;
//import com.paymentsystem.trade.producer.TradeProducer;
import com.paymentsystem.trade.service.TradeService;
//import com.paymentsystem.trade.service.TradeStatusService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class TradeServiceImpl implements TradeService {

    private final TradeOrderMapper tradeOrderMapper;
    private final AccountClient accountClient;
    //private final TradeStatusService tradeStatusService;
    //private final TradeProducer tradeProducer;

    /*public TradeServiceImpl(TradeOrderMapper tradeOrderMapper,
                            AccountClient accountClient,
                            TradeStatusService tradeStatusService,
                            TradeProducer tradeProducer) {
        this.tradeOrderMapper = tradeOrderMapper;
        this.accountClient = accountClient;
        this.tradeStatusService = tradeStatusService;
        this.tradeProducer = tradeProducer;
    }*/
    public TradeServiceImpl(TradeOrderMapper tradeOrderMapper,
                            AccountClient accountClient){
        this.tradeOrderMapper = tradeOrderMapper;
        this.accountClient = accountClient;
    }

    @Override
    @Transactional
    public void transfer(TransferRequest request) {
        System.out.println("========== 1. 开始执行 transfer ==========");

        TradeOrder existedOrder = tradeOrderMapper.selectByTradeNo(request.getTradeNo());
        System.out.println("========== 2. 查询 tradeNo 结果: " + (existedOrder == null ? "不存在" : "已存在") + " ==========");

        if (existedOrder != null) {
            throw new BusinessException(500, "交易单号已存在，请勿重复提交");
        }

        TradeOrder tradeOrder = new TradeOrder();
        tradeOrder.setTradeNo(request.getTradeNo());
        tradeOrder.setFromUserId(request.getFromUserId());
        tradeOrder.setToUserId(request.getToUserId());
        tradeOrder.setAmount(request.getAmount());
        tradeOrder.setStatus(TradeOrder.STATUS_PROCESSING);

        try {
            tradeOrderMapper.insert(tradeOrder);
            System.out.println("========== 3. 交易单插入成功 ==========");
        } catch (DuplicateKeyException e) {
            throw new BusinessException(500, "交易单号已存在，请勿重复提交");
        }

        try {
            AccountChangeRequest debitRequest = new AccountChangeRequest();
            debitRequest.setUserId(request.getFromUserId());
            debitRequest.setAmount(request.getAmount());
            debitRequest.setTradeNo(tradeOrder.getTradeNo());

            System.out.println("========== 4. 开始调用扣款 ==========");
            Result<Void> debitResult = accountClient.debit(debitRequest);
            System.out.println("========== 5. 扣款返回: " +
                    (debitResult == null ? "null" : debitResult.getCode() + " / " + debitResult.getMessage())
                    + " ==========");

            if (debitResult == null || debitResult.getCode() != 200) {
                throw new BusinessException(500,
                        debitResult == null ? "账户扣款失败" : debitResult.getMessage());
            }

            AccountChangeRequest creditRequest = new AccountChangeRequest();
            creditRequest.setUserId(request.getToUserId());
            creditRequest.setAmount(request.getAmount());
            creditRequest.setTradeNo(tradeOrder.getTradeNo());

            System.out.println("========== 6. 开始调用入账 ==========");
            Result<Void> creditResult = accountClient.credit(creditRequest);
            System.out.println("========== 7. 入账返回: " +
                    (creditResult == null ? "null" : creditResult.getCode() + " / " + creditResult.getMessage())
                    + " ==========");

            if (creditResult == null || creditResult.getCode() != 200) {
                throw new BusinessException(500,
                        creditResult == null ? "账户入账失败" : creditResult.getMessage());
            }

            //System.out.println("========== 8. 开始更新交易状态为 SUCCESS ==========");
            //tradeStatusService.updateToSuccess(tradeOrder.getId());
            System.out.println("========== 8. 开始更新交易状态为 SUCCESS ==========");
            tradeOrder.setStatus(1);
            tradeOrderMapper.updateById(tradeOrder);

            //TradeSuccessMessage message = new TradeSuccessMessage();
            //message.setTradeNo(tradeOrder.getTradeNo());
            //message.setFromUserId(tradeOrder.getFromUserId());
            //message.setToUserId(tradeOrder.getToUserId());
            //message.setAmount(tradeOrder.getAmount());

            //System.out.println("========== 9. 开始发送 RocketMQ 消息 ==========");
            //tradeProducer.sendTradeSuccess(message);
            System.out.println("========== 9. 暂时跳过 RocketMQ 发送 ==========");

            System.out.println("========== 10. transfer 执行完成 ==========");

        } catch (Exception e) {
            System.out.println("========== X. transfer 失败，准备更新交易状态为 FAIL ==========");
            e.printStackTrace();
            //tradeStatusService.updateToFail(tradeOrder.getId());
            if (tradeOrder != null && tradeOrder.getId() != null) {
                tradeOrder.setStatus(2);
                tradeOrderMapper.updateById(tradeOrder);
            }
            throw e;
        }
    }

    @Override
    public TradeOrder getByTradeNo(String tradeNo) {
        LambdaQueryWrapper<TradeOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TradeOrder::getTradeNo, tradeNo);
        return tradeOrderMapper.selectOne(wrapper);
    }
}