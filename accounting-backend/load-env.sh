#!/usr/bin/env bash
# Load .env.local into the current shell (use: source ./load-env.sh)
ENV_FILE="$(dirname "$0")/.env.local"
if [ ! -f "$ENV_FILE" ]; then
  echo ".env.local not found; copy .env.local.sample to .env.local and edit it with your values."
  return 1
fi
set -o allexport
# shellcheck disable=SC1090
. "$ENV_FILE"
set +o allexport
echo "Loaded env from $ENV_FILE"
