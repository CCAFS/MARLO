-- CHG-PWD-SWITCH-001-T01 (FN-001, DA-001, DA-002, DA-003): adds a per-Global-Unit switch for password sign-in and
-- rewords the two one-time-code descriptions so they say what changes while password sign-in is off.
--
--   system_password_login_active       boolean, default 'true'. Password sign-in is offered on a Global Unit unless
--                                      a super admin turns it off for that unit. A missing value reads as on.
--
-- Seeded for each Global Unit type 1, 2, 3 and 4 that exists in the database at migration time (type 2 because
-- migration-only databases hold "Center" as id 2). No custom_parameters row is seeded, so deploying this migration
-- changes nobody's login: every unit starts on, and turning one off is an operational act on the Super Admin
-- Specificities tab.
--
-- The descriptions of system_otp_login_active and system_otp_excluded_email_domains (seeded by
-- V2_6_0_20261015_1418) are updated in place for every type. Those two migrations are NOT edited:
-- MarloFlywayConfiguration.repair() would silently re-checksum an edited, already applied migration.
--
-- Assumes no Global Unit, user or parameter id. Idempotent: parameters has no unique index on `key`, so the seed is
-- guarded by WHERE NOT EXISTS, and the two UPDATEs write the same text on a second run. DML only, so Flyway's
-- transaction covers all of it and a failure leaves nothing half applied. On a database without the two OTP keys
-- the UPDATEs match no row and succeed.

INSERT INTO parameters (global_unit_type_id, `key`, `description`, `format`, default_value, category)
SELECT gut.id, 'system_password_login_active',
  'Lets users of this Global Unit sign in with a password on the login page. Turning it off makes the one-time code sent to their email the way to sign in, for everyone who does not use Sign in with CGIAR, even if system_otp_login_active is off or their email domain is excluded. Users of Sign in with CGIAR are not affected. If the server has no code key, the password stays available.',
  1, 'true', 2
FROM global_unit_types gut
WHERE gut.id IN (1, 2, 3, 4)
AND NOT EXISTS (
  SELECT 1 FROM parameters existing_parameter
  WHERE existing_parameter.global_unit_type_id = gut.id
  AND existing_parameter.`key` = 'system_password_login_active'
);

UPDATE parameters
SET `description` = 'Lets users of this Global Unit sign in on the login page with a one-time code sent to their email, as an alternative to their password or to Sign in with CGIAR. Only registered users who are active and members of this Global Unit receive a code. Turning it off removes the option on the next login, and users whose email domain is listed in system_otp_excluded_email_domains do not receive a code. Exception: while system_password_login_active is off and the server has a code key, everyone who does not use Sign in with CGIAR signs in with the code, whatever this setting and the exclusion say.'
WHERE `key` = 'system_otp_login_active';

UPDATE parameters
SET `description` = 'Email domains that cannot sign in with a one-time code in this Global Unit, even when system_otp_login_active is on. Write exact domains separated by commas, for example cgiar.org,example.org. A subdomain is only excluded if it is listed itself. Leave it empty to exclude no one. Exception: while system_password_login_active is off, the exclusion applies only to users of Sign in with CGIAR.'
WHERE `key` = 'system_otp_excluded_email_domains';
