-- A2-2607: give every global unit an explicit deliverable_participants_general_info_active row.
-- V2_6_0_20261019_1053 seeded only AICCRA_III ('true') and left every other unit without a row, relying on a missing
-- row reading as false. It does not on a unit switch: ValidSessionCrpInterceptor replaces only the keys the new unit
-- has rows for, so a user who opened AICCRA_III and then switched to AICCRA kept 'true' in the session and saw the
-- AICCRA III layout there. An explicit 'false' row replaces that stale value on every switch.
--
-- Safe in every environment:
-- * no global unit, parameter or user id is assumed; units are matched through their global unit type;
-- * only units that have no row yet get one, so AICCRA_III keeps its 'true' and a re-run inserts nothing;
-- * created_by and modified_by resolve the seed account 3 when it exists and NULL when it does not.

INSERT INTO custom_parameters (parameter_id, `value`, created_by, is_active, active_since, modified_by, modification_justification, global_unit_id)
SELECT p.id, 'false', (SELECT u.id FROM users u WHERE u.id = 3), 1, NOW(),
  (SELECT u.id FROM users u WHERE u.id = 3), 'A2-2607: general deliverable form layout', g.id
FROM global_units AS g
INNER JOIN parameters AS p ON p.global_unit_type_id = g.global_unit_type_id
WHERE p.`key` = 'deliverable_participants_general_info_active'
  AND NOT EXISTS (SELECT 1 FROM custom_parameters cp WHERE cp.parameter_id = p.id AND cp.global_unit_id = g.id);
