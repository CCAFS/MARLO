-- CHG-OTP-LOGIN-001-T21 (DD-13, DA-002): replaces the one-time-code domain allow-list with a per-Global-Unit
-- switch and a per-Global-Unit domain exclusion.
--
--   system_otp_login_active            boolean, default 'false'. Code sign-in is offered on a Global Unit only
--                                      while it resolves true for that unit.
--   system_otp_excluded_email_domains  text, default ''. Exact domains, comma separated, that never get a code on
--                                      that unit. Empty excludes no one.
--
-- Both keys are seeded OFF and EMPTY for each Global Unit type 1, 2, 3 and 4 that exists in the database at
-- migration time (type 2 because migration-only databases hold "Center" as id 2). No custom_parameters row is
-- seeded, so deploying this migration changes nobody's login: enabling a unit is an operational act on the Super
-- Admin Specificities tab, not a deploy artifact.
--
-- The old key crp_otp_allowed_email_domains (V2_6_0_20261009_1025) is removed: its custom_parameters rows first
-- (custom_parameters.parameter_id references parameters.id), then its parameters rows. That migration is NOT edited:
-- MarloFlywayConfiguration.repair() would silently re-checksum an edited, already applied migration.
--
-- Assumes no Global Unit, user or parameter id. Idempotent: parameters has no unique index on `key`, so each seed is
-- guarded by WHERE NOT EXISTS, and the two deletes match nothing on a second run. DML only, so Flyway's transaction
-- covers all of it and a failure leaves nothing half applied.

INSERT INTO parameters (global_unit_type_id, `key`, `description`, `format`, default_value, category)
SELECT gut.id, 'system_otp_login_active',
  'Lets users of this Global Unit sign in on the login page with a one-time code sent to their email, as an alternative to their password or to Sign in with CGIAR. Only registered users who are active and members of this Global Unit receive a code. Turning it off removes the option on the next login. Users whose email domain is listed in system_otp_excluded_email_domains never receive a code.',
  1, 'false', 2
FROM global_unit_types gut
WHERE gut.id IN (1, 2, 3, 4)
AND NOT EXISTS (
  SELECT 1 FROM parameters existing_parameter
  WHERE existing_parameter.global_unit_type_id = gut.id
  AND existing_parameter.`key` = 'system_otp_login_active'
);

INSERT INTO parameters (global_unit_type_id, `key`, `description`, `format`, default_value, category)
SELECT gut.id, 'system_otp_excluded_email_domains',
  'Email domains that can never sign in with a one-time code in this Global Unit, even when system_otp_login_active is on. Write exact domains separated by commas, for example cgiar.org,example.org. A subdomain is only excluded if it is listed itself. Leave it empty to exclude no one.',
  4, '', 2
FROM global_unit_types gut
WHERE gut.id IN (1, 2, 3, 4)
AND NOT EXISTS (
  SELECT 1 FROM parameters existing_parameter
  WHERE existing_parameter.global_unit_type_id = gut.id
  AND existing_parameter.`key` = 'system_otp_excluded_email_domains'
);

DELETE custom_parameter
FROM custom_parameters custom_parameter
INNER JOIN parameters old_parameter ON old_parameter.id = custom_parameter.parameter_id
WHERE old_parameter.`key` = 'crp_otp_allowed_email_domains';

DELETE FROM parameters WHERE `key` = 'crp_otp_allowed_email_domains';
