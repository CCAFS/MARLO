-- The "Contribution to Period Targets" section now always shows every milestone of a year,
-- so the specificity that switched that behaviour on and off is no longer read by the application.
-- It is removed here so it stops being offered in the Parameters administration screen.
DELETE FROM custom_parameters WHERE parameter_id IN (SELECT id FROM parameters WHERE `key`="contribution_performance_indicators_show_multiple_milestones_per_year_active");
DELETE FROM parameters WHERE `key`="contribution_performance_indicators_show_multiple_milestones_per_year_active";
