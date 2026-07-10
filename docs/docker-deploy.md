# CampusHub Docker Compose 部署说明

## 架构说明

`docker-compose.yml` 会在同一个默认网络中启动完整本地环境：

- 浏览器访问 `campus-web`，宿主机端口为 `3000`。
- 前端通过 `NEXT_PUBLIC_API_BASE` 调用 Gateway，默认是 `http://localhost:8080`。
- Gateway 暴露宿主机端口 `8080`，通过 Nacos 服务发现转发到各后端微服务。
- 后端微服务只暴露容器内端口，通过容器网络和 Nacos 互相发现。
- MySQL、Redis、RabbitMQ、Nacos、MinIO 使用 Docker volume 持久化数据。

## 容器职责

| 容器 | 作用 | 端口 |
| --- | --- | --- |
| `mysql` | 业务数据库，首次启动执行 `sql/init.sql` | 容器内 `3306`，宿主机默认 `3307:3306` |
| `redis` | 验证码、缓存、分布式锁、Gateway 限流 | `6379:6379` |
| `rabbitmq` | mock 短信、AI 解析、订单、支付和通知消息 | `5672:5672`, `15672:15672` |
| `nacos` | 服务注册发现和可选配置中心 | `8848:8848`, `9848:9848`, `9849:9849` |
| `minio` | 文件对象存储 | `9000:9000`, `9001:9001` |
| `campus-gateway` | 统一入口、路由、JWT 校验、限流 | `8080:8080` |
| `campus-auth-service` | 验证码登录、JWT 签发、登出 | 容器内 `8101` |
| `campus-user-service` | 用户资料、积分 | 容器内 `8102` |
| `campus-file-service` | 文件上传、MinIO bucket 初始化、文件记录 | 容器内 `8103` |
| `campus-ai-service` | mock OCR/AI 解析和 AI 任务 | 容器内 `8104` |
| `campus-trade-service` | 二手商品发布、列表、详情、状态流转 | 容器内 `8105` |
| `campus-order-service` | 订单创建、取消、超时关闭、支付成功消费 | 容器内 `8106` |
| `campus-pay-service` | mock 支付创建和 mock 支付成功 | 容器内 `8107` |
| `campus-notice-service` | mock 短信日志、站内信 | 容器内 `8108` |
| `campus-admin-service` | 后台查询和统计接口 | 容器内 `8109` |
| `campus-web` | Next.js 前端 | `3000:3000` |

## 启动顺序

1. `mysql`、`redis`、`rabbitmq`、`nacos`、`minio` 先启动并通过 healthcheck。
2. 业务微服务通过环境变量连接中间件，例如 `MYSQL_HOST=mysql`、`NACOS_ADDR=nacos:8848`。
3. `campus-file-service` 等待 MinIO 健康后启动，并自动创建 `campus-files` bucket。
4. `campus-gateway` 等待 Nacos、Redis 和业务服务启动后启动。
5. `campus-web` 等待 Gateway 容器启动后启动。

## 常用命令

构建并启动全部服务：

```bash
docker compose up -d --build
```

查看容器状态：

```bash
docker compose ps
```

查看某个服务日志：

```bash
docker compose logs -f campus-gateway
docker compose logs -f campus-auth-service
```

重新构建某个服务：

```bash
docker compose build campus-auth-service
docker compose up -d campus-auth-service
```

停止服务但保留数据：

```bash
docker compose down
```

删除数据并重建：

```bash
docker compose down -v
docker compose up -d --build
```

## 如何判断启动成功

基础设施健康状态：

```bash
docker compose ps
```

Gateway 健康接口：

```bash
curl http://localhost:8080/actuator/health
```

前端首页：

```bash
open http://localhost:3000
```

核心接口 smoke test：

```bash
curl -X POST http://localhost:8080/auth/sms/send \
  -H 'Content-Type: application/json' \
  -d '{"phone":"13800000000"}'
```

如果返回 `code` 为 `0`，说明 Gateway、auth-service、Redis、RabbitMQ 和 notice-service 的主链路已打通。验证码内容可以查看 notice-service 日志：

```bash
docker compose logs -f campus-notice-service
```

## 环境变量

Compose 默认使用本地开发值。需要覆盖时可创建 `.env`：

```env
MYSQL_DATABASE=campus_hub
MYSQL_PASSWORD=root
MYSQL_HOST_PORT=3307
RABBITMQ_USER=guest
RABBITMQ_PASSWORD=guest
MINIO_ACCESS_KEY=minioadmin
MINIO_SECRET_KEY=minioadmin
MINIO_BUCKET=campus-files
JWT_SECRET=CampusHubSecretKeyForJwtHS256NeedsAtLeast32Bytes2026
JWT_EXPIRE_SECONDS=604800
NEXT_PUBLIC_API_BASE=http://localhost:8080
```

不要把生产密钥提交进仓库。非本地环境应替换 `JWT_SECRET`、数据库密码、RabbitMQ 密码和 MinIO 密钥。

## 常见问题

### Nacos 还没健康，业务服务没有启动

等待 `docker compose ps` 中 `campus-nacos` 显示 healthy。首次启动 Nacos 较慢，compose 已设置较长 `start_period`。

### Gateway 返回 503 或找不到服务

先确认业务服务已经注册到 Nacos：

```bash
docker compose logs -f campus-auth-service
docker compose logs -f campus-gateway
```

如果刚启动完成，等待几十秒再访问。Gateway 依赖 Nacos 服务发现，业务服务需要先完成注册。

### MySQL 表结构不一致

旧 volume 不会重新执行 `sql/init.sql`。需要重建数据时执行：

```bash
docker compose down -v
docker compose up -d --build
```

### MySQL 端口被占用

Compose 默认把 MySQL 映射到宿主机 `3307`，容器内服务仍然通过 `mysql:3306` 访问。如果需要改成其他宿主机端口，在 `.env` 中设置：

```env
MYSQL_HOST_PORT=3308
```

### 前端请求不到后端

浏览器端请求默认走 `http://localhost:8080`。如果 Gateway 端口被占用，修改 `.env` 中的 `NEXT_PUBLIC_API_BASE` 后重新构建前端：

```bash
docker compose build campus-web
docker compose up -d campus-web
```

### MinIO 上传失败

确认 MinIO healthy，并查看 file-service 日志：

```bash
docker compose ps minio
docker compose logs -f campus-file-service
```

`campus-file-service` 启动时会自动创建配置的 bucket。
