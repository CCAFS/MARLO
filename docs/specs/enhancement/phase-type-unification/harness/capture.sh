#!/bin/bash
# Rendered-HTML capture harness (phase-type-unification T07, NF-002). GET-only page views against the local app.
# Usage: capture.sh <out-dir>          capture every page in pages.txt into <out-dir>
#        RAW=1 capture.sh <out-dir>    same, WITHOUT normalization (used to prove what varies run-to-run)
# Env:   BASE (default http://localhost:8080/marlo-web), CREDS (default ~/.marlo-local-credentials)
# Credentials are read from CREDS (MARLO_LOCAL_USER / MARLO_LOCAL_PASSWORD), sent only to BASE, never written out.
set -u
OUT=${1:?usage: capture.sh <out-dir>}
HERE=$(cd "$(dirname "$0")" && pwd)
BASE=${BASE:-http://localhost:8080/marlo-web}
CREDS=${CREDS:-$HOME/.marlo-local-credentials}
case "$BASE" in http://localhost:*|http://127.0.0.1:*) ;; *) echo "refusing non-local BASE" >&2; exit 2;; esac
. "$CREDS"
mkdir -p "$OUT"; rm -f "$OUT"/*.html "$OUT"/_manifest.txt
TMP=$(mktemp -d); trap 'rm -rf "$TMP"' EXIT

# Normalization (see README.md for the justification of each rule). Nothing else is touched.
normalize() {
  if [ "${RAW:-0}" = 1 ]; then cat; return; fi
  perl -pe '
    s/;jsessionid=[0-9A-Za-z._-]+/;jsessionid=NORM/g;                       # N1 servlet session id in URLs
    s/(\.(?:css|js|png|jpg|jpeg|gif|svg|ico|woff2?|ttf))\?[0-9A-Za-z._-]*/$1?NORM/g;   # N2 cache-bust query on assets
    s/(name="(?:struts\.token|token|_csrf|csrfToken)"[^>]*value=")[^"]*/$1NORM/g;      # N3 CSRF token (value after name)
    s/(value=")[^"]*("[^>]*name="(?:struts\.token|token|_csrf|csrfToken)")/$1NORM$2/g; # N3 CSRF token (value before name)
    s/\bnonce="[^"]*"/nonce="NORM"/g;                                      # N5 per-request CSP nonce
  ' | perl -0777 -pe '
    # N4 order of the Global Unit switcher entries (a Set iterated in hash order; varies run-to-run): sort the blocks
    s{(<div class="guPanel__list">\n)(.*?\n)([ ]*</div>\n[ ]*</div>\n[ ]*</div>)}{
      my ($h,$b,$t)=($1,$2,$3);
      my @blk = split /(?=^[ ]*<a class="guItem)/m, $b;
      $h . join("", sort @blk) . $t }gse;
    # N8 duplicate alliance-lever rows (same lever twice in one innovation+phase in the local data): the page shows
    # whichever row the hash-ordered Set yields first, in the hidden id input and in the <p> echo below it
    s{(name="innovation\.allianceLevers\[\d+\]\.id" value=")\d+("[^>]*/>\s*<p>\s*)\d+}{${1}NORM${2}NORM}g;
  '
}

login() { # $1 crp -> cookie jar $TMP/jar.<crp>
  local jar=$TMP/jar.$1 body=$TMP/body
  curl -s -o /dev/null -c "$jar" -b "$jar" "$BASE/login.do"
  umask 077
  { printf 'user.email=%s&user.password=%s&crp=%s&user.agree=true&next=Log+in' \
      "$(printf %s "$MARLO_LOCAL_USER" | jq -sRr @uri)" "$(printf %s "$MARLO_LOCAL_PASSWORD" | jq -sRr @uri)" "$1"; } > "$body"
  curl -s -o /dev/null -c "$jar" -b "$jar" --data @"$body" "$BASE/login.do"
  rm -f "$body"
}

: > "$OUT/_manifest.txt"
LOGGED=" "
while IFS='|' read -r name crp path flags; do
  case "$name" in ''|'#'*) continue;; esac
  if [ "$crp" = "-" ]; then jar=$TMP/jar.anon; else
    case "$LOGGED" in *" $crp "*) ;; *) login "$crp"; LOGGED="$LOGGED$crp ";; esac
    jar=$TMP/jar.$crp
  fi
  code=$(curl -s -b "$jar" -c "$jar" -L -o "$TMP/page" -w '%{http_code}' "$BASE/$path")
  if [ "$flags" = sorted ] && [ "${RAW:-0}" != 1 ]; then # N6, see README: pages whose collection order is unstable
    normalize < "$TMP/page" | perl -pe 's/\[\d+\]/[N]/g; s/(\bid="[^"]*_)\d+"/${1}N"/g' | LC_ALL=C sort > "$OUT/$name.html"
  else
    normalize < "$TMP/page" > "$OUT/$name.html"
  fi
  printf '%s\t%s\t%s\t%s bytes\n' "$name" "$code" "$path" "$(wc -c < "$OUT/$name.html" | tr -d ' ')" >> "$OUT/_manifest.txt"
done < "$HERE/pages.txt"
echo "captured $(grep -c . "$OUT/_manifest.txt") pages into $OUT"
