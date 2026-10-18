#!/usr/bin/env bash
# CHG-PWD-SWITCH-001 -- T01 AC-1 / AC-14: the AddPasswordLoginSwitch migration applied twice on three database shapes.
# Checks seeded rows (types 1-4 that exist, boolean, default true, category 2), no custom_parameters row, idempotence,
# every other parameters / custom_parameters row unchanged (checksum), and the three descriptions against the fixed
# texts of requirements.md DA-002 and design.md section 3, BYTE FOR BYTE (shell comparison of the stored value).
# The expected texts are read from the two spec files, never from the migration. The only transformation applied to
# them is un-wrapping the Markdown source lines (strip each line's indentation, join with one space); the database
# value is compared as stored, with no normalization.
# Exit status: 1 when any check FAILs. Every database written to must contain "probe" in its name.
#
# Environment:
#   PROBE_ADMIN_MYSQL  command opening a mysql client (SQL on stdin or as arguments), no default database
#   PROBE_ADMIN_DUMP   same for mysqldump
#   PROBE_COPY_DB      throwaway copy that already has the two earlier OTP migrations applied (default aiccradb_pwd_probe)
#   PROBE_MIG          migration file to apply (default: the real one); used by the falsifiers with a scratch copy
set -uo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../../../../.." && pwd)"
MIGDIR="$ROOT/marlo-web/src/main/resources/database/migrations"
MIG="${PROBE_MIG:-$(ls "$MIGDIR"/V2_6_0_*__AddPasswordLoginSwitch.sql)}"
REQ="$ROOT/docs/specs/changes/password-login-switch/requirements.md"
DES="$ROOT/docs/specs/changes/password-login-switch/design.md"
M="${PROBE_ADMIN_MYSQL:?}"; D="${PROBE_ADMIN_DUMP:?}"; COPY="${PROBE_COPY_DB:-aiccradb_pwd_probe}"
A=pwd_probe_t01_a; B=pwd_probe_t01_b; C=pwd_probe_t01_c
for n in "$COPY" $A $B $C; do case "$n" in *probe*) ;; *) echo "refusing: database name $n must contain probe"; exit 2;; esac; done
q() { $M -N -r -e "$1" 2>&1 | grep -v 'Using a password'; }
FAILS=0
check() { if [ "$2" = "$3" ]; then echo "PASS  $1 ($3)"; else echo "FAIL  $1 (expected: $2, got: $3)"; FAILS=$((FAILS + 1)); fi; }
PW=system_password_login_active; SW=system_otp_login_active; EX=system_otp_excluded_email_domains

# expected texts, from the specs: <file> <kind> <key>
expected() { python3 -I - "$1" "$2" "$3" <<'PY'
import re, sys
path, kind, key = sys.argv[1:4]
lines = open(path, encoding="utf-8").read().split("\n")
start = None
for i, l in enumerate(lines):
    if kind == "da002" and "CHG-PWD-SWITCH-001-DA-002**" in l:
        start = i; first = l[l.index('verbatim: "') + len('verbatim: "'):]
    if kind == "fixed" and l.startswith("- `%s`: \"" % key):
        start = i; first = l[len("- `%s`: \"" % key):]
    if start is not None:
        break
if start is None:
    sys.exit(3)
parts = []
i = start; cur = first
while True:
    cur = cur.strip()
    if cur.endswith('"'):
        parts.append(cur[:-1]); break
    parts.append(cur); i += 1; cur = lines[i]
sys.stdout.write(" ".join(parts))
PY
}
T_PW="$(expected "$REQ" da002 x)"; T_SW="$(expected "$DES" fixed $SW)"; T_EX="$(expected "$DES" fixed $EX)"
check "DA-002 text found in requirements.md" "yes" "$([ -n "$T_PW" ] && echo yes || echo no)"
check "DA-003 text of $SW found in design.md" "yes" "$([ -n "$T_SW" ] && echo yes || echo no)"
check "DA-003 text of $EX found in design.md" "yes" "$([ -n "$T_EX" ] && echo yes || echo no)"
echo "expected lengths (bytes): $PW=$(printf %s "$T_PW" | wc -c | tr -d ' ') $SW=$(printf %s "$T_SW" | wc -c | tr -d ' ') $EX=$(printf %s "$T_EX" | wc -c | tr -d ' ')"
echo "migration file: ${MIG#$ROOT/}"
echo "invocation per run: \$PROBE_ADMIN_MYSQL <database> < \$MIGRATION   (databases: $A cloned from $COPY, $B, $C)"

rows() { q "SELECT COUNT(*) FROM $1.parameters WHERE \`key\`='$2'"; }
by_type() { q "SELECT IFNULL(GROUP_CONCAT(CONCAT(global_unit_type_id,'/',format,'/',IFNULL(default_value,'NULL'),'/',category) ORDER BY global_unit_type_id SEPARATOR ' '),'none') FROM $1.parameters WHERE \`key\`='$2'"; }
others_p() { q "SELECT CONCAT(COUNT(*),':',IFNULL(SUM(CRC32(CONCAT_WS('|',id,global_unit_type_id,\`key\`,\`description\`,format,default_value,category))),0)) FROM $1.parameters WHERE \`key\` NOT IN ('$PW','$SW','$EX')"; }
otp_nodesc() { q "SELECT CONCAT(COUNT(*),':',IFNULL(SUM(CRC32(CONCAT_WS('|',id,global_unit_type_id,\`key\`,format,default_value,category))),0)) FROM $1.parameters WHERE \`key\` IN ('$SW','$EX')"; }
cp_all() { q "SELECT CONCAT(COUNT(*),':',IFNULL(SUM(CRC32(CONCAT_WS('|',id,parameter_id,global_unit_id,value,is_active))),0)) FROM $1.custom_parameters"; }
whole() { q "SELECT IFNULL(SUM(CRC32(CONCAT_WS('|',id,global_unit_type_id,\`key\`,\`description\`,format,default_value,category))),0) FROM $1.parameters"; }
desc_check() { # <label> <db> <key> <expected> : exactly one distinct stored value, byte-equal to the expected text
  local n got; n="$(q "SELECT COUNT(DISTINCT BINARY \`description\`) FROM $2.parameters WHERE \`key\`='$3'")"
  got="$(q "SELECT \`description\` FROM $2.parameters WHERE \`key\`='$3' LIMIT 1")"
  check "$1: one distinct stored value" "1" "$n"
  if [ "$got" = "$4" ]; then echo "PASS  $1: stored text equals the spec text byte for byte"; else
    echo "FAIL  $1: stored text differs from the spec text"; echo "  spec  : $4"; echo "  stored: $got"; FAILS=$((FAILS + 1)); fi
}

clone() { q "DROP DATABASE IF EXISTS $1; CREATE DATABASE $1 CHARACTER SET latin1" >/dev/null
  ( echo "SET foreign_key_checks=0;"; $D "$COPY" parameters custom_parameters global_unit_types global_units 2>/dev/null ) | $M "$1" 2>&1 | grep -v 'Using a password'; }

echo; echo "== (a) $A: the four touched tables cloned from $COPY (real data); apply twice"
clone $A
check "(a) fixture: the copy already has the earlier OTP keys, none of the new key" "4/4/0" "$(rows $A $SW)/$(rows $A $EX)/$(rows $A $PW)"
T_BEFORE="$(q "SELECT GROUP_CONCAT(id ORDER BY id) FROM $A.global_unit_types")"; echo "unit types in the copy: $T_BEFORE"
OP="$(others_p $A)"; OO="$(otp_nodesc $A)"; OC="$(cp_all $A)"
$M "$A" < "$MIG" 2>&1 | grep -v 'Using a password'; echo "run 1 exit ${PIPESTATUS[0]}"
R1="$(rows $A $PW)/$(rows $A $SW)/$(rows $A $EX)"; W1="$(whole $A)"
$M "$A" < "$MIG" 2>&1 | grep -v 'Using a password'; echo "run 2 exit ${PIPESTATUS[0]}"
R2="$(rows $A $PW)/$(rows $A $SW)/$(rows $A $EX)"; W2="$(whole $A)"
check "(a) after run 1: new key / code switch / exclusion rows (types 1-4 only, type 5 not seeded)" "4/4/4" "$R1"
check "(a) after run 2: no row duplicated" "$R1" "$R2"
check "(a) whole parameters table identical after run 2 (idempotent)" "$W1" "$W2"
check "(a) new key: boolean, default true, category 2, types 1-4" "1/1/true/2 2/1/true/2 3/1/true/2 4/1/true/2" "$(by_type $A $PW)"
check "(a) no custom_parameters row for any of the three keys (every unit starts on)" "0" "$(q "SELECT COUNT(*) FROM $A.custom_parameters cp JOIN $A.parameters p ON p.id=cp.parameter_id WHERE p.\`key\` IN ('$PW','$SW','$EX')")"
check "(a) every other parameters row unchanged (checksum)" "$OP" "$(others_p $A)"
check "(a) the two OTP keys: every column but description unchanged (checksum)" "$OO" "$(otp_nodesc $A)"
check "(a) custom_parameters table unchanged (checksum)" "$OC" "$(cp_all $A)"
desc_check "(a) $PW (DA-002)" $A $PW "$T_PW"
desc_check "(a) $SW (DA-003)" $A $SW "$T_SW"
desc_check "(a) $EX (DA-003)" $A $EX "$T_EX"
check "(a) applied migrations V2_6_0_20261009_1025 and V2_6_0_20261015_1418 are untouched" "" "$(cd "$ROOT" && git status --porcelain -- marlo-web/src/main/resources/database/migrations/V2_6_0_20261009_1025__AddEmailOtpLogin.sql marlo-web/src/main/resources/database/migrations/V2_6_0_20261015_1418__ReplaceOtpAllowListWithSwitchAndExclusion.sql)"

echo; echo "== (b) $B: no Global Unit 45 or 47 (only seeded units, ids < 29); apply twice"
for DB in $B $C; do q "DROP DATABASE IF EXISTS $DB; CREATE DATABASE $DB CHARACTER SET latin1" >/dev/null
  ( echo "SET foreign_key_checks=0;"; $D --no-data "$COPY" parameters custom_parameters global_unit_types global_units 2>/dev/null ) | $M "$DB" 2>&1 | grep -v 'Using a password'; done
q "SET foreign_key_checks=0; INSERT INTO $B.global_unit_types SELECT * FROM $COPY.global_unit_types WHERE id IN (1,2,3,4)"
q "SET foreign_key_checks=0; INSERT INTO $B.global_units SELECT * FROM $COPY.global_units WHERE id < 29"
q "SET foreign_key_checks=0; INSERT INTO $B.parameters (global_unit_type_id,\`key\`,\`description\`,format,default_value,category) SELECT global_unit_type_id,\`key\`,\`description\`,format,default_value,category FROM $COPY.parameters WHERE \`key\` IN ('$SW','$EX') AND global_unit_type_id IN (1,2,3,4)"
check "(b) fixture: no unit 45 or 47" "0" "$(q "SELECT COUNT(*) FROM $B.global_units WHERE id IN (45,47)")"
check "(b) fixture: types 1-4 and the 8 OTP rows loaded (a vacuous run must not pass)" "4/8/0" "$(q "SELECT COUNT(*) FROM $B.global_unit_types")/$(q "SELECT COUNT(*) FROM $B.parameters")/$(rows $B $PW)"
$M "$B" < "$MIG" 2>&1 | grep -v 'Using a password'; echo "run 1 exit ${PIPESTATUS[0]}"; BR1="$(rows $B $PW)/$(rows $B $SW)/$(rows $B $EX)"; BW1="$(whole $B)"
$M "$B" < "$MIG" 2>&1 | grep -v 'Using a password'; echo "run 2 exit ${PIPESTATUS[0]}"; BR2="$(rows $B $PW)/$(rows $B $SW)/$(rows $B $EX)"; BW2="$(whole $B)"
check "(b) after run 1: new / code switch / exclusion rows" "4/4/4" "$BR1"
check "(b) after run 2: the same, table identical" "$BR1/$BW1" "$BR2/$BW2"
check "(b) no custom_parameters row exists, so no unit id was assumed" "0" "$(q "SELECT COUNT(*) FROM $B.custom_parameters")"
desc_check "(b) $PW" $B $PW "$T_PW"; desc_check "(b) $SW" $B $SW "$T_SW"; desc_check "(b) $EX" $B $EX "$T_EX"

echo; echo "== (c) $C: no global_unit_types rows and no parameters at all; apply twice"
$M "$C" < "$MIG" 2>&1 | grep -v 'Using a password'; echo "run 1 exit ${PIPESTATUS[0]}"
$M "$C" < "$MIG" 2>&1 | grep -v 'Using a password'; echo "run 2 exit ${PIPESTATUS[0]}"
check "(c) parameters rows (nothing seeded, UPDATEs matched nothing, no failure)" "0" "$(q "SELECT COUNT(*) FROM $C.parameters")"

q "DROP DATABASE $A; DROP DATABASE $B; DROP DATABASE $C" >/dev/null
echo; echo "scratch databases $A, $B, $C dropped; $COPY is the caller's to drop"
echo "RESULT: $FAILS failed"
[ "$FAILS" -eq 0 ]
