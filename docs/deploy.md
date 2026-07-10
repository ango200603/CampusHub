# CampusHub 部署和启动

本文说明 CampusHub 的本地演示与开发启动方式。当前 `docker-compose.yml` 已完整编排基础设施、10 个后端应用和 Next.js 前端。

## 环境要求

- JDK 21
- Maven 3.9+
- Node.js 22+、npm
- Docker Desktop，或 Docker Engine + Docker Compose

常用宿主机端口：

| 服务 | 宿主机地址 | 容器内访问地址 |
| --- | --- | --- |
| MySQL | `localhost:3307` | `mysql:3306` |
| Redis | `localhost:6379` | `redis:6379` |
| RabbitMQ | `localhost:5672` | `rabbitmq:5672` |
| RabbitMQ Management | `http://localhost:15672` | `rabbitmq:15672` |
| Nacos | `http://localhost:8848/nacos` | `nacos:8848` |
| MinIO API | `http://localhost:9000` | `http://minio:9000` |
| MinIO Console | `http://localhost:9001` | `minio:9001` |
| Gateway | `http://localhost:8080` | `campus-gateway:8080` |
| Frontend | `http://localhost:3000` | `campus-web:3000` |

MySQL 默认映射为 `3307:3306`，避免与本机 MySQL 的 `3306` 冲突。可以在 `.env` 中通过 `MYSQL_HOST_PORT` 修改宿主机端口。

## 模式一：Docker Compose 完整启动

该模式适合快速演示和完整链路验收。Compose 会启动：

- MySQL、Redis、RabbitMQ、Nacos、MinIO
- `campus-gateway`
- auth、user、file、ai、trade、order、pay、notice、admin 服务
- `campus-web` 前端

构建并启动：

```bash
docker compose up -d --build
```

Compose 会先等待基础设施 healthcheck 通过，再启动依赖它们的后端服务，最后启动 Gateway 和前端。

查看状态：

```bash
docker compose ps
```

查看日志：

```bash
docker compose logs -f campus-gateway
docker compose logs -f campus-auth-service
docker compose logs -f campus-web
```

检查 Gateway：

```bash
curl http://localhost:8080/actuator/health
```

停止并保留 volume：

```bash
docker compose down
```

重新构建单个服务：

```bash
docker compose up -d --build campus-gateway
docker compose up -d --build campus-web
```

后端容器通过 Docker 服务名访问中间件，例如 `mysql:3306`、`redis:6379`、`rabbitmq:5672`、`nacos:8848` 和 `http://minio:9000`。这些地址已经由 Compose 环境变量注入，不需要写入 Dockerfile。

## 模式二：IDE 本地开发启动

该模式适合断点调试。只用 Compose 启动基础设施，Java 服务和前端由本机开发工具启动。

启动基础设施：

```bash
docker compose up -d mysql redis rabbitmq nacos minio
```

检查基础设施：

```bash
docker compose ps mysql redis rabbitmq nacos minio
```

本机进程不能直接使用 `mysql`、`redis` 等 Docker 服务名。IDE Run Configuration 建议设置：

```text
MYSQL_HOST=localhost
MYSQL_PORT=3307
MYSQL_DATABASE=campus_hub
MYSQL_USER=root
MYSQL_PASSWORD=root
REDIS_HOST=localhost
REDIS_PORT=6379
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
NACOS_ADDR=localhost:8848
MINIO_ENDPOINT=http://localhost:9000
JWT_SECRET=CampusHubSecretKeyForJwtHS256NeedsAtLeast32Bytes2026
```

Spring Boot 不会自动读取仓库根目录的 `.env`。使用 IDE 时应把需要的变量放入 Run Configuration；使用命令行时可以先导入 `.env`。

编译后端：

```bash
mvn clean package -DskipTests
```

建议按依赖顺序启动，Gateway 最后启动：

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

启动前端开发服务器：

```bash
cd frontend/campus-web
npm install
NEXT_PUBLIC_API_BASE=http://localhost:8080 npm run dev
```

## 数据初始化与重建

MySQL volume 第一次创建时会执行 `sql/init.sql`。如果本地旧 volume 与当前表结构不一致，并且确认数据可以删除：

```bash
docker compose down -v
docker compose up -d --build
```

`docker compose down -v` 会删除所有 Compose volume，包括 MySQL、Redis、RabbitMQ、Nacos 和 MinIO 数据。

## 常见问题

### Nacos 注册失败

- 运行 `docker compose ps nacos`，确认状态为 `healthy`。
- 使用 `docker compose logs -f nacos` 查看启动日志。
- Nacos 首次启动可能需要 30-60 秒，IDE 模式下应等待就绪后再启动 Java 服务。

### 文件上传失败

- 检查 `MINIO_ENDPOINT`、访问密钥和 bucket 名称。
- 默认 bucket 为 `campus-files`；缺失时可在 `http://localhost:9001` 创建。
- Compose 模式使用 `http://minio:9000`，IDE 模式使用 `http://localhost:9000`。

### 前端无法访问后端

- 确认 Gateway 健康检查返回 `UP`。
- 浏览器端的 `NEXT_PUBLIC_API_BASE` 应为宿主机可访问的 `http://localhost:8080`。
- 修改 `NEXT_PUBLIC_API_BASE` 后需要重新执行前端构建。

### 登录后返回 401

- 请求需要携带 `Authorization: Bearer <token>`。
- Gateway 与 auth-service 必须使用相同的 `JWT_SECRET`。
- 修改 `JWT_SECRET` 后，之前签发的 token 会失效。

## 上线前注意事项

- 示例账号和密码仅适用于本地环境，部署前应通过安全的密钥管理方式替换。
- MySQL 不应使用 root 作为生产应用账号，也不应直接暴露公网。
- RabbitMQ、Nacos、MinIO 和 Redis 应开启鉴权、访问控制、持久化与监控。
- 数据库结构变更应使用 Flyway、Liquibase 或独立迁移流程，而不是依赖容器首次启动脚本。
- 当前 AI 与支付均为 mock 实现，不应在文档或部署说明中描述为真实第三方服务。
