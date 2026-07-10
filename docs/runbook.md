# CampusHub 运行手册

本文用于在本地启动 CampusHub、检查服务状态并完成基础冒烟测试。默认在仓库根目录执行命令。

## 完整 Docker 启动

构建镜像并启动 MySQL、Redis、RabbitMQ、Nacos、MinIO、全部后端微服务和前端：

```bash
docker compose up -d --build
```

查看容器状态：

```bash
docker compose ps
```

基础设施应显示为 `healthy`，后端服务和 `campus-web` 应显示为 `Up`。首次构建需要下载 Maven 和 npm 依赖，耗时会比后续构建长。

查看 Gateway 日志：

```bash
docker compose logs -f campus-gateway
```

检查 Gateway 健康状态：

```bash
curl http://localhost:8080/actuator/health
```

预期响应：

```json
{"status":"UP"}
```

常用访问地址：

| 服务 | 地址 |
| --- | --- |
| 前端 | `http://localhost:3000` |
| Gateway | `http://localhost:8080` |
| Nacos | `http://localhost:8848/nacos` |
| RabbitMQ Management | `http://localhost:15672` |
| MinIO Console | `http://localhost:9001` |
| MySQL | `localhost:3307` |

停止服务但保留数据：

```bash
docker compose down
```

## 验证码冒烟测试

通过 Gateway 发送本地 mock 验证码：

```bash
curl -X POST http://localhost:8080/auth/sms/send \
  -H 'Content-Type: application/json' \
  -d '{"phone":"13800138000"}'
```

接口应返回业务码 `0`。验证码不会调用真实短信服务，可从 Redis 读取：

```bash
docker exec campus-redis redis-cli GET sms:code:13800138000
```

验证码默认有过期时间，同一手机号短时间重复发送可能触发限流。

## IDE 本地开发模式

需要调试 Java 代码时，只启动基础设施：

```bash
docker compose up -d mysql redis rabbitmq nacos minio
```

IDE 中运行微服务时使用宿主机地址，而不是 Docker 服务名。建议在 Run Configuration 中设置：

```text
MYSQL_HOST=localhost
MYSQL_PORT=3307
REDIS_HOST=localhost
REDIS_PORT=6379
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
NACOS_ADDR=localhost:8848
MINIO_ENDPOINT=http://localhost:9000
NEXT_PUBLIC_API_BASE=http://localhost:8080
```

先编译后端：

```bash
mvn clean package -DskipTests
```

建议先启动业务服务，最后启动 `campus-gateway`。前端开发服务器单独启动：

```bash
cd frontend/campus-web
npm install
npm run dev
```

## 常见问题

### 端口被占用

使用 `docker compose ps` 和系统端口工具确认冲突进程。常见端口包括 `3000`、`8080`、`3307`、`6379`、`5672`、`15672`、`8848`、`9000` 和 `9001`。

MySQL 宿主机端口可通过 `.env` 调整：

```text
MYSQL_HOST_PORT=3308
```

修改后，IDE 中的 `MYSQL_PORT` 也要使用相同端口。容器内后端仍通过 `mysql:3306` 访问 MySQL。

### MySQL volume 中是旧数据

`sql/init.sql` 只会在 MySQL volume 第一次创建时执行。确认可以删除本地数据后，执行：

```bash
docker compose down -v
docker compose up -d --build
```

`-v` 会删除 MySQL、Redis、RabbitMQ、Nacos 和 MinIO 的本地持久化数据，不要用于需要保留数据的环境。

### Nacos 未就绪

Nacos 首次启动通常较慢。检查状态和日志：

```bash
docker compose ps nacos
docker compose logs -f nacos
```

等待 Nacos 变为 `healthy` 后再检查业务服务。Compose 会依据 healthcheck 安排后端启动顺序。

### MinIO bucket 不存在

文件服务默认使用 `campus-files` bucket。上传失败时先检查 MinIO 状态和文件服务日志：

```bash
docker compose ps minio
docker compose logs -f campus-file-service
```

必要时打开 `http://localhost:9001`，使用本地配置的 MinIO 账号创建 `campus-files` bucket。

### 前端请求地址错误

`NEXT_PUBLIC_API_BASE` 在 Next.js 构建时写入前端产物。默认值为 `http://localhost:8080`。修改该值后需要重新构建前端镜像：

```bash
NEXT_PUBLIC_API_BASE=http://localhost:8080 docker compose up -d --build campus-web
```

浏览器访问前端时应使用宿主机可访问的 Gateway 地址，不能使用仅容器内部可解析的 `http://campus-gateway:8080`。
