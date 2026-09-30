-- Remove crp_login_header_text, which nothing reads any more.
-- It customised the label of the non-production banner through BaseAction.getCustomTextHeader(). header.ftl stopped
-- calling that getter in 94f16b6b7d (2025-03-13) and now prints "Testing Environment" directly, which is also the
-- only value the parameter ever held, so the banner does not change.
--
-- Safe in every environment:
-- * no global unit, parameter or user id is assumed; rows are matched by key only;
-- * only DML, so Flyway applies it in a single transaction: all or nothing;
-- * re-running it, or running it where the key never existed, changes 0 rows.

-- Child rows first: custom_parameters.parameter_id references parameters.id.
DELETE FROM custom_parameters
WHERE parameter_id IN (SELECT id FROM parameters WHERE `key` = 'crp_login_header_text');

DELETE FROM parameters WHERE `key` = 'crp_login_header_text';
