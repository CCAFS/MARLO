#!/usr/bin/env bash
# Verify that MARLO actually compiles. An incremental `mvn compile` reports
# BUILD SUCCESS on code that does not compile, because Maven judges a file
# unchanged against its existing .class and never recompiles it. Wiping
# target/classes first is what makes the result meaningful.
#
#   clean-compile.sh              # marlo-data + marlo-web (the usual gate)
#   clean-compile.sh <modules>    # e.g. "marlo-data" for a data-only change
#
# `mvn clean` alone is not enough here: it can fail with "Failed to delete
# target/classes", which is why the rm -rf comes first.
set -uo pipefail

REPO_ROOT="$(git rev-parse --show-toplevel 2>/dev/null)" || true
if [ -z "$REPO_ROOT" ]; then
  echo "error: not inside a git checkout - run this from the MARLO repository" >&2
  exit 1
fi
cd "$REPO_ROOT" || exit 1
MODULES="${1:-marlo-data,marlo-web}"

echo "--- wiping compiled output"
rm -rf marlo-web/target/classes marlo-web/target/test-classes marlo-data/target/classes

LOG="${TMPDIR:-/tmp}/marlo-clean-compile-$$.log"
echo "--- mvn -o -pl $MODULES -am test-compile -DskipTests   (log: $LOG)"
mvn -o -pl "$MODULES" -am test-compile -DskipTests > "$LOG" 2>&1
STATUS=$?

echo "--- source files compiled per module"
grep -E 'Compiling [0-9]+ source file' "$LOG" || echo "(none reported - Maven skipped compilation)"

WEB_COUNT="$(grep -E 'Compiling [0-9]+ source files to .*marlo-web/target/classes' "$LOG" \
  | grep -oE 'Compiling [0-9]+' | grep -oE '[0-9]+' | head -1)"
WEB_COUNT="${WEB_COUNT:-0}"
case "$MODULES" in
  *marlo-web*)
    if [ "$WEB_COUNT" -lt 900 ]; then
      echo "!! WARNING: marlo-web compiled only $WEB_COUNT files. A full clean compile is"
      echo "!! ~1043 files (marlo-data is ~2403), so this run reused stale .class files and"
      echo "!! proves nothing about your change. Check the log before trusting it."
    fi
    ;;
esac

if [ $STATUS -eq 0 ]; then
  echo "=== BUILD SUCCESS ($MODULES, clean recompile)"
else
  echo "=== BUILD FAILURE - compilation errors:"
  grep -E '^\[ERROR\].*\.java' "$LOG" | head -40
  echo "(full log: $LOG)"
fi
exit $STATUS
