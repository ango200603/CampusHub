#!/usr/bin/env bash
set -Eeuo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
FRONTEND_DIR="$ROOT_DIR/frontend/campus-web"

require_command() {
  if ! command -v "$1" >/dev/null 2>&1; then
    echo "$1 is required but was not found in PATH." >&2
    exit 1
  fi
}

require_command java
require_command mvn
require_command node
require_command npm

cd "$ROOT_DIR"

echo "Checking backend build..."
mvn clean package -DskipTests

echo
echo "Checking frontend build..."
cd "$FRONTEND_DIR"

if [ -f package-lock.json ]; then
  npm ci
else
  npm install
fi

npm run build

echo
echo "CampusHub development check passed."
