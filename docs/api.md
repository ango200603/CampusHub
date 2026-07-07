# API 说明

默认从 Gateway 访问：`http://localhost:8080`。

除 `POST /auth/sms/send`、`POST /auth/login/sms`、`/actuator/**` 外，请求都要带登录态：

```http
Authorization: Bearer <token>
```

Gateway 校验 JWT 后会把 `X-User-Id`、`X-User-Phone` 透传给下游服务，前端不用自己传。

## 返回格式

```json
{
  "code": 0,
  "message": "ok",
  "data": {},
  "timestamp": "2026-07-06T16:30:00"
}
```

常见错误码：

| code | 含义 | 例子 |
| --- | --- | --- |
| 400 | 参数不对 | 参数校验失败、验证码错误、买自己的商品 |
| 401 | 未登录或 token 无效 | 缺少 Bearer token、token 过期 |
| 403 | 没权限 | 看别人的订单、改别人的商品 |
| 404 | 资源不存在 | 用户、文件、任务、商品、订单、支付单、通知不存在 |
| 409 | 状态冲突 | 商品已锁定、订单不能取消、积分不足、资源被锁 |
| 429 | 频率太高 | 短信限流、网关限流 |
| 500 | 服务异常 | 下游不可用、MinIO 上传失败、未处理异常 |

业务异常大多会返回 JSON 业务码。调用方别只看 HTTP 状态，也要看 `code`。

## 通用对象

用户：

```json
{
  "id": 1900000000000000001,
  "phone": "13800138000",
  "nickname": "校园用户8000",
  "avatarUrl": "",
  "points": 100,
  "status": 1,
  "createdAt": "2026-07-06T16:30:00"
}
```

文件：

```json
{
  "id": 1900000000000000002,
  "originalName": "course.pdf",
  "fileUrl": "http://localhost:9000/campus-files/1900000000000000001/uuid-course.pdf",
  "fileType": "application/pdf",
  "fileSize": 204800,
  "status": "UPLOADED",
  "createdAt": "2026-07-06T16:30:00"
}
```

AI 任务：

```json
{
  "id": 1900000000000000003,
  "fileId": 1900000000000000002,
  "taskType": "OCR_SUMMARY",
  "status": "PENDING",
  "resultText": null,
  "errorMessage": null,
  "createdAt": "2026-07-06T16:30:00",
  "updatedAt": "2026-07-06T16:30:00"
}
```

商品：

```json
{
  "id": 1900000000000000004,
  "sellerId": 1900000000000000001,
  "title": "二手教材",
  "description": "高数教材，九成新",
  "price": 25.50,
  "category": "book",
  "coverUrl": "https://example.com/book.png",
  "status": "AVAILABLE",
  "createdAt": "2026-07-06T16:30:00"
}
```

订单：

```json
{
  "id": 1900000000000000005,
  "orderNo": "O202607061630001234",
  "buyerId": 1900000000000000006,
  "sellerId": 1900000000000000001,
  "itemId": 1900000000000000004,
  "amount": 25.50,
  "orderType": "TRADE_ITEM",
  "status": "UNPAID",
  "createdAt": "2026-07-06T16:30:00"
}
```

支付单：

```json
{
  "orderNo": "O202607061630001234",
  "payNo": "P202607061631001234",
  "amount": 25.50,
  "status": "WAITING",
  "createdAt": "2026-07-06T16:31:00"
}
```

通知：

```json
{
  "id": 1900000000000000007,
  "title": "支付成功",
  "content": "订单 O202607061630001234 已支付成功",
  "readStatus": 0,
  "createdAt": "2026-07-06T16:31:00"
}
```

## 认证

### POST `/auth/sms/send`

发登录验证码。验证码进 Redis：`sms:code:{phone}`，短信消息由 notice-service 打印。

入参：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| phone | string | 是 | 大陆手机号，`^1[3-9]\d{9}$` |

```json
{
  "phone": "13800138000"
}
```

返回：`data = null`。

常见错误：`400` 手机号格式不对；`429` 60 秒内重复发送。

### POST `/auth/login/sms`

验证码登录。用户不存在时自动注册，成功后返回 JWT。

入参：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| phone | string | 是 | 大陆手机号 |
| code | string | 是 | 6 位数字验证码 |

返回：

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
      "createdAt": "2026-07-06T16:30:00"
    }
  },
  "timestamp": "2026-07-06T16:30:00"
}
```

常见错误：`400` 验证码错误或过期；`500` user-service 不可用。

### POST `/auth/logout`

退出登录，删除 `login:token:{userId}`。

入参：无。返回：`data = null`。常见错误：`401`。

### GET `/auth/me`

解析当前 token。

返回：

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "userId": 1900000000000000001,
    "phone": "13800138000",
    "roles": ["USER"]
  },
  "timestamp": "2026-07-06T16:30:00"
}
```

常见错误：`401`。

## 用户

### GET `/users/me`

当前用户资料。返回用户对象。

常见错误：`401`；`404` 用户不存在。

### PUT `/users/me`

更新当前用户资料。

入参：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| nickname | string | 否 | 最长 64；空白字符串不会更新 |
| avatarUrl | string | 否 | 最长 255；可传空字符串清空 |

```json
{
  "nickname": "张三",
  "avatarUrl": "https://example.com/avatar.png"
}
```

返回更新后的用户对象。

常见错误：`400`；`401`；`404`。

### GET `/users/{id}`

按 ID 查用户。返回用户对象。

路径参数：`id` 用户 ID。

常见错误：`401`；`404`。

### GET `/users/points/me`

当前用户积分。

返回：

```json
{
  "code": 0,
  "message": "ok",
  "data": 100,
  "timestamp": "2026-07-06T16:30:00"
}
```

常见错误：`401`；`404`。

### POST `/users/points/increase`

增加用户积分。当前实现还没有管理员角色校验，别直接开放给普通用户。

入参：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| userId | number | 是 | 用户 ID |
| amount | number | 是 | 正整数 |

返回：`data = null`。

常见错误：`400`；`401`；`404`；`409`。

### POST `/users/points/decrease`

扣减用户积分。入参同 `/users/points/increase`。

返回：`data = null`。

常见错误：`400`；`401`；`409` 积分不足或用户不存在。

## 文件与 AI

### POST `/files/upload`

上传文件到 MinIO，写入 `file_records`，再投递 AI 解析消息。

入参：`multipart/form-data`

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| file | file | 是 | 文件不能为空 |

返回文件对象。

常见错误：`400` 文件为空；`401`；`500` MinIO 上传失败。

### GET `/files/{id}`

查自己的文件详情。路径参数：`id` 文件 ID。

返回文件对象。

常见错误：`401`；`404` 文件不存在或不属于当前用户。

### GET `/files/my`

当前用户的文件列表。

返回：

```json
{
  "code": 0,
  "message": "ok",
  "data": [
    {
      "id": 1900000000000000002,
      "originalName": "course.pdf",
      "fileUrl": "http://localhost:9000/campus-files/1900000000000000001/uuid-course.pdf",
      "fileType": "application/pdf",
      "fileSize": 204800,
      "status": "UPLOADED",
      "createdAt": "2026-07-06T16:30:00"
    }
  ],
  "timestamp": "2026-07-06T16:30:00"
}
```

常见错误：`401`。

### DELETE `/files/{id}`

软删除自己的文件记录，状态改为 `DELETED`。路径参数：`id` 文件 ID。

返回：`data = null`。

常见错误：`401`；`404` 文件不存在或不属于当前用户。

### POST `/ai/tasks`

手动创建 AI 解析任务，并投递解析消息。

入参：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| fileId | number | 是 | 文件 ID |
| taskType | string | 否 | 默认 `OCR_SUMMARY` |

```json
{
  "fileId": 1900000000000000002,
  "taskType": "OCR_SUMMARY"
}
```

返回 AI 任务对象。

常见错误：`400` 缺少 `fileId`；`401`。

### GET `/ai/tasks/{id}`

查自己的 AI 任务。路径参数：`id` 任务 ID。

返回 AI 任务对象。成功后 `status = SUCCESS`，`resultText` 是模拟 OCR/摘要 JSON 字符串。

常见错误：`401`；`404` 任务不存在或不属于当前用户。

### GET `/ai/tasks/my`

当前用户的 AI 任务列表。

返回 AI 任务对象数组。

常见错误：`401`。

## 交易

### POST `/trades/items`

发布二手商品。

入参：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| title | string | 是 | 最长 100 |
| description | string | 否 | 商品描述 |
| price | number | 是 | 最小 `0.01` |
| category | string | 是 | 分类 |
| coverUrl | string | 否 | 封面 URL |

返回商品对象，初始状态 `AVAILABLE`。

常见错误：`400`；`401`。

### GET `/trades/items`

商品列表，只返回 `AVAILABLE`。

查询参数：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| category | string | 否 | 精确匹配分类 |
| keyword | string | 否 | 标题模糊查询 |

返回商品对象数组。

常见错误：`401`。

### GET `/trades/items/{id}`

商品详情。路径参数：`id` 商品 ID。

返回商品对象。

常见错误：`401`；`404` 商品不存在。

### PUT `/trades/items/{id}`

修改自己发布的商品。

入参：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| title | string | 否 | 最长 100；空白字符串不会更新 |
| description | string | 否 | 可传空字符串清空 |
| price | number | 否 | 最小 `0.01` |
| category | string | 否 | 空白字符串不会更新 |
| coverUrl | string | 否 | 可传空字符串清空 |

返回更新后的商品对象。

常见错误：`400`；`401`；`403` 不是发布者；`404`。

### DELETE `/trades/items/{id}`

下架自己发布的商品，状态改为 `OFF_SHELF`。

路径参数：`id` 商品 ID。返回：`data = null`。

常见错误：`401`；`403`；`404`。

## 订单

### POST `/orders`

创建二手商品订单。服务会用 Redis 锁住商品，避免重复购买。

入参：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| itemId | number | 是 | 商品 ID |
| orderType | string | 否 | 默认 `TRADE_ITEM` |

返回订单对象，初始状态 `UNPAID`。

常见错误：`400` 缺少商品 ID 或购买自己的商品；`401`；`409` 商品不可买或正在处理。

### GET `/orders/{id}`

订单详情。买家和卖家可看。

路径参数：`id` 订单 ID。返回订单对象。

常见错误：`401`；`403` 无权查看；`404`。

### GET `/orders/my`

当前用户作为买家或卖家的订单列表。

返回订单对象数组。

常见错误：`401`。

### POST `/orders/{id}/cancel`

取消自己的未支付订单，并释放商品。

路径参数：`id` 订单 ID。返回：`data = null`。

常见错误：`401`；`403` 不是买家；`404`；`409` 当前状态不能取消。

## 支付

### POST `/pay/create`

创建模拟支付单。同一个 `orderNo` 重复调用会返回已有支付单。

入参：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| orderNo | string | 是 | 订单号 |
| amount | number | 是 | 最小 `0.01` |

返回支付单对象。

常见错误：`400`；`401`。

### POST `/pay/mock-success`

模拟支付成功，并投递 `order.pay.success`。

入参：

| 字段 | 类型 | 必填 | 说明 |
| --- | --- | --- | --- |
| payNo | string | 否 | 优先按支付单号查 |
| orderNo | string | 否 | `payNo` 为空或没匹配时按订单号查 |

```json
{
  "payNo": "P202607061631001234"
}
```

返回支付单对象，成功后 `status = SUCCESS`。

常见错误：`401`；`404` 支付单不存在。

### GET `/pay/records/{orderNo}`

按订单号查支付记录。路径参数：`orderNo` 订单号。

返回支付单对象。

常见错误：`401`；`404` 支付记录不存在。

## 通知

### GET `/notices/my`

当前用户的站内信列表。`readStatus = 0` 表示未读，`1` 表示已读。

返回通知对象数组。

常见错误：`401`。

### PUT `/notices/{id}/read`

标记自己的通知为已读。

路径参数：`id` 通知 ID。返回：`data = null`。

常见错误：`401`；`404` 通知不存在或不属于当前用户。

## 后台

后台接口现在只依赖 Gateway 登录态，还没有管理员角色校验；正式开放前要补 RBAC。

### GET `/admin/users`

最近 100 个用户。

返回示例：

```json
{
  "code": 0,
  "message": "ok",
  "data": [
    {
      "id": 1900000000000000001,
      "phone": "13800138000",
      "nickname": "校园用户8000",
      "points": 100,
      "status": 1,
      "created_at": "2026-07-06T16:30:00"
    }
  ],
  "timestamp": "2026-07-06T16:30:00"
}
```

常见错误：`401`；`500`。

### GET `/admin/orders`

最近 100 个订单。字段：`id`、`order_no`、`buyer_id`、`seller_id`、`item_id`、`amount`、`status`、`created_at`。

常见错误：`401`；`500`。

### GET `/admin/files`

最近 100 个文件记录。字段：`id`、`user_id`、`original_name`、`file_type`、`file_size`、`status`、`created_at`。

常见错误：`401`；`500`。

### GET `/admin/ai/tasks`

最近 100 个 AI 任务。字段：`id`、`user_id`、`file_id`、`task_type`、`status`、`created_at`、`updated_at`。

常见错误：`401`；`500`。

### GET `/admin/stats`

后台统计。

返回示例：

```json
{
  "code": 0,
  "message": "ok",
  "data": {
    "metrics": {
      "users": 10,
      "files": 8,
      "aiTasks": 8,
      "tradeItems": 5,
      "orders": 3,
      "paidOrders": 2,
      "notices": 6
    }
  },
  "timestamp": "2026-07-06T16:30:00"
}
```

常见错误：`401`；`500`。

## 内部接口

这些接口给 Feign 或服务内部用，不给前端直接调：

- `POST /users/internal/login-user`：auth-service 登录时用手机号查或建用户
- `GET /trades/internal/items/{id}`：order-service 查商品
- `POST /trades/internal/items/{id}/lock-for-order`：order-service 锁商品
- `POST /trades/internal/items/{id}/release`：order-service 释放商品
- `POST /trades/internal/items/{id}/sold`：order-service 支付成功后标记售出
