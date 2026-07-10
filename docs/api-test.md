# CampusHub 接口链路测试说明

本文按一条完整业务链路组织接口测试：验证码登录、上传文件、AI 任务查询、发布商品、创建订单、创建支付单、模拟支付成功、查看订单状态、查看站内信。示例默认走 Gateway。

## 测试前准备

- Gateway 地址：`http://localhost:8080`
- 示例手机号：`13800138000`
- 登录后把返回的 `token` 保存为变量：`TOKEN`
- 后续请求统一携带：`Authorization: Bearer ${TOKEN}`
- 文件上传前在 MinIO 控制台创建 bucket：`campus-files`
- 验证码为本地 mock。发送后可在 Redis 里读取：

```bash
docker exec -it campus-redis redis-cli GET sms:code:13800138000
```

统一响应结构：

```json
{
  "code": 0,
  "message": "ok",
  "data": {},
  "timestamp": "2026-07-08T20:00:00"
}
```

## 1. 发送验证码

Method: `POST`

URL: `http://localhost:8080/auth/sms/send`

Headers:

```http
Content-Type: application/json
```

Body:

```json
{
  "phone": "13800138000"
}
```

预期响应：

```json
{
  "code": 0,
  "message": "ok",
  "data": null,
  "timestamp": "2026-07-08T20:00:00"
}
```

验证点：

- Redis 出现 `sms:code:13800138000`，过期时间约 5 分钟。
- RabbitMQ `sms.send.queue` 收到 mock 短信消息，notice-service 负责消费并记录日志。

## 2. 验证码登录

Method: `POST`

URL: `http://localhost:8080/auth/login/sms`

Headers:

```http
Content-Type: application/json
```

Body:

```json
{
  "phone": "13800138000",
  "code": "123456"
}
```

把 `code` 替换为 Redis 中实际验证码。

预期响应：

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 604800,
    "user": {
      "id": 1900000000000000001,
      "phone": "13800138000",
      "nickname": "校园用户8000",
      "avatarUrl": "",
      "points": 100,
      "status": 1,
      "createdAt": "2026-07-08T20:00:00"
    }
  },
  "timestamp": "2026-07-08T20:00:00"
}
```

验证点：

- 用户不存在时，auth-service 通过 OpenFeign 调用 user-service 自动创建用户。
- 后续所有登录态接口都用 `Authorization: Bearer ${TOKEN}`。

## 3. 上传文件

Method: `POST`

URL: `http://localhost:8080/files/upload`

Headers:

```http
Authorization: Bearer ${TOKEN}
Content-Type: multipart/form-data
```

Body:

```text
file=@/tmp/campus-note.pdf
```

curl 示例：

```bash
curl -X POST "http://localhost:8080/files/upload" \
  -H "Authorization: Bearer ${TOKEN}" \
  -F "file=@/tmp/campus-note.pdf"
```

预期响应：

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "id": 1900000000000000101,
    "originalName": "campus-note.pdf",
    "fileUrl": "http://localhost:9000/campus-files/1900000000000000001/uuid-campus-note.pdf",
    "fileType": "application/pdf",
    "fileSize": 24576,
    "status": "UPLOADED",
    "createdAt": "2026-07-08T20:05:00"
  },
  "timestamp": "2026-07-08T20:05:00"
}
```

验证点：

- `file_records` 新增一条文件记录。
- MinIO `campus-files` bucket 中出现对象。
- RabbitMQ `ai.parse.queue` 收到 `file.parse` 消息。

## 4. 查询 AI 任务

文件上传会投递解析消息，ai-service 消费后会自动创建 AI 任务。如果想手动创建一条任务，也可以调用 `POST /ai/tasks`。

### 4.1 手动创建 AI 任务

Method: `POST`

URL: `http://localhost:8080/ai/tasks`

Headers:

```http
Authorization: Bearer ${TOKEN}
Content-Type: application/json
```

Body:

```json
{
  "fileId": 1900000000000000101,
  "taskType": "OCR_SUMMARY"
}
```

预期响应：

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "id": 1900000000000000201,
    "fileId": 1900000000000000101,
    "taskType": "OCR_SUMMARY",
    "status": "PENDING",
    "resultText": null,
    "errorMessage": null,
    "createdAt": "2026-07-08T20:06:00",
    "updatedAt": "2026-07-08T20:06:00"
  },
  "timestamp": "2026-07-08T20:06:00"
}
```

### 4.2 查询我的 AI 任务

Method: `GET`

URL: `http://localhost:8080/ai/tasks/my`

Headers:

```http
Authorization: Bearer ${TOKEN}
```

Body: 无

预期响应：

```json
{
  "code": 0,
  "message": "ok",
  "data": [
    {
      "id": 1900000000000000201,
      "fileId": 1900000000000000101,
      "taskType": "OCR_SUMMARY",
      "status": "SUCCESS",
      "resultText": "{\"summary\":\"已完成 campus-note.pdf 的模拟 OCR 和 AI 摘要。\",\"keywords\":[\"校园\",\"资料\",\"待办\"],\"todos\":[\"复习摘要内容\",\"整理课程重点\",\"同步到个人任务清单\"]}",
      "errorMessage": null,
      "createdAt": "2026-07-08T20:06:00",
      "updatedAt": "2026-07-08T20:06:03"
    }
  ],
  "timestamp": "2026-07-08T20:06:05"
}
```

验证点：

- 初始状态可能是 `PENDING` 或 `PROCESSING`。
- 等待 1 到 3 秒后再次查询，状态应变为 `SUCCESS`。

## 5. 发布二手商品

Method: `POST`

URL: `http://localhost:8080/trades/items`

Headers:

```http
Authorization: Bearer ${TOKEN}
Content-Type: application/json
```

Body:

```json
{
  "title": "Java 21 实战笔记",
  "description": "课堂整理，适合复习 Spring Boot 和微服务。",
  "price": 29.90,
  "category": "BOOK",
  "coverUrl": "http://localhost:9000/campus-files/demo/java-note-cover.png"
}
```

预期响应：

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "id": 1900000000000000301,
    "sellerId": 1900000000000000001,
    "title": "Java 21 实战笔记",
    "description": "课堂整理，适合复习 Spring Boot 和微服务。",
    "price": 29.90,
    "category": "BOOK",
    "coverUrl": "http://localhost:9000/campus-files/demo/java-note-cover.png",
    "status": "AVAILABLE",
    "createdAt": "2026-07-08T20:10:00"
  },
  "timestamp": "2026-07-08T20:10:00"
}
```

验证点：

- `trade_items` 新增记录。
- 商品详情可通过 `GET /trades/items/{id}` 查询。

## 6. 创建订单

下单用户不能是商品卖家。建议再用另一个手机号登录，拿到买家 `BUYER_TOKEN` 后创建订单。

Method: `POST`

URL: `http://localhost:8080/orders`

Headers:

```http
Authorization: Bearer ${BUYER_TOKEN}
Content-Type: application/json
```

Body:

```json
{
  "itemId": 1900000000000000301,
  "orderType": "TRADE_ITEM"
}
```

预期响应：

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "id": 1900000000000000401,
    "orderNo": "CH202607082011009991234",
    "buyerId": 1900000000000000002,
    "sellerId": 1900000000000000001,
    "itemId": 1900000000000000301,
    "amount": 29.90,
    "orderType": "TRADE_ITEM",
    "status": "UNPAID",
    "createdAt": "2026-07-08T20:11:00"
  },
  "timestamp": "2026-07-08T20:11:00"
}
```

验证点：

- order-service 使用 Redis 锁 `lock:trade:item:{itemId}` 防止重复下单。
- trade-service 会把商品从 `AVAILABLE` 锁定为 `LOCKED`。
- order-service 会发送订单超时关闭消息。

## 7. 创建支付单

Method: `POST`

URL: `http://localhost:8080/pay/create`

Headers:

```http
Authorization: Bearer ${BUYER_TOKEN}
Content-Type: application/json
```

Body:

```json
{
  "orderNo": "CH202607082011009991234",
  "amount": 29.90,
  "payType": "MOCK_BALANCE"
}
```

预期响应：

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "orderNo": "CH202607082011009991234",
    "payNo": "PAY202607082012009991234",
    "amount": 29.90,
    "status": "WAITING",
    "createdAt": "2026-07-08T20:12:00"
  },
  "timestamp": "2026-07-08T20:12:00"
}
```

验证点：

- 重复调用同一个 `orderNo` 会返回已有支付单，不重复插入。
- `pay_records` 中状态为 `WAITING`。

## 8. 模拟支付成功

Method: `POST`

URL: `http://localhost:8080/pay/mock-success`

Headers:

```http
Authorization: Bearer ${BUYER_TOKEN}
Content-Type: application/json
```

Body:

```json
{
  "payNo": "PAY202607082012009991234",
  "orderNo": "CH202607082011009991234"
}
```

预期响应：

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "orderNo": "CH202607082011009991234",
    "payNo": "PAY202607082012009991234",
    "amount": 29.90,
    "status": "SUCCESS",
    "createdAt": "2026-07-08T20:12:00"
  },
  "timestamp": "2026-07-08T20:13:00"
}
```

验证点：

- pay-service 条件更新支付状态，成功后发送 `order.pay.success`。
- 重复模拟成功不会重复发送支付成功消息。
- order-service 消费支付成功消息后把订单改为 `PAID`，并调用 trade-service 标记商品 `SOLD`。

## 9. 查看订单状态

Method: `GET`

URL: `http://localhost:8080/orders/{orderId}`

Headers:

```http
Authorization: Bearer ${BUYER_TOKEN}
```

Body: 无

预期响应：

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "id": 1900000000000000401,
    "orderNo": "CH202607082011009991234",
    "buyerId": 1900000000000000002,
    "sellerId": 1900000000000000001,
    "itemId": 1900000000000000301,
    "amount": 29.90,
    "orderType": "TRADE_ITEM",
    "status": "PAID",
    "createdAt": "2026-07-08T20:11:00"
  },
  "timestamp": "2026-07-08T20:13:03"
}
```

如果刚支付完立刻查询，MQ 消费可能还没完成，可以等待 1 到 2 秒后重试。

## 10. 查看站内信

Method: `GET`

URL: `http://localhost:8080/notices/my`

Headers:

```http
Authorization: Bearer ${BUYER_TOKEN}
```

Body: 无

预期响应：

```json
{
  "code": 0,
  "message": "ok",
  "data": [
    {
      "id": 1900000000000000501,
      "title": "支付成功",
      "content": "订单 CH202607082011009991234 已支付成功",
      "readStatus": 0,
      "createdAt": "2026-07-08T20:13:02"
    }
  ],
  "timestamp": "2026-07-08T20:14:00"
}
```

标记已读：

Method: `PUT`

URL: `http://localhost:8080/notices/{noticeId}/read`

Headers:

```http
Authorization: Bearer ${BUYER_TOKEN}
```

Body: 无

预期响应：

```json
{
  "code": 0,
  "message": "ok",
  "data": null,
  "timestamp": "2026-07-08T20:14:20"
}
```

## 常见失败响应

验证码错误：

```json
{
  "code": 401,
  "message": "验证码错误或已过期",
  "data": null,
  "timestamp": "2026-07-08T20:00:00"
}
```

未登录：

```json
{
  "code": 401,
  "message": "未登录或登录已过期",
  "data": null,
  "timestamp": "2026-07-08T20:00:00"
}
```

资源不存在：

```json
{
  "code": 404,
  "message": "资源不存在",
  "data": null,
  "timestamp": "2026-07-08T20:00:00"
}
```

