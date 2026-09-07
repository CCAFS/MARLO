#!/usr/bin/env bash
#
# Post-deploy smoke check for unresolved i18n keys.
#
# Struts renders a missing resource-bundle entry as the key name itself, so a deploy that shipped
# new keys in global.properties without restarting the application shows raw keys (for example
# "dashboard.schedule.title") to users. This script fetches a rendered page and fails if any known
# key name appears literally in the visible markup.
#
# See reports/ai-context/deployment-checklist.md (Rule 0).
#
# Usage:
#   scripts/post-deploy-smoke-i18n.sh <url-or-html-file> [key-prefix ...]
#
# Examples:
#   scripts/post-deploy-smoke-i18n.sh https://marlo.cgiar.org/dashboard.do
#   scripts/post-deploy-smoke-i18n.sh /tmp/dashboard.html dashboard. global.
#   SMOKE_COOKIE=/tmp/marlo-cookies.txt scripts/post-deploy-smoke-i18n.sh https://host/dashboard.do
#
# Environment:
#   SMOKE_COOKIE     path to a cookie jar, for pages behind login (the dashboard is)
#   SMOKE_CURL_OPTS  extra options passed to curl (for example -k on a staging certificate)
#
# Exit codes:
#   0  clean
#   1  raw i18n keys found in the page
#   2  usage or fetch error

set -uo pipefail

DEFAULT_PREFIX="dashboard."

usage() {
  echo "Usage: $(basename "$0") <url-or-html-file> [key-prefix ...]" >&2
  echo "       default key prefix: ${DEFAULT_PREFIX}" >&2
}

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"
PROPERTIES_FILE="${REPO_ROOT}/marlo-web/src/main/resources/global.properties"

TARGET="${1:-}"
if [ -z "${TARGET}" ]; then
  usage
  exit 2
fi
shift

PREFIXES=("$@")
if [ ${#PREFIXES[@]} -eq 0 ]; then
  PREFIXES=("${DEFAULT_PREFIX}")
fi

if [ ! -f "${PROPERTIES_FILE}" ]; then
  echo "ERROR: cannot find ${PROPERTIES_FILE}. Run this script from inside the MARLO checkout." >&2
  exit 2
fi

WORK_DIR="$(mktemp -d)"
trap 'rm -rf "${WORK_DIR}"' EXIT

KEYS_FILE="${WORK_DIR}/keys.txt"
PAGE_FILE="${WORK_DIR}/page.html"
TEXT_FILE="${WORK_DIR}/page.txt"
FOUND_FILE="${WORK_DIR}/found.txt"

# Collect the key names declared under the requested prefixes.
: > "${KEYS_FILE}"
for prefix in "${PREFIXES[@]}"; do
  escaped="$(printf '%s' "${prefix}" | sed 's/[.[\*^$]/\\&/g')"
  grep -E "^[[:space:]]*${escaped}[A-Za-z0-9_.-]*[[:space:]]*=" "${PROPERTIES_FILE}" \
    | sed -E 's/[[:space:]]*=.*$//; s/^[[:space:]]*//' >> "${KEYS_FILE}"
done
sort -u "${KEYS_FILE}" > "${KEYS_FILE}.sorted" && mv "${KEYS_FILE}.sorted" "${KEYS_FILE}"

KEY_COUNT="$(wc -l < "${KEYS_FILE}" | tr -d '[:space:]')"
if [ "${KEY_COUNT}" -eq 0 ]; then
  echo "ERROR: no keys in global.properties match the prefixes: ${PREFIXES[*]}" >&2
  exit 2
fi

# Fetch the page, or read it from disk when a local file was given.
if [ -f "${TARGET}" ]; then
  cp "${TARGET}" "${PAGE_FILE}"
else
  CURL_ARGS=(-sS -L --max-time 45 -o "${PAGE_FILE}")
  if [ -n "${SMOKE_COOKIE:-}" ]; then
    CURL_ARGS+=(-b "${SMOKE_COOKIE}" -c "${SMOKE_COOKIE}")
  fi
  # SMOKE_CURL_OPTS is intentionally word-split so callers can pass several options.
  # shellcheck disable=SC2086
  if ! curl "${CURL_ARGS[@]}" ${SMOKE_CURL_OPTS:-} "${TARGET}"; then
    echo "ERROR: could not fetch ${TARGET}" >&2
    exit 2
  fi
fi

if [ ! -s "${PAGE_FILE}" ]; then
  echo "ERROR: ${TARGET} returned an empty body." >&2
  exit 2
fi

if grep -qiE 'j_security_check|type="password"|name="password"' "${PAGE_FILE}"; then
  echo "WARNING: the page looks like a login screen, so the real screen was probably not checked." >&2
  echo "         Pass SMOKE_COOKIE=<cookie-jar>, or save the authenticated page and pass its path." >&2
fi

# Drop <script> and <style> blocks so inline JS cannot produce false positives.
if command -v perl >/dev/null 2>&1; then
  perl -0777 -pe 's{<script\b.*?</script\s*>}{ }gis; s{<style\b.*?</style\s*>}{ }gis;' \
    "${PAGE_FILE}" > "${TEXT_FILE}"
else
  cp "${PAGE_FILE}" "${TEXT_FILE}"
fi

grep -oFf "${KEYS_FILE}" "${TEXT_FILE}" 2>/dev/null | sort -u > "${FOUND_FILE}"

if [ -s "${FOUND_FILE}" ]; then
  echo "FAIL: unresolved i18n keys are rendering in ${TARGET}"
  echo
  sed 's/^/  - /' "${FOUND_FILE}"
  echo
  echo "Most likely cause: the deploy shipped new global.properties keys without restarting the"
  echo "application. Restart Tomcat (or reload the webapp context) and run this check again."
  echo "See reports/ai-context/deployment-checklist.md (Rule 0)."
  exit 1
fi

echo "OK: none of the ${KEY_COUNT} keys matching '${PREFIXES[*]}' render as raw text in ${TARGET}"
