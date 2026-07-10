# CampusHub 架构说明

CampusHub 是一个本地微服务实战项目，重点展示 Spring Cloud 微服务拆分、Gateway 统一入口、OpenFeign 服务调用、Redis 缓存和锁、RabbitMQ 异步消息、MinIO 文件存储、Nacos 注册发现等后端能力。

## 系统架构图

```mermaid
flowchart LR
    U["浏览器或接口测试工具"] --> G["campus-gateway:8080"]
    G --> A["auth-service:8101"]
    G --> US["user-service:8102"]
    G --> F["file-service:8103"]
    G --> AI["ai-service:8104"]
    G --> T["trade-service:8105"]
    G --> O["order-service:8106"]
    G --> P["pay-service:8107"]
    G --> N["notice-service:8108"]
    G --> AD["admin-service:8109"]

    A -->|OpenFeign| US
    O -->|OpenFeign| T
    P -->|查询预留| O
    AD -->|OpenFeign| US
    AD -->|OpenFeign| N

    A --> R["Redis 7"]
    US --> R
    T --> R
    O --> R
    G --> R

    A --> MQ["RabbitMQ"]
    F --> MQ
    AI --> MQ
    O --> MQ
    P --> MQ
    N --> MQ

    F --> M["MinIO"]
    A --> DB["MySQL 8"]
    US --> DB
    F --> DB
    AI --> DB
    T --> DB
    O --> DB
    P --> DB
    N --> DB
    AD --> DB

    A --> NC["Nacos"]
    US --> NC
    F --> NC
    AI --> NC
    T --> NC
    O --> NC
    P --> NC
    N --> NC
    AD --> NC
    G --> NC
```

## 手机号验证码登录流程图

```mermaid
sequenceDiagram
    participant Client
    participant Gateway
    participant AuthService
    participant Redis
    participant RabbitMQ
    participant NoticeService
    participant UserService

    Client->>Gateway: POST /auth/sms/send
    Gateway->>AuthService: 转发免登录请求
    AuthService->>Redis: 写 sms:limit:{phone}
    AuthService->>Redis: 写 sms:code:{phone}
    AuthService->>RabbitMQ: 发送 sms.send 消息
    RabbitMQ->>NoticeService: 消费 mock 短信
    NoticeService-->>RabbitMQ: ack
    AuthService-->>Client: Result<Void>

    Client->>Gateway: POST /auth/login/sms
    Gateway->>AuthService: 转发免登录请求
    AuthService->>Redis: 校验验证码
    AuthService->>UserService: Feign getOrCreate(phone)
    UserService-->>AuthService: 用户信息
    AuthService-->>Client: JWT 和用户信息
```

## 文件上传与 AI 异步解析流程图

```mermaid
sequenceDiagram
    participant Client
    participant Gateway
    participant FileService
    participant MinIO
    participant MySQL
    participant RabbitMQ
    participant AiService

    Client->>Gateway: POST /files/upload
    Gateway->>FileService: 透传 X-User-Id
    FileService->>MinIO: 上传文件对象
    FileService->>MySQL: 保存 file_records
    FileService->>RabbitMQ: 发送 file.parse
    FileService-->>Client: 文件记录和 URL
    RabbitMQ->>AiService: 消费解析消息
    AiService->>MySQL: 创建 ai_tasks PENDING
    AiService->>MySQL: 更新 PROCESSING
    AiService->>MySQL: 写入 SUCCESS 和 resultText
    AiService-->>RabbitMQ: ack
```

## 二手交易下单支付流程图

```mermaid
sequenceDiagram
    participant Buyer
    participant Gateway
    participant TradeService
    participant OrderService
    participant Redis
    participant PayService
    participant RabbitMQ
    participant NoticeService

    Buyer->>Gateway: POST /trades/items
    Gateway->>TradeService: 发布商品
    TradeService-->>Buyer: AVAILABLE 商品

    Buyer->>Gateway: POST /orders
    Gateway->>OrderService: 创建订单
    OrderService->>Redis: 获取 lock:trade:item:{itemId}
    OrderService->>TradeService: Feign detail
    OrderService->>TradeService: Feign lockForOrder
    OrderService-->>Buyer: UNPAID 订单
    OrderService->>RabbitMQ: 发送订单超时消息

    Buyer->>Gateway: POST /pay/create
    Gateway->>PayService: 创建支付单
    PayService-->>Buyer: WAITING 支付单

    Buyer->>Gateway: POST /pay/mock-success
    Gateway->>PayService: 模拟支付成功
    PayService->>RabbitMQ: 发送 order.pay.success
    RabbitMQ->>OrderService: 支付成功消费
    OrderService->>TradeService: Feign sold
    OrderService->>RabbitMQ: 发送 notice.send
    RabbitMQ->>NoticeService: 创建站内信
```

## 基础组件作用

### Redis

- 保存短信验证码：`sms:code:{phone}`。
- 保存短信发送限流：`sms:limit:{phone}`。
- 缓存用户积分和商品详情。
- 提供分布式锁 key，例如 `lock:trade:item:{itemId}`、`lock:user:points:{userId}`。
- Gateway 使用 Redis 生成按分钟的限流 key。

### RabbitMQ

- `sms.send.queue`：mock 短信通知。
- `ai.parse.queue`：文件上传后异步解析。
- `order.timeout.queue`：订单超时关闭。
- `order.pay.success.queue`：支付成功推动订单状态变化。
- `notice.send.queue`：异步创建站内信。
- `dead.letter.queue`：记录消费失败消息。

### MinIO

- 保存用户上传的文件对象。
- file-service 返回对象 URL，并把文件元数据写入 MySQL。
- 本地启动后需要确保 `campus-files` bucket 存在。

### Nacos

- 作为本地注册中心，服务启动后注册自身实例。
- Gateway 和 OpenFeign 通过服务名访问下游服务。
- 当前配置文件本地也可运行，Nacos 配置导入使用 optional。

### Gateway

- 统一入口，默认端口 `8080`。
- 根据路径路由到各微服务。
- 对 JWT 做初步校验，并把 `X-User-Id`、`X-User-Phone` 传给下游。
- 处理 CORS、白名单和简单限流。

### OpenFeign

- auth-service 调 user-service 完成登录自动注册。
- order-service 调 trade-service 完成商品详情查询、锁定、释放和售出。
- admin-service 调 user-service、notice-service 做后台聚合能力预留。

