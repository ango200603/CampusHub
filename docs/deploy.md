# 部署和启动

这份文档写给本地开发和演示环境。当前仓库的 `docker-compose.yml` 只拉起 MySQL、Redis、RabbitMQ、Nacos、MinIO，Java 服务和前端还是本机进程启动。

## 环境

- JDK 21
- Maven 3.9+
- Node.js 22+、npm
- Docker Desktop，或 Docker Engine + Docker Compose
- 本地端口别被占用：`3306`、`6379`、`5672`、`15672`、`8848`、`9848`、`9849`、`9000`、`9001`、`8080`、`8101-8109`、`3000`

## 本地跑起来

准备环境变量：

```bash
cp .env.example .env
set -a
source .env
set +a
```

Spring Boot 不会自己读取 `.env`。命令行启动前先 `source`；用 IDE 启动时，把这些变量填到 Run Configuration。

启动基础设施：

```bash
./scripts/start-infra.sh
```

第一次启动 MySQL 时会跑 `sql/init.sql`。如果之前留下了旧 volume，表结构对不上，直接清掉重建：

```bash
docker compose down -v
./scripts/start-infra.sh
```

后端编译：

```bash
mvn clean package -DskipTests
```

服务按这个顺序开，少走一些注册和依赖报错：

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

想快速确认当前机器能不能构建，跑：

```bash
./scripts/dev-check.sh
```

脚本会做两件事：后端 `mvn clean package -DskipTests`，前端 `npm ci` 或 `npm install` 后 `npm run build`。

## Compose 组件

| 组件 | 地址 | 本地默认账号 |
| --- | --- | --- |
| MySQL | `localhost:3306` | `root` / `root` |
| Redis | `localhost:6379` | 无密码 |
| RabbitMQ | AMQP `localhost:5672`，控制台 `http://localhost:15672` | `guest` / `guest` |
| Nacos | `http://localhost:8848/nacos` | 本地关闭鉴权 |
| MinIO | API `http://localhost:9000`，控制台 `http://localhost:9001` | `minioadmin` / `minioadmin` |

常用命令：

```bash
docker compose ps
docker compose logs -f mysql
docker compose logs -f rabbitmq
docker compose logs -f nacos
docker compose logs -f minio
docker compose down
docker compose down -v
```

MinIO 上传会用到 bucket `campus-files`。如果上传时报 bucket 不存在，进 MinIO Console 建一个同名 bucket；生产环境可以交给初始化任务或 IaC 处理。

## 常见问题

### Nacos 注册失败

- 先看 `docker compose ps`，确认 `8848` 已经起来。
- Nacos 第一次启动会慢一点，等 30-60 秒再重启 Java 服务。
- 项目用了 `optional:nacos:${spring.application.name}.yaml`，Nacos 里没有配置文件也能启动。

### MySQL 表不存在或字段不一致

- `sql/init.sql` 只在 MySQL volume 第一次创建时执行。
- 本地开发可以 `docker compose down -v` 后重来。
- 生产环境别靠容器入口脚本迁移表结构，用 Flyway、Liquibase 或单独迁移流程。

### 文件上传失败

- 检查 `MINIO_ENDPOINT`、`MINIO_ACCESS_KEY`、`MINIO_SECRET_KEY`、`MINIO_BUCKET`。
- 确认 bucket 已建好。
- 当前 `fileUrl` 是用 MinIO API 地址拼出来的；私有 bucket 不能直接被浏览器读，需要公开策略或预签名 URL。

### 登录后还是 401

- 前端请求要带 `Authorization: Bearer <token>`。
- Gateway 和 auth-service 的 `JWT_SECRET` 要一致。
- 换过 `JWT_SECRET` 后，旧 token 全部失效。

### RabbitMQ 有消息不消费

- 确认 RabbitMQ 和对应业务服务都在运行。
- 打开 `http://localhost:15672` 看 exchange、queue、unacked 和 dead-letter。
- AI、订单、通知消费者是手动 ack，失败消息可能会进死信队列。

### 前端构建失败

- 用 Node.js 22+。
- 有 `package-lock.json` 时优先 `npm ci`。
- `NEXT_PUBLIC_API_BASE` 指向 Gateway，例如 `http://localhost:8080`。

## 上线前检查

- 示例密码全部换掉：MySQL `root/root`、RabbitMQ `guest/guest`、MinIO `minioadmin/minioadmin`、Nacos 默认配置都只适合本地。
- `JWT_SECRET` 换成高熵密钥，通过环境变量或密钥管理注入；所有解析 JWT 的服务保持一致。
- Redis 如果开启密码、TLS 或集群，当前 `application.yml` 还要补对应配置，不能只改 `.env`。
- MySQL 不暴露公网；应用账号按最小权限建，不用 root。
- RabbitMQ 建独立 vhost 和账号，打开持久化队列、死信监控、TLS、容量告警。
- Nacos 开鉴权，用 namespace/group 隔离环境，控制台和注册端口都限制来源。
- MinIO 配强密钥、独立 bucket 策略、持久化、备份和 TLS；对象访问用预签名 URL 或受控 CDN。
- Sentinel Dashboard 只放内网；限流/熔断规则要有持久化方案。
- Gateway 现在只有登录态和简单限流。后台、积分变更这类接口上线前要补 RBAC、审计和风控。
- CI 现在只做打包和前端构建。正式发布还要补测试、镜像扫描和部署后的健康检查。
