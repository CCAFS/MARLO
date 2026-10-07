#!/usr/bin/env bash
# Run Checkstyle on MARLO files using the repo config, bypassing the broken
# maven-checkstyle-plugin. Optionally compares against the same files at HEAD so
# pre-existing violations are not attributed to the current change.
#
#   checkstyle.sh <file.java> [more.java ...]
#   checkstyle.sh --baseline <file.java> [more.java ...]
#
# --baseline also checks each file as it exists at HEAD and prints both counts,
# so only the delta matters.
set -euo pipefail

REPO_ROOT="$(git rev-parse --show-toplevel 2>/dev/null)" || true
if [ -z "$REPO_ROOT" ]; then
  echo "error: not inside a git checkout - run this from the MARLO repository" >&2
  exit 1
fi
CONFIG="$REPO_ROOT/configuration/marlo-checkstyle.xml"
CS_VERSION="8.18"
CACHE_DIR="${TMPDIR:-/tmp}/marlo-checkstyle"
CP_FILE="$CACHE_DIR/cp-$CS_VERSION.txt"

[ -f "$CONFIG" ] || { echo "error: $CONFIG not found (run from inside the MARLO checkout)" >&2; exit 1; }

# Resolve the full Checkstyle dependency tree once and cache it. Hand-picking jars
# fails: 8.18 needs antlr 2.7.7, commons-logging, Saxon-HE and guava's helper jars.
if [ ! -s "$CP_FILE" ]; then
  mkdir -p "$CACHE_DIR/pom"
  cat > "$CACHE_DIR/pom/pom.xml" <<POM
<project xmlns="http://maven.apache.org/POM/4.0.0">
  <modelVersion>4.0.0</modelVersion>
  <groupId>local</groupId><artifactId>marlo-checkstyle-cp</artifactId><version>1</version>
  <dependencies>
    <dependency>
      <groupId>com.puppycrawl.tools</groupId>
      <artifactId>checkstyle</artifactId>
      <version>$CS_VERSION</version>
    </dependency>
  </dependencies>
</project>
POM
  ( cd "$CACHE_DIR/pom" && mvn -q -o \
      org.apache.maven.plugins:maven-dependency-plugin:3.6.1:build-classpath \
      -Dmdep.outputFile="$CP_FILE" ) \
    || { echo "error: could not resolve the checkstyle $CS_VERSION classpath" >&2; exit 1; }
fi
CP="$(cat "$CP_FILE")"

run_checkstyle() { java -cp "$CP" com.puppycrawl.tools.checkstyle.Main -c "$CONFIG" "$@" 2>&1; }
count_violations() { grep -c '^\[\(WARN\|ERROR\)\]' || true; }

BASELINE=0
if [ "${1:-}" = "--baseline" ]; then BASELINE=1; shift; fi
[ $# -gt 0 ] || { echo "usage: checkstyle.sh [--baseline] <file.java> [...]" >&2; exit 1; }

echo "=== working tree ==="
WT_OUT="$(run_checkstyle "$@")"
echo "$WT_OUT"
WT_COUNT="$(printf '%s\n' "$WT_OUT" | count_violations)"

if [ "$BASELINE" = "1" ]; then
  HEAD_DIR="$(mktemp -d)"
  trap 'rm -rf "$HEAD_DIR"' EXIT
  HEAD_FILES=()
  for f in "$@"; do
    rel="$(git -C "$REPO_ROOT" ls-files --full-name -- "$f" | head -1)"
    [ -n "$rel" ] || continue
    dest="$HEAD_DIR/$rel"
    mkdir -p "$(dirname "$dest")"
    git -C "$REPO_ROOT" show "HEAD:$rel" > "$dest" 2>/dev/null && HEAD_FILES+=("$dest")
  done
  if [ ${#HEAD_FILES[@]} -gt 0 ]; then
    echo "=== same files at HEAD (baseline) ==="
    HEAD_COUNT="$(run_checkstyle "${HEAD_FILES[@]}" | count_violations)"
    echo "violations at HEAD: $HEAD_COUNT"
  else
    HEAD_COUNT=0
    echo "=== no tracked version at HEAD (new files); baseline is 0 ==="
  fi
  echo "--- violations: HEAD=$HEAD_COUNT  working tree=$WT_COUNT  delta=$((WT_COUNT - HEAD_COUNT))"
else
  echo "--- violations in working tree: $WT_COUNT"
fi
