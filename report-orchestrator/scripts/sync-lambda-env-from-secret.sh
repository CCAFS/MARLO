#!/usr/bin/env bash
set -euo pipefail

: "${LAMBDA_FUNCTION_NAME:?LAMBDA_FUNCTION_NAME is required}"
: "${AWS_SECRET_NAME:?AWS_SECRET_NAME is required}"
: "${AWS_REGION:=us-east-1}"

if ! command -v jq >/dev/null 2>&1; then
  echo "ERROR: jq is required to map Secrets Manager JSON to Lambda environment variables"
  exit 1
fi

echo "Fetching secret: ${AWS_SECRET_NAME}"
SECRET_JSON=$(aws secretsmanager get-secret-value \
  --secret-id "${AWS_SECRET_NAME}" \
  --region "${AWS_REGION}" \
  --query SecretString \
  --output text)

if [ -z "${SECRET_JSON}" ] || [ "${SECRET_JSON}" = "null" ]; then
  echo "ERROR: Secret ${AWS_SECRET_NAME} is empty"
  exit 1
fi

# Ensure all values are strings (Lambda requirement).
ENV_JSON=$(echo "${SECRET_JSON}" | jq -c '
  to_entries
  | map({key: .key, value: (.value | tostring)})
  | from_entries
  | {Variables: .}
')

echo "Updating Lambda environment for: ${LAMBDA_FUNCTION_NAME}"
aws lambda update-function-configuration \
  --function-name "${LAMBDA_FUNCTION_NAME}" \
  --environment "${ENV_JSON}" \
  --region "${AWS_REGION}"

echo "Waiting for Lambda configuration update..."
aws lambda wait function-updated \
  --function-name "${LAMBDA_FUNCTION_NAME}" \
  --region "${AWS_REGION}"

echo "Lambda environment synced from secret ${AWS_SECRET_NAME}"
