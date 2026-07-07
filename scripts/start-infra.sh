#!/usr/bin/env bash
set -Eeuo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

if ! command -v docker >/dev/null 2>&1; then
  echo "docker is required but was not found in PATH." >&2
  exit 1
fi

if docker compose version >/dev/null 2>&1; then
  COMPOSE=(docker compose)
elif command -v docker-compose >/dev/null 2>&1; then
  COMPOSE=(docker-compose)
else
  echo "Docker Compose is required but was not found." >&2
  exit 1
fi

if [ -f .env ]; then
  set -a
  # shellcheck disable=SC1091
  source .env
  set +a
fi

echo "Starting CampusHub infrastructure..."
"${COMPOSE[@]}" up -d mysql redis rabbitmq nacos minio

echo
echo "Container status:"
"${COMPOSE[@]}" ps

echo
echo "Infrastructure endpoints:"
echo "- MySQL: localhost:${MYSQL_PORT:-3306}"
echo "- Redis: localhost:${REDIS_PORT:-6379}"
echo "- RabbitMQ: amqp://localhost:${RABBITMQ_PORT:-5672}, management http://localhost:${RABBITMQ_MANAGEMENT_PORT:-15672}"
echo "- Nacos: http://localhost:8848/nacos"
echo "- MinIO: ${MINIO_ENDPOINT:-http://localhost:9000}, console http://localhost:${MINIO_CONSOLE_PORT:-9001}"
echo
echo "If file upload fails, create the MinIO bucket '${MINIO_BUCKET:-campus-files}' in the MinIO console."
