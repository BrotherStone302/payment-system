package com.paymentsystem.trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.paymentsystem.common.exception.BusinessException;
import com.paymentsystem.common.result.Result;
import com.paymentsystem.trade.client.AccountClient;
import com.paymentsystem.common.dto.TradeSuccessMessage;
import com.paymentsystem.trade.dto.TransferRequest;
import com.paymentsystem.trade.entity.TradeOrder;
import com.paymentsystem.trade.mapper.TradeOrderMapper;
import com.paymentsystem.trade.producer.TradeProducer;
import com.paymentsystem.trade.service.TradeService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.paymentsystem.trade.service.TradeStatusService;
import com.paymentsystem.common.dto.AccountChangeRequest;

@Service
public class TradeServiceImpl implements TradeService {

    private final TradeOrderMapper tradeOrderMapper;
    private final AccountClient accountClient;
    private final TradeProducer tradeProducer;
    private final TradeStatusService tradeStatusService;

    public TradeServiceImpl(TradeOrderMapper tradeOrderMapper,
                            AccountClient accountClient,
                            TradeProducer tradeProducer,
                            TradeStatusService tradeStatusService) {
        this.tradeOrderMapper = tradeOrderMapper;
        this.accountClient = accountClient;
        this.tradeProducer = tradeProducer;
        this.tradeStatusService = tradeStatusService;
    }

    @Override
    @Transactional(noRollbackFor = BusinessException.class)
    public void transfer(TransferRequest request) {
        System.out.println("========== 1. 开始执行 transfer ==========");

        if (request == null) {
            throw new BusinessException(400, "请求不能为空");
        }
        if (request.getTradeNo() == null || request.getTradeNo().trim().isEmpty()) {
            throw new BusinessException(400, "交易单号不能为空");
        }
        if (request.getFromUserId() == null) {
            throw new BusinessException(400, "转出用户不能为空");
        }
        if (request.getToUserId() == null) {
            throw new BusinessException(400, "转入用户不能为空");
        }
        if (request.getAmount() == null) {
            throw new BusinessException(400, "转账金额不能为空");
        }
        if (request.getAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new BusinessException(400, "转账金额必须大于0");
        }
        if (request.getFromUserId().equals(request.getToUserId())) {
            throw new BusinessException(400, "转出用户和转入用户不能相同");
        }

        TradeOrder existedOrder = tradeOrderMapper.selectByTradeNo(request.getTradeNo());
        System.out.println("========== 2. 查询 tradeNo 结果: " + (existedOrder == null ? "不存在" : "已存在") + " ==========");

        if (existedOrder != null) {
            throw new BusinessException(400, "交易单号已存在，请勿重复提交");
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
            throw new BusinessException(400, "交易单号已存在，请勿重复提交");
        }

        boolean debitSuccess = false;

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

            if (debitResult == null) {
                throw new BusinessException(500, "账户扣款服务调用失败");
            }
            if (debitResult.getCode() != 200) {
                throw new BusinessException(400, debitResult.getMessage());
            }

            debitSuccess = true;

            AccountChangeRequest creditRequest = new AccountChangeRequest();
            creditRequest.setUserId(request.getToUserId());
            creditRequest.setAmount(request.getAmount());
            creditRequest.setTradeNo(tradeOrder.getTradeNo());

            System.out.println("========== 6. 开始调用入账 ==========");
            Result<Void> creditResult = accountClient.credit(creditRequest);
            System.out.println("========== 7. 入账返回: " +
                    (creditResult == null ? "null" : creditResult.getCode() + " / " + creditResult.getMessage())
                    + " ==========");

            if (creditResult == null) {
                throw new BusinessException(500, "账户入账服务调用失败");
            }
            if (creditResult.getCode() != 200) {
                throw new BusinessException(400, creditResult.getMessage());
            }

            System.out.println("========== 8. 开始更新交易状态为 SUCCESS ==========");
            tradeOrder.setStatus(TradeOrder.STATUS_SUCCESS);
            tradeOrderMapper.updateById(tradeOrder);

            TradeSuccessMessage message = new TradeSuccessMessage();
            message.setTradeNo(tradeOrder.getTradeNo());
            message.setFromUserId(tradeOrder.getFromUserId());
            message.setToUserId(tradeOrder.getToUserId());
            message.setAmount(tradeOrder.getAmount());

            System.out.println("========== 9. 开始发送 RocketMQ 消息 ==========");
            tradeProducer.sendTradeSuccess(message);

            System.out.println("========== 10. transfer 执行完成 ==========");

        } catch (Exception e) {
            System.out.println("========== X. transfer 失败，准备更新交易状态为 FAIL ==========");
            e.printStackTrace();

            if (debitSuccess) {
                System.out.println("========== X1. 检测到已扣款，开始执行补偿退款 ==========");

                try {
                    AccountChangeRequest refundRequest = new AccountChangeRequest();
                    refundRequest.setUserId(request.getFromUserId());
                    refundRequest.setAmount(request.getAmount());
                    refundRequest.setTradeNo(request.getTradeNo() + "_REFUND");

                    Result<Void> refundResult = accountClient.credit(refundRequest);
                    System.out.println("========== X2. 补偿退款返回: " +
                            (refundResult == null ? "null" : refundResult.getCode() + " / " + refundResult.getMessage())
                            + " ==========");

                    if (refundResult == null || refundResult.getCode() != 200) {
                        tradeOrder.setStatus(TradeOrder.STATUS_FAIL);
                        tradeOrderMapper.updateById(tradeOrder);
                        throw new BusinessException(500, "转账失败，且补偿退款失败，请人工处理");
                    }

                    System.out.println("========== X3. 补偿退款成功 ==========");

                } catch (BusinessException be) {
                    throw be;
                } catch (Exception refundEx) {
                    refundEx.printStackTrace();
                    tradeOrder.setStatus(TradeOrder.STATUS_FAIL);
                    tradeOrderMapper.updateById(tradeOrder);
                    throw new BusinessException(500, "转账失败，且补偿退款异常，请人工处理");
                }
            }

            if (tradeOrder.getId() != null) {
                tradeOrder.setStatus(TradeOrder.STATUS_FAIL);
                tradeOrderMapper.updateById(tradeOrder);
            }

            if (e instanceof BusinessException) {
                throw (BusinessException) e;
            }
            throw new BusinessException(500, "转账失败");
        }
    }

    @Override
    public TradeOrder getByTradeNo(String tradeNo) {
        LambdaQueryWrapper<TradeOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TradeOrder::getTradeNo, tradeNo);
        return tradeOrderMapper.selectOne(wrapper);
    }
}