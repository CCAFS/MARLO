#!/usr/bin/env bash
# Verifies MARLO's specificity two-file rule mechanically instead of by eye.
#
# A specificity is correct only when three artifacts agree on one string:
#   1. marlo-web/.../config/APConstants.java   -- the constant's VALUE
#   2. marlo-data/.../config/APConstants.java  -- the same constant, same value
#   3. the Flyway migration that INSERTs it into `parameters`.`key`
#
# CLAUDE.md hard rule 4 requires 1 and 2; AGENTS.md requires the value to equal parameters.key.
# Nothing in the build checks either, so a typo in one of the three ships silently: the flag simply
# never resolves, and it looks like a rollout problem rather than a spelling problem.
#
# Usage:
#   ./scripts/verify-specificity-constants.sh CONSTANT_NAME [migration.sql]
#   ./scripts/verify-specificity-constants.sh COGNITO_AUTH_ACTIVE
#
# Exit 0 = the three agree. Exit 1 = mismatch, with the offending values printed.
# Written for CHG-COGNITO-AUTH-001-T02, whose "Fails when" clause requires this check to exist.

set -u

CONSTANT="${1:-}"
MIGRATION="${2:-}"
RENAMED=0

if [ -z "$CONSTANT" ]; then
  echo "usage: $0 CONSTANT_NAME [migration.sql]" >&2
  exit 2
fi

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
WEB_CONSTANTS="$REPO_ROOT/marlo-web/src/main/java/org/cgiar/ccafs/marlo/config/APConstants.java"
DATA_CONSTANTS="$REPO_ROOT/marlo-data/src/main/java/org/cgiar/ccafs/marlo/config/APConstants.java"

# Pulls the string literal assigned to the named constant. Tolerates the wrapped form
# (`NAME =\n    "value";`) that Checkstyle's 120-char limit forces on the longer keys.
value_of() {
  local file="$1"
  tr '\n' ' ' < "$file" \
    | grep -oE "String[[:space:]]+${CONSTANT}[[:space:]]*=[[:space:]]*\"[^\"]*\"" \
    | grep -oE '"[^"]*"' \
    | tr -d '"' \
    | head -1
}

WEB_VALUE="$(value_of "$WEB_CONSTANTS")"
DATA_VALUE="$(value_of "$DATA_CONSTANTS")"

status=0

if [ -z "$WEB_VALUE" ]; then
  echo "FAIL: $CONSTANT is not declared in marlo-web APConstants" >&2
  status=1
fi

if [ -z "$DATA_VALUE" ]; then
  echo "FAIL: $CONSTANT is not declared in marlo-data APConstants" >&2
  status=1
fi

if [ -n "$WEB_VALUE" ] && [ -n "$DATA_VALUE" ] && [ "$WEB_VALUE" != "$DATA_VALUE" ]; then
  echo "FAIL: the two APConstants disagree on $CONSTANT" >&2
  echo "  marlo-web  = '$WEB_VALUE'" >&2
  echo "  marlo-data = '$DATA_VALUE'" >&2
  status=1
fi

# Counts INSERTs into `parameters` whose `key` COLUMN -- not merely some column -- holds $2.
#
# Checking only that the string appears somewhere inside the VALUES tuple is not enough: a migration
# with a typo'd key whose *description* happens to contain the correct string would pass, which is
# precisely the "typo ships silently" failure this script exists to catch. So the key's position is
# derived from each statement's OWN column list (no assumption about the AGENTS.md column order),
# and values are read as successive single-quoted literals, which stays correct for descriptions
# that contain commas.
count_key_inserts() {
  local file="$1"
  local wanted="$2"
  # `--` comments are stripped BEFORE newlines are flattened, because they are line-scoped: a comment
  # containing a ";" would otherwise split a statement, and one containing "(" would be mistaken for the
  # column list. (Known limitation: a literal "--" inside a quoted SQL string would also be stripped.
  # No MARLO migration does that, and this is a lint, not a parser.)
  sed 's/--.*//' "$file" | tr '\n' ' ' | tr ';' '\n' | awk -v wanted="$wanted" -v q="'" '
    /INSERT[ \t]*INTO[ \t]*parameters/ {
      # Anchor at the INSERT keyword so nothing before it can be read as the column list.
      ip = index($0, "INSERT")
      stmt = substr($0, ip)
      open = index(stmt, "(")
      if (open == 0) { next }
      rest = substr(stmt, open + 1)
      closeParen = index(rest, ")")
      if (closeParen == 0) { next }
      cols = substr(rest, 1, closeParen - 1)
      gsub(/[`\t ]/, "", cols)
      n = split(cols, colarr, ",")
      idx = 0
      for (i = 1; i <= n; i++) { if (colarr[i] == "key") { idx = i } }
      if (idx == 0) { next }

      vp = index(stmt, "VALUES")
      if (vp == 0) { next }
      vals = substr(stmt, vp + 6)

      seen = 0
      found = ""
      while (match(vals, q "[^" q "]*" q)) {
        seen++
        if (seen == idx) { found = substr(vals, RSTART + 1, RLENGTH - 2); break }
        vals = substr(vals, RSTART + RLENGTH)
      }
      if (found == wanted) { hits++ }
    }
    END { print hits + 0 }'
}

# Locate the migration that inserts this key, unless one was named explicitly.
if [ -z "$MIGRATION" ] && [ -n "$WEB_VALUE" ]; then
  # This repository holds ~1,600 migrations, so the column-aware check is far too slow to run over all
  # of them. grep narrows the field to files that mention the string at all (cheap, and cannot miss a
  # real hit); the precise check then runs only on those.
  while IFS= read -r candidate; do
    [ -n "$candidate" ] || continue
    if [ "$(count_key_inserts "$candidate" "$WEB_VALUE")" -gt 0 ]; then
      MIGRATION="$candidate"
      break
    fi
  done <<EOF
$(grep -rl -- "'${WEB_VALUE}'" "$REPO_ROOT/marlo-web/src/main/resources/database/migrations/" 2>/dev/null)
EOF
fi

if [ -z "$MIGRATION" ] && [ -n "$WEB_VALUE" ]; then
  # Not every specificity key is born in an INSERT. Some arrive by rename -- e.g. ai_section_active is
  # set by `UPDATE parameters SET `key` = 'ai_section_active'` in
  # V2_6_0_20260826_1000__RenameUserIdeaSectionActiveParameter.sql. Treating that as a failure would make
  # this script cry wolf on valid keys, which is how a lint gets ignored.
  RENAME="$(grep -rlE "SET[[:space:]]+\`key\`[[:space:]]*=[[:space:]]*'${WEB_VALUE}'" \
    "$REPO_ROOT/marlo-web/src/main/resources/database/migrations/" 2>/dev/null | head -1)"
  if [ -n "$RENAME" ]; then
    echo "  migration  = $(basename "$RENAME") (key set by rename, not INSERT)"
    MIGRATION="$RENAME"
    RENAMED=1
  fi
fi

if [ -z "$MIGRATION" ]; then
  echo "FAIL: no migration INSERTs '$WEB_VALUE' into the parameters.key column, and none renames a key to it" >&2
  status=1
elif [ "${RENAMED:-0}" -eq 1 ]; then
  : # provenance already reported above
else
  MIGRATION_ROWS="$(count_key_inserts "$MIGRATION" "$WEB_VALUE")"
  if [ "${MIGRATION_ROWS:-0}" -eq 0 ]; then
    echo "FAIL: $(basename "$MIGRATION") does not INSERT '$WEB_VALUE' into the parameters.key column" >&2
    echo "  (the string may appear in another column -- description, default_value -- which does not count)" >&2
    status=1
  else
    echo "  migration  = $(basename "$MIGRATION") ($MIGRATION_ROWS row(s) with key='$WEB_VALUE')"
  fi
fi

if [ "$status" -eq 0 ]; then
  echo "OK: $CONSTANT = '$WEB_VALUE' agrees across marlo-web, marlo-data and the migration"
fi

exit "$status"
