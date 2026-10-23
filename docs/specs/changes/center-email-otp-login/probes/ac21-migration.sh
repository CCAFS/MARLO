#!/usr/bin/env bash
# CHG-OTP-LOGIN-001 -- T21 AC-21: the switch-and-exclusion migration applied twice on three database shapes, checked
# against the DA-002 text and the old allow-list rows. Prints the invocations and the database names it touches, and
# a PASS or FAIL line per check; the exit status is the number of FAIL lines (capped at 1).
# Creates and drops two scratch databases whose names contain "probe"; never touches any other.
#
# Environment:
#   PROBE_ADMIN_MYSQL  command opening a mysql client with the privileges to create databases, SQL on stdin or as arguments
#   PROBE_ADMIN_DUMP   same for mysqldump
#   PROBE_COPY_DB      the throwaway copy that already has the T01 migration applied   (default aiccradb_otp_probe)
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../../../../.." && pwd)"
MIGDIR="$ROOT/marlo-web/src/main/resources/database/migrations"
MIG="$(ls "$MIGDIR"/V2_6_0_*__ReplaceOtpAllowListWithSwitchAndExclusion.sql)"
REQ="$ROOT/docs/specs/changes/center-email-otp-login/requirements.md"
M="${PROBE_ADMIN_MYSQL:?}"; D="${PROBE_ADMIN_DUMP:?}"; COPY="${PROBE_COPY_DB:-aiccradb_otp_probe}"
B=otp_probe_ac21_b; C=otp_probe_ac21_c
case "$COPY$B$C" in *probe*probe*probe*) ;; *) echo "refusing: database names must contain probe"; exit 2;; esac
q() { $M -N -e "$1"; }
FAILS=0
check() { # check <label> <expected> <actual>
  if [ "$2" = "$3" ]; then echo "PASS  $1 ($3)"; else echo "FAIL  $1 (expected: $2, got: $3)"; FAILS=$((FAILS + 1)); fi
}
SWITCH=system_otp_login_active; EXCL=system_otp_excluded_email_domains; OLD=crp_otp_allowed_email_domains
# The two descriptions, read from DA-002 so a drift between the migration and the requirement is a FAIL here.
desc_of() { grep -F "  - \`$1\`: \"" "$REQ" | head -1 | sed -e "s/^  - \`$1\`: \"//" -e 's/"$//'; }
DESC_SWITCH="$(desc_of $SWITCH)"; DESC_EXCL="$(desc_of $EXCL)"
check "DA-002 text of $SWITCH found in requirements.md" "yes" "$([ -n "$DESC_SWITCH" ] && echo yes || echo no)"
check "DA-002 text of $EXCL found in requirements.md" "yes" "$([ -n "$DESC_EXCL" ] && echo yes || echo no)"
case "$DESC_SWITCH$DESC_EXCL" in *"'"*|*'\'*) echo "refusing: a description holds a quote or a backslash"; exit 2;; esac

rows_of() { q "SELECT COUNT(*) FROM $1.parameters WHERE \`key\`='$2'"; }
by_type() { q "SELECT IFNULL(GROUP_CONCAT(CONCAT(global_unit_type_id,'/',format,'/',IFNULL(default_value,'NULL'),'/',category) ORDER BY global_unit_type_id SEPARATOR ' '),'none') FROM $1.parameters WHERE \`key\`='$2'"; }
old_cp() { q "SELECT COUNT(*) FROM $1.custom_parameters cp JOIN $1.parameters p ON p.id = cp.parameter_id WHERE p.\`key\`='$OLD'"; }
others() { q "SELECT CONCAT(COUNT(*), ':', IFNULL(SUM(CRC32(CONCAT(p.id,'|',IFNULL(p.\`key\`,''),'|',IFNULL(p.default_value,'')))),0)) FROM $1.parameters p WHERE p.\`key\` NOT IN ('$SWITCH','$EXCL','$OLD')"; }
others_cp() { q "SELECT CONCAT(COUNT(*), ':', IFNULL(SUM(CRC32(CONCAT(cp.id,'|',cp.parameter_id,'|',cp.global_unit_id,'|',IFNULL(cp.value,'')))),0)) FROM $1.custom_parameters cp JOIN $1.parameters p ON p.id = cp.parameter_id WHERE p.\`key\` NOT IN ('$SWITCH','$EXCL','$OLD')"; }

echo "migration file: ${MIG#$ROOT/}"
echo "invocation per run: \$PROBE_ADMIN_MYSQL <database> < \$MIGRATION   (databases: $COPY, $B, $C)"

echo; echo "== (a) $COPY: the throwaway copy, which has the T01 migration; a fixture value on the old key for one unit; apply twice"
# A custom_parameters row on the old key, so the delete order (child before parent) is exercised. FK-safe: it borrows an
# existing parameter id of the old key and an existing global unit.
if [ "$(old_cp "$COPY")" = "0" ]; then
  q "INSERT INTO $COPY.custom_parameters (parameter_id, value, is_active, global_unit_id) SELECT p.id, 'outlook.com', 1, (SELECT MIN(id) FROM $COPY.global_units) FROM $COPY.parameters p WHERE p.\`key\`='$OLD' AND p.global_unit_type_id=1"
fi
echo "before: old key parameters rows $(rows_of "$COPY" $OLD) ; old key custom_parameters rows $(old_cp "$COPY") ; new keys $(rows_of "$COPY" $SWITCH)+$(rows_of "$COPY" $EXCL)"
OTHERS_BEFORE="$(others "$COPY")|$(others_cp "$COPY")"
$M "$COPY" < "$MIG"; echo "run 1 exit $?"
AFTER1="$(rows_of "$COPY" $SWITCH)/$(rows_of "$COPY" $EXCL)/$(rows_of "$COPY" $OLD)/$(old_cp "$COPY")"
$M "$COPY" < "$MIG"; echo "run 2 exit $?"
AFTER2="$(rows_of "$COPY" $SWITCH)/$(rows_of "$COPY" $EXCL)/$(rows_of "$COPY" $OLD)/$(old_cp "$COPY")"
check "(a) after run 1: switch rows / exclusion rows / old rows / old custom rows" "4/4/0/0" "$AFTER1"
check "(a) after run 2: nothing duplicated, nothing more removed" "$AFTER1" "$AFTER2"
check "(a) switch seeded as boolean, default false, specificity, for types 1-4" "1/1/false/2 2/1/false/2 3/1/false/2 4/1/false/2" "$(by_type "$COPY" $SWITCH)"
check "(a) exclusion seeded as text, empty default, specificity, for types 1-4" "1/4//2 2/4//2 3/4//2 4/4//2" "$(by_type "$COPY" $EXCL)"
check "(a) no custom_parameters row seeded for either key (every unit starts off)" "0" "$(q "SELECT COUNT(*) FROM $COPY.custom_parameters cp JOIN $COPY.parameters p ON p.id=cp.parameter_id WHERE p.\`key\` IN ('$SWITCH','$EXCL')")"
check "(a) every other parameters and custom_parameters row is unchanged" "$OTHERS_BEFORE" "$(others "$COPY")|$(others_cp "$COPY")"
check "(a) description of $SWITCH is the DA-002 text, on all 4 rows" "4" "$(q "SELECT COUNT(*) FROM $COPY.parameters WHERE \`key\`='$SWITCH' AND BINARY description = BINARY '$DESC_SWITCH'")"
check "(a) description of $EXCL is the DA-002 text, on all 4 rows" "4" "$(q "SELECT COUNT(*) FROM $COPY.parameters WHERE \`key\`='$EXCL' AND BINARY description = BINARY '$DESC_EXCL'")"
check "(a) the T01 migration file is untouched (it is not part of the change)" "" "$(cd "$ROOT" && git status --porcelain -- marlo-web/src/main/resources/database/migrations/V2_6_0_20261009_1025__AddEmailOtpLogin.sql)"

# structure of the tables the migration touches, plus the seeded units (< 29, so no unit 45 or 47), into two scratch databases
for DB in $B $C; do
  q "DROP DATABASE IF EXISTS $DB; CREATE DATABASE $DB CHARACTER SET latin1"
  ( echo "SET foreign_key_checks=0;"; $D --no-data "$COPY" parameters custom_parameters global_unit_types global_units ) | $M "$DB"
done
q "INSERT INTO $B.global_unit_types (id, name, is_active) SELECT id, name, is_active FROM $COPY.global_unit_types"
q "SET foreign_key_checks=0; INSERT INTO $B.global_units SELECT * FROM $COPY.global_units WHERE id < 29"
q "INSERT INTO $B.parameters (global_unit_type_id, \`key\`, \`description\`, \`format\`, default_value, category) SELECT id, '$OLD', 'old', 4, '', 2 FROM $B.global_unit_types WHERE id IN (1,2,3,4)"
q "INSERT INTO $B.custom_parameters (parameter_id, value, is_active, global_unit_id) SELECT p.id, 'x.org', 1, 1 FROM $B.parameters p WHERE p.\`key\`='$OLD' AND p.global_unit_type_id=1"

echo; echo "== (b) $B: no Global Unit 45 or 47 ($(q "SELECT COUNT(*) FROM $B.global_units") seeded units, ids < 29), the old key with one custom value; apply twice"
check "(b) fixture: no unit 45 or 47" "0" "$(q "SELECT COUNT(*) FROM $B.global_units WHERE id IN (45,47)")"
check "(b) before: old key rows / old custom rows" "4/1" "$(rows_of "$B" $OLD)/$(old_cp "$B")"
$M "$B" < "$MIG"; echo "run 1 exit $?"
BA1="$(rows_of "$B" $SWITCH)/$(rows_of "$B" $EXCL)/$(rows_of "$B" $OLD)/$(old_cp "$B")"
$M "$B" < "$MIG"; echo "run 2 exit $?"
BA2="$(rows_of "$B" $SWITCH)/$(rows_of "$B" $EXCL)/$(rows_of "$B" $OLD)/$(old_cp "$B")"
check "(b) after run 1: switch / exclusion / old rows / old custom rows" "4/4/0/0" "$BA1"
check "(b) after run 2: the same" "$BA1" "$BA2"
check "(b) no custom_parameters row exists for either new key, so no unit id was assumed" "0" "$(q "SELECT COUNT(*) FROM $B.custom_parameters cp JOIN $B.parameters p ON p.id=cp.parameter_id WHERE p.\`key\` IN ('$SWITCH','$EXCL')")"

echo; echo "== (c) $C: no global_unit_types rows at all, no old key; apply twice"
$M "$C" < "$MIG"; echo "run 1 exit $?"
$M "$C" < "$MIG"; echo "run 2 exit $?"
check "(c) parameters rows" "0" "$(q "SELECT COUNT(*) FROM $C.parameters")"
q "DROP DATABASE $B; DROP DATABASE $C"
echo; echo "scratch databases $B and $C dropped; $COPY is the caller's to drop"
echo "RESULT: $FAILS failed"
[ "$FAILS" -eq 0 ]
