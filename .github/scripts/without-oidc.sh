#!/usr/bin/env bash
# Runs a command with no AWS credential, no OIDC request variable and no runner token in its environment.
# Every step that executes repository or dependency code (npm ci, lint, tests, docker build) goes through
# it: a job with `id-token: write` exposes the OIDC request variables to all its steps, so without this any
# install script or test could request a token and assume the build role.
set -euo pipefail
exec env \
  -u ACTIONS_ID_TOKEN_REQUEST_TOKEN -u ACTIONS_ID_TOKEN_REQUEST_URL \
  -u ACTIONS_RUNTIME_TOKEN -u ACTIONS_CACHE_URL -u ACTIONS_RESULTS_URL \
  -u AWS_ACCESS_KEY_ID -u AWS_SECRET_ACCESS_KEY -u AWS_SESSION_TOKEN \
  "$@"
