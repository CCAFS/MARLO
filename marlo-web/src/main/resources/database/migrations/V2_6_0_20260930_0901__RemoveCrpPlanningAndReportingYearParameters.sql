-- Remove the crp_planning_year and crp_reporting_year parameters.
-- They held a fixed planning and reporting year per global unit (last set around 2017-2020, 0 for AICCRA reporting)
-- while every live screen already takes the year from the actual phase. Their readers, BaseAction.getPlanningYear()
-- and getReportingYear(), were removed in the same change, and the legacy synthesis and P&R screens that used them
-- now read the year of the actual phase. No Java, FTL, JS, Pentaho report or database routine, view or trigger
-- references them.
--
-- Safe in every environment:
-- * no global unit, parameter or user id is assumed; rows are matched by key only;
-- * only DML, so Flyway applies it in a single transaction: all or nothing;
-- * re-running it, or running it where the keys never existed, changes 0 rows.

-- Child rows first: custom_parameters.parameter_id references parameters.id.
DELETE FROM custom_parameters
WHERE parameter_id IN (
  SELECT id FROM parameters
  WHERE `key` IN ('crp_planning_year', 'crp_reporting_year')
);

DELETE FROM parameters
WHERE `key` IN ('crp_planning_year', 'crp_reporting_year');
