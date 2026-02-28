#!/usr/bin/env bash
set -euo pipefail

if ! command -v docker >/dev/null 2>&1; then
  echo "docker command not found" >&2
  exit 1
fi

if [ ! -f "$HOME/.docker/config.json" ]; then
  exec docker compose up --build "$@"
fi

if command -v docker-credential-desktop.exe >/dev/null 2>&1 || command -v docker-credential-desktop >/dev/null 2>&1; then
  exec docker compose up --build "$@"
fi

TMP_DOCKER_CONFIG="$(mktemp -d)"
cleanup() { rm -rf "$TMP_DOCKER_CONFIG"; }
trap cleanup EXIT

python - <<'PY' "$HOME/.docker/config.json" "$TMP_DOCKER_CONFIG/config.json"
import json,sys
src,dst=sys.argv[1],sys.argv[2]
with open(src,'r',encoding='utf-8') as f:
    cfg=json.load(f)
cfg.pop('credsStore',None)
for key in ('credHelpers',):
    if key in cfg and isinstance(cfg[key],dict):
        cfg[key]={k:v for k,v in cfg[key].items() if 'desktop' not in str(v).lower()}
with open(dst,'w',encoding='utf-8') as f:
    json.dump(cfg,f,indent=2)
PY

echo "Using temporary Docker config without desktop credential helper: $TMP_DOCKER_CONFIG"
DOCKER_CONFIG="$TMP_DOCKER_CONFIG" docker compose up --build "$@"
