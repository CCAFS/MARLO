-- Remove three parameters nothing reads any more.
-- crp_open_planing_date, crp_open_reporting_date and crp_real_reporting_date told BaseAction whether a project or
-- deliverable was "new". Their last readers were removed in 2018 (isProjectNew) and the last commented-out reference
-- in 2019; no Java, FTL, JS, Pentaho report or database routine, view, trigger or event references them.
--
-- Safe in every environment:
-- * no global unit, parameter or user id is assumed; rows are matched by key only;
-- * only DML, so Flyway applies it in a single transaction: all or nothing;
-- * re-running it, or running it where the keys never existed, changes 0 rows.

-- Child rows first: custom_parameters.parameter_id references parameters.id.
DELETE FROM custom_parameters
WHERE parameter_id IN (
  SELECT id FROM parameters
  WHERE `key` IN ('crp_open_planing_date', 'crp_open_reporting_date', 'crp_real_reporting_date')
);

DELETE FROM parameters
WHERE `key` IN ('crp_open_planing_date', 'crp_open_reporting_date', 'crp_real_reporting_date');
