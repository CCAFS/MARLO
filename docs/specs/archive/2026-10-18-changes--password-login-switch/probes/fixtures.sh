#!/usr/bin/env bash
# CHG-PWD-SWITCH-001-T08 -- fixtures of the live probes, in the throwaway copy only (refuses unless the database name
# contains "probe"). Idempotent: every run leaves the same rows.
#   - user 3862 is renamed Probe User, probe.user@outlook.com (no real address or name stays in the copy), gets the test password PROBE_PW_PASS (MD5, as MARLO stores it) and
#     the username probe.t06; it is an active, non-CGIAR member of units 45 (AICCRA) and 47 (AICCRA_III);
#   - probe.cgiar@cgiar.org (username probe.cgiar): is_cgiar_user = 1, active member of unit 47 (no Cognito); its password is
#     never used: a CGIAR password goes to Active Directory, which does not answer offline;
#   - the fixture users of the CHG-OTP-LOGIN-001 probes (README section 2): otp.inactive@, otp.nonmember@, otp.cgiar@,
#     otp.softdeleted@ and otp.staff@cgiar.org, and the pool otp.p01..p30@outlook.com, with agree_terms = 0
#     (a NULL there makes Hibernate flush an update the audit listener rejects, README section 9).
# Environment: PROBE_MYSQL (command opening "mysql -N <throwaway copy>", SQL on stdin), PROBE_PW_PASS (never printed).
set -euo pipefail
: "${PROBE_MYSQL:?}"; : "${PROBE_PW_PASS:?}"
case "$PROBE_MYSQL" in *probe*) ;; *) echo "refusing: PROBE_MYSQL must name a probe database"; exit 2;; esac
DB="$($PROBE_MYSQL -e 'SELECT DATABASE()')"
case "$DB" in *probe*) ;; *) echo "refusing: database $DB is not a probe copy"; exit 2;; esac
case "$PROBE_PW_PASS" in *"'"*|*'\'*) echo "refusing: the test password must not hold a quote or a backslash"; exit 2;; esac
$PROBE_MYSQL <<SQL
UPDATE users SET first_name='Probe', last_name='User', email='probe.user@outlook.com', username='probe.t06', password=MD5('$PROBE_PW_PASS'), is_active=1, is_cgiar_user=0,
  agree_terms=0 WHERE id=3862;
DROP TEMPORARY TABLE IF EXISTS fx;
CREATE TEMPORARY TABLE fx (email VARCHAR(255) PRIMARY KEY, active TINYINT, cgiar TINYINT, member TINYINT, member_active TINYINT);
INSERT INTO fx VALUES
  ('otp.inactive@outlook.com', 0, 0, 1, 1), ('otp.nonmember@outlook.com', 1, 0, 0, 1), ('otp.cgiar@outlook.com', 1, 1, 1, 1),
  ('otp.softdeleted@outlook.com', 1, 0, 1, 0), ('otp.staff@cgiar.org', 1, 1, 1, 1);
INSERT INTO fx SELECT CONCAT('otp.p', LPAD(n, 2, '0'), '@outlook.com'), 1, 0, 1, 1
  FROM (SELECT a.N + b.N * 10 + 1 AS n FROM (SELECT 0 N UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9) a,
        (SELECT 0 N UNION SELECT 1 UNION SELECT 2) b) t WHERE n <= 30;
INSERT INTO users (first_name, last_name, email, password, is_cgiar_user, is_active, auto_save, agree_terms, created_by, modification_justification)
  SELECT 'Probe', SUBSTRING_INDEX(email, '@', 1), email, MD5(UUID()), cgiar, active, 1, 0, 1082, 'probe fixture (CHG-PWD-SWITCH-001-T08)'
  FROM fx WHERE email NOT IN (SELECT email FROM users);
INSERT INTO crp_users (user_id, is_active, created_by, global_unit_id)
  SELECT u.id, fx.member_active, 1082, 45 FROM fx JOIN users u ON u.email = fx.email
  WHERE fx.member = 1 AND NOT EXISTS (SELECT 1 FROM crp_users c WHERE c.user_id = u.id AND c.global_unit_id = 45);
INSERT INTO users (first_name, last_name, username, email, password, is_cgiar_user, is_active, auto_save, agree_terms, created_by, modification_justification)
  SELECT 'Probe', 'Cgiar', 'probe.cgiar', 'probe.cgiar@cgiar.org', MD5(UUID()), 1, 1, 1, 0, 1082, 'probe fixture (CHG-PWD-SWITCH-001-T08)'
  FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'probe.cgiar@cgiar.org');
INSERT INTO crp_users (user_id, is_active, created_by, global_unit_id)
  SELECT u.id, 1, 1082, 47 FROM users u WHERE u.email = 'probe.cgiar@cgiar.org'
  AND NOT EXISTS (SELECT 1 FROM crp_users c WHERE c.user_id = u.id AND c.global_unit_id = 47);
SELECT 'fixtures', COUNT(*) FROM users WHERE email LIKE 'otp.%' OR email IN ('probe.user@outlook.com', 'probe.cgiar@cgiar.org');
SQL
echo "fixtures ready in $DB"
