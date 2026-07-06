# CampusHub 校园服务聚合平台

CampusHub 是一个面向大学生的本地可运行微服务项目，覆盖手机号验证码登录、用户资料、文件上传、AI/OCR 异步解析、二手交易、订单、模拟支付、站内通知和后台管理。

## 技术栈

- 后端：Java 21、Spring Boot 3.3、Spring Cloud Alibaba、Spring Cloud Gateway、Nacos、OpenFeign、Sentinel、MyBatis-Plus、JWT
- 中间件：MySQL 8、Redis 7、RabbitMQ Management、MinIO
- 前端：Next.js、TypeScript、Tailwind CSS，移动端优先
- 工程：Maven 多模块、Docker Compose

## 微服务模块

- `campus-common`：统一响应、异常、JWT、Redis 分布式锁、MQ 常量、编号生成
- `campus-gateway`：统一入口、路由、JWT 鉴权、CORS、白名单、Redis 限流
- `campus-auth-service`：短信验证码、验证码登录、JWT、登出
- `campus-user-service`：用户资料、积分增减、积分缓存和并发控制
- `campus-file-service`：MinIO 上传、文件记录、发送 AI 解析消息
- `campus-ai-service`：AI 任务、RabbitMQ 消费、模拟 OCR/摘要、失败死信
- `campus-trade-service`：二手商品发布、浏览、详情缓存、锁定和售出
- `campus-order-service`：订单创建、取消、超时关闭、支付成功消费
- `campus-pay-service`：模拟支付单、模拟支付成功、支付幂等
- `campus-notice-service`：mock 短信日志、站内信、已读
- `campus-admin-service`：用户、文件、AI、订单和统计查询

## 本地启动

1. 启动基础组件：

   ```bash
   docker compose up -d
   ```

2. 首次启动 MySQL 会自动执行 `sql/init.sql`。如果你已经有旧 volume，可执行：

   ```bash
   docker compose down -v
   docker compose up -d
   ```

3. 编译后端：

   ```bash
   mvn clean package -DskipTests
   ```

4. 分别启动服务，建议顺序：

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

5. 启动前端：

   ```bash
   cd frontend/campus-web
   npm install
   npm run dev
   ```

## 访问地址

- Gateway：http://localhost:8080
- Nacos：http://localhost:8848/nacos
- RabbitMQ Management：http://localhost:15672，账号 `guest`，密码 `guest`
- MinIO Console：http://localhost:9001，账号 `minioadmin`，密码 `minioadmin`
- Frontend：http://localhost:3000

## 核心接口

### 认证

- `POST /auth/sms/send`：发送验证码，写入 Redis `sms:code:{phone}`，并发送 MQ 到 notice-service 打印 mock 短信
- `POST /auth/login/sms`：验证码登录，自动注册用户，返回 JWT
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

## 核心业务流程

1. 手机号登录：auth-service 生成验证码，Redis 保存 5 分钟，RabbitMQ 发送短信消息，notice-service 打印 mock 短信日志，登录成功后 auth-service 通过 OpenFeign 调用 user-service 自动注册或查询用户并返回 JWT。
2. 文件上传与 AI 解析：file-service 上传到 MinIO 并保存 `file_records`，发送 `ai.parse` 消息，ai-service 手动 ack 消费，模拟 1-3 秒 OCR/摘要，更新 `ai_tasks` 为 `SUCCESS`，失败时写入 `FAILED` 并进入死信。
3. 发布商品与创建订单：trade-service 发布商品并缓存详情，order-service 使用 Redis 锁 `lock:trade:item:{itemId}` 防重复购买，通过 Feign 锁定商品，创建 `UNPAID` 订单，发送超时关闭 TTL 消息。
4. 模拟支付成功：pay-service 创建支付单并幂等标记成功，发送 `order.pay.success`，order-service 幂等消费后改订单为 `PAID`，调用 trade-service 标记 `SOLD`，再发送站内信消息给 notice-service。

## Redis 使用场景

- `sms:code:{phone}`：验证码，5 分钟过期
- `sms:limit:{phone}`：短信限流，60 秒过期
- `trade:item:{itemId}`：商品详情缓存，更新和状态变化时删除
- `trade:hot:list`：热门商品缓存预留，商品变更时删除
- `user:points:{userId}`：积分缓存
- `lock:trade:item:{itemId}`、`lock:user:points:{userId}`、`lock:order:{orderNo}`：分布式锁，Lua 校验 value 后释放

## RabbitMQ 使用场景

- `campus.sms.exchange` / `sms.send.queue`：mock 短信
- `campus.ai.exchange` / `ai.parse.queue`：AI 异步解析
- `campus.order.exchange` / `order.timeout.queue`：订单超时关闭
- `campus.order.exchange` / `order.pay.success.queue`：支付成功最终一致性
- `campus.notice.exchange` / `notice.send.queue`：站内信
- `campus.dead.exchange` / `dead.letter.queue`：失败消息死信

消费者使用 JSON 消息和手动 ack；AI、订单、通知等关键消费者在失败时更新业务状态或 nack 到死信队列。

## MySQL 索引设计

- `users.uk_users_phone` 保证手机号唯一，支撑登录自动注册
- `file_records.idx_file_user_created` 支撑我的文件列表
- `ai_tasks.idx_ai_user_created`、`idx_ai_status_created` 支撑用户任务和后台状态筛选
- `trade_items.idx_trade_category_status`、`idx_trade_seller_created` 支撑商品列表和卖家管理
- `orders.uk_orders_order_no`、`idx_orders_buyer_created`、`idx_orders_status_created` 支撑订单幂等、我的订单、超时扫描
- `pay_records.uk_pay_no`、`idx_pay_order_no` 支撑支付幂等和订单号查询
- `notices.idx_notice_user_created` 支撑我的通知

## 面试亮点

- 网关统一 JWT 鉴权、白名单、CORS 和 Redis 限流
- 验证码登录链路解耦：Redis 存储、MQ 通知、用户服务自动注册
- 文件上传与 AI 解析异步化，手动 ack、失败状态、死信队列完整
- 订单支付通过 MQ 保证最终一致性，支付回调和订单消费都做幂等
- 商品购买使用 Redis 分布式锁和数据库条件更新，避免重复购买
- Maven 多模块边界清晰，适合继续扩展权限、审计、搜索和运营功能
