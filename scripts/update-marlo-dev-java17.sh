#!/usr/bin/env bash
# Updates marlo-dev.properties to HTTP on port 8080 (for Java 17 / run-marlo-java17.sh).
# Replaces https://localhost:8443 with localhost:8080 so resources load over HTTP.
# Usage: ./scripts/update-marlo-dev-java17.sh   (from repo root)
#    or: ./scripts/update-marlo-dev-java17.sh /path/to/marlo-dev.properties

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

# https://localhost:8443 -> localhost:8080 (Java 17: HTTP on 8080)
TMP_FILE="${PROPS_ABS}.tmp.$$"
sed -e 's|https://localhost:8443|localhost:8080|g' \
    "$PROPS_ABS" > "$TMP_FILE"
mv "$TMP_FILE" "$PROPS_ABS"

echo "Updated: $PROPS_ABS (HTTPS/8443 -> HTTP/8080 for localhost)."
