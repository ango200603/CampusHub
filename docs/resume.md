# CampusHub 简历描述参考

本文提供面向 Java 后端实习简历的项目表述。项目定位是个人微服务实战项目，不建议写成生产级系统或商业项目。

## 项目描述

CampusHub 是一个校园服务聚合平台的本地微服务实战项目，基于 Java 21、Spring Boot 3、Spring Cloud Alibaba、Gateway、Nacos、OpenFeign、MyBatis-Plus、MySQL、Redis、RabbitMQ、MinIO 和 JWT 实现。项目覆盖验证码登录、用户资料、文件上传、AI/OCR 异步解析、二手商品发布、订单创建、模拟支付、站内信通知和后台查询等业务链路，重点练习微服务拆分、服务调用、异步消息、缓存和分布式锁等 Java 后端能力。

## 项目亮点

1. 采用 Maven 多模块拆分公共组件、网关和业务服务，公共模块封装统一响应、异常、JWT、Redis key、MQ 常量和编号生成，降低重复代码。
2. 使用 Gateway 作为统一入口，完成路由转发、JWT 初步校验、白名单放行、CORS 和 Redis 简单限流，下游服务通过请求头获取用户上下文。
3. 验证码登录链路结合 Redis、RabbitMQ 和 OpenFeign：Redis 保存验证码和限流 key，RabbitMQ 投递 mock 短信消息，auth-service 通过 user-service 完成用户自动注册。
4. 文件上传链路使用 MinIO 存储文件对象，file-service 写入文件记录后投递 `file.parse` 消息，ai-service 异步消费并更新任务状态，体现异步处理和手动 ack。
5. 二手交易链路覆盖商品锁定、订单创建、模拟支付和站内信通知：order-service 使用 Redis 锁控制重复下单，pay-service 通过条件更新保证支付成功幂等，再用 MQ 驱动订单状态变化。

## 面试讲解版本

可以按下面顺序讲，不需要把每个技术名词都堆出来。

1. 项目背景：这是一个校园场景的微服务练习项目，我把常见校园服务拆成登录、用户、文件、AI、交易、订单、支付、通知和后台服务，用 Gateway 统一入口。
2. 登录链路：用户先请求验证码，auth-service 把验证码写 Redis，并发一条短信消息到 RabbitMQ。登录时校验验证码，再通过 Feign 调 user-service 查询或创建用户，最后签发 JWT。Gateway 后续校验 JWT 并把用户信息透传给下游。
3. 文件和 AI 链路：文件先上传到 MinIO，数据库只保存元数据和 URL。上传成功后 file-service 发解析消息，ai-service 消费后创建任务，模拟 OCR 和摘要，把任务从 PENDING 更新到 PROCESSING，再到 SUCCESS。失败会写 FAILED 并进入死信。
4. 交易链路：卖家发布商品后，买家创建订单。order-service 会拿 Redis 锁并调用 trade-service 锁定商品，避免重复购买。支付服务创建支付单，模拟支付成功后发送 MQ 消息，order-service 消费后把订单改为 PAID，并通知 trade-service 标记商品 SOLD。
5. 工程化：项目补了 `.env.example`、CI、Docker Compose、本地启动脚本、接口文档、架构图、运行手册和单元测试，方便本地复现和代码检查。

## 简历写法示例

```text
CampusHub 校园服务聚合平台 | Java 后端个人微服务实战项目
- 基于 Java 21、Spring Boot 3、Spring Cloud Alibaba、Nacos、Gateway、OpenFeign、MyBatis-Plus、MySQL、Redis、RabbitMQ、MinIO、JWT 实现校园场景微服务项目。
- 负责多模块后端架构搭建，封装统一响应、异常处理、JWT 工具、Redis key、MQ 常量和基础实体，完成登录、用户、文件、AI、交易、订单、支付、通知等服务基础功能。
- 设计验证码登录链路，使用 Redis 存储验证码和限流标记，通过 RabbitMQ 投递 mock 短信消息，并通过 OpenFeign 调用用户服务完成自动注册。
- 设计文件上传与 AI 异步解析流程，文件存储到 MinIO，解析任务通过 RabbitMQ 异步消费，支持任务状态流转和失败死信处理。
- 设计二手交易下单支付流程，使用 Redis 锁控制重复下单，支付成功通过 RabbitMQ 推动订单状态更新，并生成站内信通知。
```

## 不建议这样写

- 不要写“生产级高并发系统”，当前项目定位是本地实战项目。
- 不要写“真实支付接入”，当前支付是 mock 支付。
- 不要写“真实 AI 大模型解析”，当前 OCR 和摘要是 mock 实现。
- 不要写“完整权限系统”，当前只做登录态和基础后台接口，RBAC 仍可增强。

