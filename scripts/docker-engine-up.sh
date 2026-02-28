#!/usr/bin/env bash
set -euo pipefail

if ! command -v docker >/dev/null 2>&1; then
  echo "docker not found. Please install Docker Engine (docker-ce)." >&2
  exit 1
fi

if [ ! -S /var/run/docker.sock ]; then
  echo "Docker Engine socket /var/run/docker.sock not found. Start dockerd first." >&2
  exit 1
fi

# Enforce Docker Engine socket context (non-Desktop)
export DOCKER_HOST="unix:///var/run/docker.sock"

if ! docker version >/dev/null 2>&1; then
  echo "Cannot connect to Docker daemon. Ensure your user can access /var/run/docker.sock." >&2
  exit 1
fi

compose_cmd=()
if docker compose version >/dev/null 2>&1; then
  compose_cmd=(docker compose)
elif command -v docker-compose >/dev/null 2>&1; then
  compose_cmd=(docker-compose)
else
  echo "Neither 'docker compose' plugin nor 'docker-compose' binary is available." >&2
  echo "Install one of: docker-compose-plugin (recommended) or docker-compose." >&2
  exit 1
fi

echo "Starting services with Docker Engine via $DOCKER_HOST using: ${compose_cmd[*]}"
exec "${compose_cmd[@]}" up --build "$@"
