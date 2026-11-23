#!/bin/bash
# Parity run: production (Docker PhantomJS) vs local engine for every payload given.
# Usage: run.sh <out-dir> <payload.json>...
# Env: PDF_MODULE_TS   path to reports-microservice/src/domain/api/pdf/pdf.module.ts
#      PROD_MODULES    node_modules built on linux/amd64 with handlebars@4.7.8 + pdf-creator-node@2.3.5
#      REPO            MARLO checkout (marlo-web compiled: target/classes + test classpath)
#      IGNORE          regex of words to ignore (default: generation timestamps)
set -u
OUT=$1; shift
HERE=$(cd "$(dirname "$0")" && pwd)
mkdir -p "$OUT"
IGNORE=${IGNORE:-'^(20[0-9]{2}-[0-9]{2}-[0-9]{2}|[0-9]{1,2}:[0-9]{2}(:[0-9]{2})?|[0-9]{8}_[0-9]{4}.*|(Mon|Tues|Wednes|Thurs|Fri|Satur|Sun)day,|[A-Z][a-z]+ [0-9]{1,2}(st|nd|rd|th),)$'}
CP_FILE="$OUT/.cp"
if [ ! -s "$CP_FILE" ]; then
  (cd "$REPO" && mvn -q -o -pl marlo-web dependency:build-classpath -Dmdep.outputFile="$CP_FILE" -Dmdep.includeScope=test) || exit 2
fi
CP="$REPO/marlo-web/target/classes:$(cat "$CP_FILE")"
status=0
for payload in "$@"; do
  payload=$(cd "$(dirname "$payload")" && pwd)/$(basename "$payload")
  name=$(basename "$payload" .json)
  # Test fixtures carry "__TEMPLATE__": merge the production template from ../templates/<report>.html.
  if grep -q '"__TEMPLATE__"' "$payload"; then
    tpl="$(dirname "$payload")/../templates/${name%-payload}.html"
    python3 -I -c 'import json,sys; m=json.load(open(sys.argv[1])); m["data"]["templateData"]=open(sys.argv[2]).read(); json.dump(m,open(sys.argv[3],"w"))' \
      "$payload" "$tpl" "$OUT/$name.json.in" || { status=2; continue; }
    payload="$OUT/$name.json.in"
  fi
  docker run --rm --platform linux/amd64 -v "$HERE":/h:ro -v "$PROD_MODULES":/node_modules:ro -v "$(dirname "$PDF_MODULE_TS")":/ts:ro \
    -v "$(dirname "$payload")":/p:ro -v "$OUT":/o -e NODE_MODULES_DIR=/node_modules node:23 \
    node /h/prod-render.js /ts/pdf.module.ts "/p/$(basename "$payload")" "/o/$name-prod.pdf" || { status=2; continue; }
  (cd "$OUT" && java -cp "$CP" "$HERE/LocalRender.java" "$payload" "$OUT/$name-local.pdf") || { status=2; continue; }
  echo "== $name"
  python3 -I "$HERE/compare.py" "$OUT/$name-prod.pdf" "$OUT/$name-local.pdf" --ignore "$IGNORE" --json "$OUT/$name.json" || status=1
done
exit $status
