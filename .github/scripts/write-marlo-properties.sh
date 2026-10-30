#!/usr/bin/env bash
# Writes the MARLO DEV application properties from Secrets Manager to the git-ignored file the build expects, as the
# legacy Jenkins job marlo-dev-docker does (the whole SecretString of dev/app/aiccra/marlo/properties to
# marlo-web/src/main/resources/config/marlo-dev.properties, before `docker build .`). The original Dockerfile and the
# way MARLO loads the file (classpath:config/marlo-${spring.profiles.active:dev}.properties) are unchanged.
#
# Adapted from the Reporting Tool DEV script write-frontend-config.sh (onecgiar_pr@ef5597273): same git-ignore guard,
# mode 0600, silent CLI errors and log masks; only the path and the mask parser (properties instead of TypeScript
# string literals) differ.
#
# The value goes from the AWS CLI straight to the file: it is never echoed and never a step or job output; only its
# values are read back to register log masks. The file is removed by the workflow's `if: always()` step
# (remove-marlo-properties.sh).
# Inputs (environment): SECRET_ID, AWS_REGION and the step-scoped temporary credentials.
set -euo pipefail
: "${SECRET_ID:?}" "${AWS_REGION:?}"

file="marlo-web/src/main/resources/config/marlo-dev.properties"

# Refuse to write anywhere git would track: the file must stay ignored.
git check-ignore -q "$file" \
  || { echo "::error::$file is not git-ignored (fail closed)"; exit 1; }

umask 077
if ! aws secretsmanager get-secret-value --region "$AWS_REGION" --secret-id "$SECRET_ID" \
       --query SecretString --output text > "$file" 2> /dev/null; then
  rm -f "$file"
  # The CLI error is not printed: it may quote request details. The id is a masked environment secret.
  echo "::error::could not read the MARLO properties secret (check the build role and the secret id)"
  exit 1
fi
if [ ! -s "$file" ]; then
  rm -f "$file"
  echo "::error::the MARLO properties secret is empty (fail closed)"
  exit 1
fi

# Defense in depth only (the protection is that nothing prints the file): register every property value of 8+
# characters as a log mask, so an accidental echo in a later step of this job shows `***`. Comments and blank lines
# are skipped; the key ends at the first `=` or `:`. Workflow commands are consumed by the runner and never shown.
masked=0
while IFS= read -r line || [ -n "$line" ]; do
  line="${line%$'\r'}"
  case "$line" in '' | '#'* | '!'*) continue ;; esac
  [[ "$line" =~ ^[^=:]*[=:][[:space:]]*(.*)$ ]] || continue
  value="${BASH_REMATCH[1]}"
  value="${value//'%'/'%25'}"
  [ "${#value}" -ge 8 ] || continue
  echo "::add-mask::${value}"
  masked=$((masked + 1))
done < "$file"

echo "MARLO properties written (1 file, mode 0600, ${masked} values masked)"
