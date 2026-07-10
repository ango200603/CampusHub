# CampusHub 校园服务聚合平台

CampusHub 是一个能在本地跑起来的校园微服务项目。主流程包括手机号验证码登录、用户资料、文件上传、AI/OCR 异步解析、二手交易、订单、模拟支付、站内通知和后台查询。

## 项目定位

CampusHub 定位为本地微服务实战项目，适合用于学习和展示 Java 后端工程能力。项目强调链路完整、模块边界清晰、基础设施可复现，不声称可以不经改造直接用于生产环境。

## 技术栈

- 后端：Java 21、Spring Boot 3.3、Spring Cloud Alibaba、Spring Cloud Gateway、Nacos、OpenFeign、Sentinel、MyBatis-Plus、JWT
- 中间件：MySQL 8、Redis 7、RabbitMQ Management、MinIO
- 前端：Next.js、TypeScript、Tailwind CSS
- 工程：Maven 多模块、Docker Compose

## 模块

- `campus-common`：统一响应、异常、JWT、Redis 锁、MQ 常量、编号生成
- `campus-gateway`：统一入口、路由、JWT 鉴权、CORS、白名单、Redis 限流
- `campus-auth-service`：短信验证码、验证码登录、JWT、登出
- `campus-user-service`：用户资料、积分增减、积分缓存
- `campus-file-service`：MinIO 上传、文件记录、投递 AI 解析消息
- `campus-ai-service`：AI 任务、RabbitMQ 消费、模拟 OCR/摘要、失败死信
- `campus-trade-service`：二手商品发布、列表、详情缓存、锁定、售出
- `campus-order-service`：订单创建、取消、超时关闭、支付成功消费
- `campus-pay-service`：模拟支付单、模拟支付成功、支付幂等
- `campus-notice-service`：mock 短信日志、站内信、已读
- `campus-admin-service`：用户、文件、AI、订单和统计查询

## 文档目录

- `docs/api.md`：按服务整理的接口说明、请求参数、响应示例和错误码。
- `docs/api-test.md`：按完整业务链路编写的接口测试步骤，可用于本地验收。
- `docs/architecture.md`：系统架构图、登录流程、文件解析流程、交易支付流程。
- `docs/docker-deploy.md`：完整 Docker Compose 编排、服务说明、启动顺序和排障。
- `docs/deploy.md`：本地启动、Docker Compose、生产环境注意事项。
- `docs/runbook.md`：从基础设施到微服务、前端和链路验证的运行手册。
- `docs/resume.md`：适合 Java 后端实习简历的项目描述和面试讲解稿。

## 已实现能力

- 统一响应、业务异常、全局异常处理、JWT 工具、Redis key 和 RabbitMQ 常量。
- Gateway 统一路由、JWT 初步校验、白名单、CORS 和 Redis 简单限流。
- 手机号验证码登录，支持 Redis 验证码、短信发送限流、RabbitMQ mock 短信、用户自动注册。
- 文件上传到 MinIO，写入文件记录，并通过 RabbitMQ 触发 AI 异步解析任务。
- AI 任务状态流转，支持 `PENDING`、`PROCESSING`、`SUCCESS`、`FAILED` 和死信兜底。
- 二手商品发布、列表、详情、下架，商品详情具备缓存思路。
- 订单创建、取消、超时关闭、支付成功消费，使用 Redis 锁减少重复下单。
- 模拟支付单创建和支付成功回调，支付成功使用条件更新保证幂等。
- 站内信创建、列表、已读，以及后台统计和聚合查询接口。
- Docker Compose、`.env.example`、CI、启动脚本、接口文档、运行手册和基础单元测试。

## Docker 一键启动完整项目

构建并启动全部服务：

```bash
docker compose up -d --build
```

查看 Gateway 日志：

```bash
docker compose logs -f campus-gateway
```

停止服务：

```bash
docker compose down
```

重建数据库和所有持久化数据：

```bash
docker compose down -v
docker compose up -d --build
```

访问地址：

- Frontend：http://localhost:3000
- Gateway：http://localhost:8080
- Nacos：http://localhost:8848/nacos
- RabbitMQ：http://localhost:15672
- MinIO Console：http://localhost:9001

MySQL 容器内地址固定为 `mysql:3306`，后端服务通过该地址访问；宿主机默认映射到 `localhost:3307`，避免和本机已有 MySQL 冲突。如需改为 `3306:3306`，在 `.env` 中设置 `MYSQL_HOST_PORT=3306`。

## 本地开发启动

如果要在 IDE 中手动运行各个微服务，可以只启动基础组件：

```bash
docker compose up -d mysql redis rabbitmq nacos minio
```

第一次启动 MySQL 会自动执行 `sql/init.sql`。如果本机已有旧 volume，表结构又对不上，可以重建基础组件：

```bash
docker compose down -v
docker compose up -d mysql redis rabbitmq nacos minio
```

编译后端：

```bash
mvn clean package -DskipTests
```

按下面顺序分别启动服务：

```bash
mvn -pl campus-user-service spring-boot:run
mvn -pl campus-notice-service spring-boot:run
mvn -pl campus-auth-service spring-boot:run
mvn -pl campus-file-service spring-boot:run
mvn -pl campus-ai-service spring-boot:run
mvn -pl campus-trade-service spring-boot:run
mvn -pl campus-order-service spring-boot:run
mvn -pl campus-pay-service spring-boot:run
mvn -pl campus-admin-service spring-boot:run
mvn -pl campus-gateway spring-boot:run
```

前端：

```bash
cd frontend/campus-web
npm install
npm run dev
```

## 地址

- Gateway：http://localhost:8080
- Frontend：http://localhost:3000
- Nacos：http://localhost:8848/nacos
- RabbitMQ Management：http://localhost:15672，`guest` / `guest`
- MinIO Console：http://localhost:9001，`minioadmin` / `minioadmin`

## 核心接口

### 认证

- `POST /auth/sms/send`：发送验证码，Redis key 为 `sms:code:{phone}`，短信消息由 notice-service 打印
- `POST /auth/login/sms`：验证码登录，用户不存在时自动注册，返回 JWT
- `POST /auth/logout`：退出登录
- `GET /auth/me`：解析当前 JWT

### 用户

- `GET /users/me`
- `PUT /users/me`
- `GET /users/{id}`
- `GET /users/points/me`
- `POST /users/points/increase`
- `POST /users/points/decrease`

### 文件与 AI

- `POST /files/upload`
- `GET /files/{id}`
- `GET /files/my`
- `DELETE /files/{id}`
- `POST /ai/tasks`
- `GET /ai/tasks/{id}`
- `GET /ai/tasks/my`

### 交易、订单、支付、通知

- `POST /trades/items`
- `GET /trades/items`
- `GET /trades/items/{id}`
- `PUT /trades/items/{id}`
- `DELETE /trades/items/{id}`
- `POST /orders`
- `GET /orders/{id}`
- `GET /orders/my`
- `POST /orders/{id}/cancel`
- `POST /pay/create`
- `POST /pay/mock-success`
- `GET /pay/records/{orderNo}`
- `GET /notices/my`
- `PUT /notices/{id}/read`

### 后台

- `GET /admin/users`
- `GET /admin/orders`
- `GET /admin/files`
- `GET /admin/ai/tasks`
- `GET /admin/stats`

## 业务流程

1. 手机号登录：auth-service 生成验证码，Redis 保存 5 分钟；验证码消息进 RabbitMQ，notice-service 打印 mock 短信；登录成功后 auth-service 通过 OpenFeign 调 user-service，自动注册或查询用户，再签发 JWT。
2. 文件上传与 AI 解析：file-service 上传 MinIO，写 `file_records`，投递 `ai.parse`；ai-service 手动 ack 消费，模拟 1-3 秒 OCR/摘要，成功写 `SUCCESS`，失败写 `FAILED` 并进入死信。
3. 商品下单：trade-service 发布商品并缓存详情；order-service 用 Redis 锁 `lock:trade:item:{itemId}` 防重复购买，通过 Feign 锁定商品，创建 `UNPAID` 订单，并发送超时关闭消息。
4. 模拟支付：pay-service 创建支付单并幂等标记成功，投递 `order.pay.success`；order-service 消费后把订单改成 `PAID`，调用 trade-service 标记 `SOLD`，再发一条站内信。

## Redis

- `sms:code:{phone}`：验证码，5 分钟过期
- `sms:limit:{phone}`：短信限流，60 秒过期
- `trade:item:{itemId}`：商品详情缓存，商品更新或状态变化时删除
- `trade:hot:list`：热门商品缓存预留
- `user:points:{userId}`：积分缓存
- `lock:trade:item:{itemId}`、`lock:user:points:{userId}`、`lock:order:{orderNo}`：分布式锁，Lua 校验 value 后释放

## RabbitMQ

- `campus.sms.exchange` / `sms.send.queue`：mock 短信
- `campus.ai.exchange` / `ai.parse.queue`：AI 异步解析
- `campus.order.exchange` / `order.timeout.queue`：订单超时关闭
- `campus.order.exchange` / `order.pay.success.queue`：支付成功
- `campus.notice.exchange` / `notice.send.queue`：站内信
- `campus.dead.exchange` / `dead.letter.queue`：死信

关键消费者使用 JSON 消息和手动 ack；失败时先更新业务状态，再 nack 到死信队列。

## 生产环境注意事项

- 默认账号只给本地用：MySQL `root/root`、RabbitMQ `guest/guest`、MinIO `minioadmin/minioadmin`、Nacos 关闭鉴权都不能直接上线。
- `JWT_SECRET` 换成高熵密钥，走环境变量或密钥管理；Gateway、auth-service 和其他解析 JWT 的服务要保持一致。
- MinIO 要配置强密钥、bucket 策略、持久化存储、备份和 TLS；不要直接暴露内部 endpoint。
- RabbitMQ 建独立 vhost 和最小权限账号，打开持久化、死信监控、TLS 和容量告警。
- Nacos 开鉴权，用 namespace/group 隔离环境，并限制控制台和注册端口来源。
- Sentinel Dashboard 不要公网暴露；限流/熔断规则要持久化。
- 后台接口、积分变更接口目前只依赖登录态，正式使用前要补 RBAC、审计日志和操作风控。

## MySQL 索引

- `users.uk_users_phone`：手机号唯一，支撑登录自动注册
- `file_records.idx_file_user_created`：我的文件列表
- `ai_tasks.idx_ai_user_created`、`idx_ai_status_created`：用户任务和后台状态筛选
- `trade_items.idx_trade_category_status`、`idx_trade_seller_created`：商品列表和卖家管理
- `orders.uk_orders_order_no`、`idx_orders_buyer_created`、`idx_orders_status_created`：订单幂等、我的订单、超时扫描
- `pay_records.uk_pay_no`、`idx_pay_order_no`：支付幂等和订单号查询
- `notices.idx_notice_user_created`：我的通知

## 仍可增强能力

- 后台接口目前偏基础，后续可补 RBAC、菜单权限、操作审计和管理员登录。
- AI/OCR 目前是 mock 实现，后续可接入真实 OCR 或大模型服务，并增加结果结构化校验。
- 支付目前是 mock 支付，后续可增加签名校验、回调验签、支付渠道适配和对账任务。
- 交易搜索目前依赖基础查询，后续可接入 Elasticsearch 或数据库全文索引。
- 观测能力可继续补日志 traceId、链路追踪、Prometheus 指标和告警规则。
- 测试覆盖可继续补 Controller 层、MQ 消费者、Feign fallback 和集成测试。
