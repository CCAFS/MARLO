#!/usr/bin/env bash
# Report every FTL reference to the CSS/JS assets changed in the working tree,
# with its cache-busting query param, so a stale one is easy to spot.
# Without the bump, browsers and the CDN keep serving the old asset and the
# change never reaches users even though the source file is correct.
#
#   cache-bust-check.sh                 # assets changed vs HEAD
#   cache-bust-check.sh <file> [...]    # specific assets
set -uo pipefail

REPO_ROOT="$(git rev-parse --show-toplevel 2>/dev/null)" || true
if [ -z "$REPO_ROOT" ]; then
  echo "error: not inside a git checkout - run this from the MARLO repository" >&2
  exit 1
fi
cd "$REPO_ROOT" || exit 1
TODAY="$(date +%Y%m%d)"

if [ $# -gt 0 ]; then
  ASSETS="$(printf '%s\n' "$@")"
else
  ASSETS="$( { git diff --name-only HEAD -- '*.css' '*.js'
               git diff --cached --name-only HEAD -- '*.css' '*.js'; } | sort -u )"
fi

[ -n "$ASSETS" ] || { echo "no changed .css/.js files - nothing to bump"; exit 0; }

STALE=0
for asset in $(printf '%s\n' "$ASSETS" | sort -u); do
  base="$(basename "$asset")"
  echo "=== $base"
  refs="$(grep -rn --include='*.ftl' -- "$base" marlo-web/src/main/webapp || true)"
  if [ -z "$refs" ]; then
    echo "  (no FTL references it - check for a JS/CSS import instead)"
    continue
  fi
  while IFS= read -r line; do
    param="$(printf '%s' "$line" | grep -oE "$base\?[A-Za-z0-9._-]+" | head -1 | sed "s|$base?||")"
    loc="${line%%:*}:$(printf '%s' "${line#*:}" | cut -d: -f1)"
    if [ -z "$param" ]; then
      echo "  STALE  $loc - no cache-busting param at all"
      STALE=1
    elif [ "$param" = "$TODAY" ] || [[ "$param" == "$TODAY"-* ]]; then
      echo "  ok     $loc - ?$param"
    else
      echo "  STALE  $loc - ?$param (today is $TODAY)"
      STALE=1
    fi
  done <<< "$refs"
done

if [ "$STALE" = "1" ]; then
  echo
  echo "Bump the STALE references to ?$TODAY (add -1, -2 ... for a second bump the same day)."
  echo "These FTLs often use CRLF endings: edit with sed on the line, not a perl -0 pattern on \\n."
fi
