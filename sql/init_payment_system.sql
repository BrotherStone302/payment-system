CREATE DATABASE IF NOT EXISTS payment_system DEFAULT CHARACTER SET utf8mb4;
USE payment_system;

-- 1. 账户表
DROP TABLE IF EXISTS account;
CREATE TABLE account (
                         id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                         user_id BIGINT NOT NULL COMMENT '用户ID',
                         balance DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '账户余额',
                         frozen_amount DECIMAL(18,2) NOT NULL DEFAULT 0.00 COMMENT '冻结金额',
                         status INT NOT NULL DEFAULT 1 COMMENT '账户状态：1正常，0禁用',
                         create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                         update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                         PRIMARY KEY (id),
                         UNIQUE KEY uk_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账户表';

-- 2. 账户流水表
DROP TABLE IF EXISTS account_flow;
CREATE TABLE account_flow (
                              id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                              user_id BIGINT NOT NULL COMMENT '用户ID',
                              account_id BIGINT NOT NULL COMMENT '账户ID',
                              trade_no VARCHAR(64) NOT NULL COMMENT '交易单号',
                              change_type INT NOT NULL COMMENT '变动类型：1扣款，2入账',
                              amount DECIMAL(18,2) NOT NULL COMMENT '变动金额',
                              balance_before DECIMAL(18,2) NOT NULL COMMENT '变动前余额',
                              balance_after DECIMAL(18,2) NOT NULL COMMENT '变动后余额',
                              create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                              update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                              PRIMARY KEY (id),
                              KEY idx_user_id (user_id),
                              KEY idx_account_id (account_id),
                              KEY idx_trade_no (trade_no),
                              KEY idx_change_type (change_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账户流水表';

-- 3. 交易订单表
DROP TABLE IF EXISTS trade_order;
CREATE TABLE trade_order (
                             id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                             trade_no VARCHAR(64) NOT NULL COMMENT '交易单号',
                             from_user_id BIGINT NOT NULL COMMENT '转出用户ID',
                             to_user_id BIGINT NOT NULL COMMENT '转入用户ID',
                             amount DECIMAL(18,2) NOT NULL COMMENT '交易金额',
                             status INT NOT NULL DEFAULT 0 COMMENT '交易状态：0处理中，1成功，2失败',
                             create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                             update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                             PRIMARY KEY (id),
                             UNIQUE KEY uk_trade_no (trade_no),
                             KEY idx_from_user_id (from_user_id),
                             KEY idx_to_user_id (to_user_id),
                             KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交易订单表';

-- 4. 对账记录表
DROP TABLE IF EXISTS reconcile_record;
CREATE TABLE reconcile_record (
                                  id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
                                  trade_no VARCHAR(64) NOT NULL COMMENT '交易单号',
                                  from_user_id BIGINT NOT NULL COMMENT '转出用户ID',
                                  to_user_id BIGINT NOT NULL COMMENT '转入用户ID',
                                  amount DECIMAL(18,2) NOT NULL COMMENT '交易金额',
                                  status INT NOT NULL DEFAULT 0 COMMENT '状态：0初始化，1成功，2失败',
                                  message_body TEXT COMMENT '消息体',
                                  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                                  PRIMARY KEY (id),
                                  KEY idx_trade_no (trade_no),
                                  KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='对账记录表';

-- 5. 初始化账户测试数据
INSERT INTO account (user_id, balance, frozen_amount, status)
VALUES
    (1, 1000.00, 0.00, 1),
    (2, 1000.00, 0.00, 1);