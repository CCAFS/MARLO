#!/usr/bin/env bash
# Updates marlo-dev.properties for Java 17 run (HTTP on port 8080).
# Ensures resource-related URLs are explicit and consistent.
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

set_or_append_property() {
  local key="$1"
  local value="$2"
  local file="$3"

  if grep -qE "^${key}=" "$file"; then
    sed -i.bak -E "s|^${key}=.*|${key}=${value}|" "$file"
    rm -f "${file}.bak"
  else
    printf "\n%s=%s\n" "$key" "$value" >> "$file"
  fi
}

# Java 17 local convention: HTTP on 8080
set_or_append_property "marlo.baseUrl" "http://localhost:8080/marlo-web/" "$PROPS_ABS"
set_or_append_property "file.downloads" "http://localhost:8080/marlo-web/data" "$PROPS_ABS"
set_or_append_property "clarisa.summariesPDF" "http://localhost:8080/marlo-web/" "$PROPS_ABS"

echo "Updated: $PROPS_ABS"
echo "  marlo.baseUrl=http://localhost:8080/marlo-web/"
echo "  file.downloads=http://localhost:8080/marlo-web/data"
echo "  clarisa.summariesPDF=http://localhost:8080/marlo-web/"
