# payment-system

基于微服务的支付系统设计与实现。

## 项目简介

本项目围绕支付场景，构建账户、交易、对账等核心微服务模块，重点实现转账主流程、消息驱动对账、异常状态流转与补偿机制，用于训练后端工程能力，并服务于后续就业与毕业设计。

## 技术栈

- Java
- Spring Boot
- MySQL
- Redis
- RocketMQ
- Seata
- Maven

## 模块说明

- `payment-account`：账户模块，负责账户信息与余额相关能力
- `payment-trade`：交易模块，负责转账主流程与交易消息发送
- `payment-reconcile`：对账模块，负责消费交易成功消息、生成对账记录、异常处理与补偿
- `payment-common`：公共模块，提供通用返回结构等基础能力
- `payment-user`：用户模块，提供用户相关基础能力

## 当前已实现功能

- 微服务基础拆分
- 转账主流程
- RocketMQ 交易成功消息发送
- 对账记录生成
- 对账记录查询、按状态筛选、汇总统计
- 对账异常标记与恢复
- 批量异常补偿
- 定时自动补偿任务
- 第一轮查询接口压测

## 当前项目亮点

- 基于 RocketMQ 实现交易与对账解耦，体现最终一致性处理思路
- 支持异常状态流转、批量补偿与定时自动补偿
- 基于状态校验实现基础幂等控制
- 持续优化接口设计与返回结构

## 启动说明

1. 启动 MySQL、Redis、RocketMQ 等依赖环境
2. 依次启动：
    - `payment-account`
    - `payment-trade`
    - `payment-reconcile`
    - `payment-user`
3. 使用 `trade-test.http`、`account-test.http` 等文件进行接口测试

## 后续计划

- 对账列表分页优化
- 更完整的压测与性能分析
- 分布式事务方案对比
- 完善项目文档与架构图