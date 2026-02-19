#!/usr/bin/env bash
# Updates marlo-dev.properties to HTTPS on port 8443 (for Java 8 / run-marlo-java8.sh).
# Replaces http://localhost:8080 and localhost:8080 with https://localhost:8443.
# Usage: ./scripts/update-marlo-dev-java8.sh   (from repo root)
#    or: ./scripts/update-marlo-dev-java8.sh /path/to/marlo-dev.properties

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
PROPS_FILE="${1:-$REPO_ROOT/marlo-web/src/main/resources/config/marlo-dev.properties}"

if [ ! -f "$PROPS_FILE" ]; then
  echo "ERROR: File not found: $PROPS_FILE"
  exit 1
fi

# Resolve absolute path
PROPS_ABS="$(cd "$(dirname "$PROPS_FILE")" && pwd)/$(basename "$PROPS_FILE")"

# Java 8: HTTP/8080 -> HTTPS/8443
TMP_FILE="${PROPS_ABS}.tmp.$$"
sed -e 's|http://localhost:8080|https://localhost:8443|g' \
    -e 's|localhost:8080|https://localhost:8443|g' \
    "$PROPS_ABS" > "$TMP_FILE"
mv "$TMP_FILE" "$PROPS_ABS"

echo "Updated: $PROPS_ABS (HTTP/8080 -> HTTPS/8443 for localhost)."
