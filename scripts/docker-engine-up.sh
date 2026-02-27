#!/usr/bin/env bash
set -euo pipefail

if ! command -v docker >/dev/null 2>&1; then
  echo "docker not found. Please install Docker Engine (docker-ce + compose plugin)." >&2
  exit 1
fi

if [ ! -S /var/run/docker.sock ]; then
  echo "Docker Engine socket /var/run/docker.sock not found. Start dockerd first." >&2
  exit 1
fi

if ! docker version >/dev/null 2>&1; then
  echo "Cannot connect to Docker daemon. Ensure your user can access /var/run/docker.sock." >&2
  exit 1
fi

# Enforce Docker Engine socket context (non-Desktop)
export DOCKER_HOST="unix:///var/run/docker.sock"

echo "Starting services with Docker Engine via $DOCKER_HOST"
exec docker compose up --build "$@"
