#!/usr/bin/env bash
# CHG-OTP-LOGIN-001 -- T15 AC-17: the migration applied twice on three database shapes. Prints the invocations and the
# database names it touches. Creates and drops two scratch databases whose names contain "probe"; never touches any other.
#
# Environment:
#   PROBE_ADMIN_MYSQL  command opening a mysql client with the privileges to create databases, SQL on stdin or as arguments
#   PROBE_ADMIN_DUMP   same for mysqldump
#   PROBE_COPY_DB      the throwaway copy that already has the migration applied once   (default aiccradb_otp_probe)
set -euo pipefail
ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../../../../.." && pwd)"
MIG="$ROOT/marlo-web/src/main/resources/database/migrations/V2_6_0_20261009_1025__AddEmailOtpLogin.sql"
M="${PROBE_ADMIN_MYSQL:?}"; D="${PROBE_ADMIN_DUMP:?}"; COPY="${PROBE_COPY_DB:-aiccradb_otp_probe}"
B=otp_probe_ac17_b; C=otp_probe_ac17_c
case "$COPY$B$C" in *probe*probe*probe*) ;; *) echo "refusing: database names must contain probe"; exit 2;; esac
q() { $M -N -e "$1"; }
echo "migration file: ${MIG#$ROOT/}"
echo "invocation per run: \$PROBE_ADMIN_MYSQL <database> < \$MIGRATION   (databases: $COPY, $B, $C)"

echo; echo "== (a) $COPY: the throwaway copy, which already has the migration applied once; apply twice more"
echo "before: parameters key rows: $(q "SELECT COUNT(*) FROM $COPY.parameters WHERE \`key\`='crp_otp_allowed_email_domains'") ; otp tables: $(q "SHOW TABLES FROM $COPY LIKE 'otp\_%'" | tr '\n' ' ')"
for i in 1 2; do $M "$COPY" < "$MIG"; echo "run $i exit $?"; done
echo "after:  parameters key rows: $(q "SELECT COUNT(*) FROM $COPY.parameters WHERE \`key\`='crp_otp_allowed_email_domains'") ; by type: $(q "SELECT CONCAT(global_unit_type_id, ' ', COUNT(*)) FROM $COPY.parameters WHERE \`key\`='crp_otp_allowed_email_domains' GROUP BY global_unit_type_id" | tr '\n' ';')"
# structure of the two tables the migration touches, into two scratch databases
for DB in $B $C; do
  q "DROP DATABASE IF EXISTS $DB; CREATE DATABASE $DB CHARACTER SET utf8mb4"
  ( echo "SET foreign_key_checks=0;"; $D --no-data "$COPY" parameters global_unit_types ) | $M "$DB"
done
q "INSERT INTO $B.global_unit_types (id, name, is_active) VALUES (1,'Platform',1),(2,'Center',1),(3,'CRP',1),(4,'Center',1),(5,'Other',1)"

echo; echo "== (b) $B: no global_units table or rows at all, no OTP tables, global_unit_types 1-5, parameters empty; apply twice"
for i in 1 2; do $M "$B" < "$MIG"; echo "run $i exit $?"; done
echo "otp tables: $(q "SHOW TABLES FROM $B LIKE 'otp\_%'" | tr '\n' ' ') ; parameters by type (type, rows, category): $(q "SELECT CONCAT(global_unit_type_id,' ',COUNT(*),' ',MIN(category),';') FROM $B.parameters GROUP BY global_unit_type_id" | tr '\n' ' ')"
echo "global_units exists in $B: $(q "SHOW TABLES FROM $B LIKE 'global_units'" | wc -l | tr -d ' ') (0 = no such table)"

echo; echo "== (c) $C: no global_unit_types rows at all; apply twice"
for i in 1 2; do $M "$C" < "$MIG"; echo "run $i exit $?"; done
echo "parameters rows: $(q "SELECT COUNT(*) FROM $C.parameters")"
q "DROP DATABASE $B; DROP DATABASE $C"
echo; echo "scratch databases $B and $C dropped"
