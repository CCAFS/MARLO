-- Remove the crp_planning_active and crp_reporting_active parameters.
-- Whether a global unit is in planning or reporting is decided by the description of the actual phase
-- (BaseAction.isPlanningActive() and isReportingActive()), not by these flags. Their only readers,
-- BaseAction.isPlanningActiveParam() and isReportingActiveParam(), had no callers in Java, FTL or JS and were
-- removed in the same change. No Pentaho report or database routine, view or trigger references them.
--
-- Safe in every environment:
-- * no global unit, parameter or user id is assumed; rows are matched by key only;
-- * only DML, so Flyway applies it in a single transaction: all or nothing;
-- * re-running it, or running it where the keys never existed, changes 0 rows.

-- Child rows first: custom_parameters.parameter_id references parameters.id.
DELETE FROM custom_parameters
WHERE parameter_id IN (
  SELECT id FROM parameters
  WHERE `key` IN ('crp_planning_active', 'crp_reporting_active')
);

DELETE FROM parameters
WHERE `key` IN ('crp_planning_active', 'crp_reporting_active');
