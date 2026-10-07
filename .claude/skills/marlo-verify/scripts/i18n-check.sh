#!/usr/bin/env bash
# Check the i18n lines added or changed in marlo-web .properties files for the two
# characters Struts getText mangles. MarloLocalizedTextProvider extends
# GlobalLocalizedTextProvider, which passes every value through java.text.MessageFormat,
# even when no arguments are given:
#   - a single ' opens a quoted section and is dropped: "can't" renders as "cant"
#   - a { that is not a {0}-style argument is parsed as a format element
#
# Only added lines are checked. The files already carry hundreds of undoubled
# apostrophes (global.properties alone had 65 on 2026-09-30); those are a separate
# finding, not something to fold into an unrelated change.
#
#   i18n-check.sh                 # .properties changed vs HEAD (staged, unstaged, untracked)
#   i18n-check.sh <file> [...]    # every value line of the given files
#
# FIX lines fail the gate (exit 1). REVIEW lines need a human look.
set -uo pipefail

REPO_ROOT="$(git rev-parse --show-toplevel 2>/dev/null)" || true
if [ -z "$REPO_ROOT" ]; then
  echo "error: not inside a git checkout - run this from the MARLO repository" >&2
  exit 1
fi
cd "$REPO_ROOT" || exit 1

RES="marlo-web/src/main/resources"

# Emit "<file>:<line>:<text>" for each line to inspect.
collect() {
  if [ $# -gt 0 ]; then
    for f in "$@"; do
      grep -n '' "$f" | sed "s|^|$f:|"
    done
    return
  fi
  local files
  files="$( { git diff --name-only HEAD -- "$RES/*.properties"
              git ls-files --others --exclude-standard -- "$RES/*.properties"; } \
            | grep -v "^$RES/config/" | sort -u )"
  for f in $files; do
    if git ls-files --error-unmatch "$f" >/dev/null 2>&1; then
      git diff -U0 HEAD -- "$f" | awk -v f="$f" '
        /^@@/ { split($3, a, ","); n = substr(a[1], 2) + 0; next }
        /^\+\+\+/ { next }
        /^\+/ { print f ":" n ":" substr($0, 2); n++ }'
    else
      grep -n '' "$f" | sed "s|^|$f:|"
    fi
  done
}

FAIL=0
FOUND=0
while IFS= read -r entry; do
  [ -n "$entry" ] || continue
  file="${entry%%:*}"; rest="${entry#*:}"
  lineno="${rest%%:*}"; text="${rest#*:}"
  # Skip comments and blank lines.
  case "$(printf '%s' "$text" | sed 's/^[[:space:]]*//')" in
    ''|'#'*|'!'*) continue ;;
  esac
  # The value is what follows the first '=' on a key line; a continuation line is all value.
  if printf '%s' "$text" | grep -qE '^[[:space:]]*[^[:space:]=:]+[[:space:]]*[=:]'; then
    value="$(printf '%s' "$text" | sed -E 's/^[[:space:]]*[^[:space:]=:]+[[:space:]]*[=:][[:space:]]*//')"
  else
    value="$text"
  fi
  # Properties loading turns \' into '; MessageFormat sees what remains.
  loaded="$(printf '%s' "$value" | sed "s/\\\\'/'/g")"
  FOUND=1
  # A single apostrophe: not part of a '' pair.
  if printf '%s' "$loaded" | sed "s/''//g" | grep -q "'"; then
    echo "FIX     $file:$lineno  single ' is dropped by MessageFormat - write '' (e.g. can''t)"
    echo "        $value"
    FAIL=1
  fi
  # A brace that is not a {n} or {n,type,...} argument.
  if printf '%s' "$loaded" | sed "s/'[^']*'//g" | sed -E 's/\{[0-9]+(,[^}]*)?\}//g' | grep -q '[{}]'; then
    echo "REVIEW  $file:$lineno  bare { or } is parsed as a format element - quote it as '{' or move it out of the key"
    echo "        $value"
  fi
done < <(collect "$@")

[ "$FOUND" = "1" ] || { echo "no changed i18n lines - nothing to check"; exit 0; }
[ "$FAIL" = "0" ] && echo "i18n check: no FIX findings"
exit "$FAIL"
