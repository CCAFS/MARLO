-- Remove the AICCRA Additional Financing start-phase parameter.
-- It only drove BaseAction.isAFPhase(), which compared global phase ids against it. Every phase created after the
-- AF started satisfies that check, so the AF labels are now always shown and the parameter is no longer read.
--
-- Safe in every environment:
-- * no global unit, parameter or phase id is assumed; rows are matched by key only;
-- * only DML, so Flyway applies it in a single transaction: all or nothing;
-- * re-running it, or running it where the key never existed, changes 0 rows.

-- 1. Stage studies now always use description_af for their composed name. V2_6_0_20240822_0950 only filled it for
--    ids 1-3, so copy the base description into any stage left without one instead of leaving it unnamed.
UPDATE rep_ind_stage_studies
SET description_af = description
WHERE (description_af IS NULL OR TRIM(description_af) = '')
  AND description IS NOT NULL;

-- 2. Child rows first: custom_parameters.parameter_id references parameters.id.
DELETE FROM custom_parameters
WHERE parameter_id IN (SELECT id FROM parameters WHERE `key` = 'crp_aiccra_af_start_phase');

DELETE FROM parameters WHERE `key` = 'crp_aiccra_af_start_phase';
