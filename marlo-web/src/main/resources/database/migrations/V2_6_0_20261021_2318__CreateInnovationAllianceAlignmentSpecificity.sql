-- Specificity: innovation_alliance_alignment_active (A2-2613)
-- ON  -> the innovation form shows the Alliance Alignment tab when an Alliance center is selected, and Check for
--        missing fields validates its levers (the behaviour before A2-2613).
-- OFF -> the innovation form has no Alliance Alignment tab and does not validate it. The stored answers are posted
--        back unchanged on save, so earlier phases and their summaries keep them.
--
-- Rollout: A2-2613 removes the tab, so it is created OFF for every global unit, and a unit with no row reads as OFF
-- too. To bring the tab back for a unit, set its row to 'true', for example AICCRA:
--   UPDATE custom_parameters cp INNER JOIN parameters p ON p.id = cp.parameter_id
--   INNER JOIN global_units g ON g.id = cp.global_unit_id
--   SET cp.`value` = 'true'
--   WHERE p.`key` = 'innovation_alliance_alignment_active' AND g.acronym = 'AICCRA';
--
-- Safe in every environment:
-- * the parameter inserts are guarded, because parameters has no unique index on `key` and a retry after a failed
--   attempt would otherwise duplicate them;
-- * no global unit, parameter or user id is assumed; units are matched through their global unit type;
-- * every unit gets an explicit 'false' row: ValidSessionCrpInterceptor only replaces the keys the new unit has
--   rows for, so a missing row would keep a stale 'true' in the session after switching from a unit that has it on;
-- * created_by and modified_by resolve the seed account 3 when it exists and NULL when it does not.

INSERT INTO parameters (global_unit_type_id, `key`, `description`, `format`, default_value, category)
SELECT '1', 'innovation_alliance_alignment_active', 'Show the Alliance Alignment tab of the innovation form when an Alliance center is selected, and validate it.', '1', 'false', '2'
WHERE NOT EXISTS (SELECT 1 FROM parameters WHERE `key` = 'innovation_alliance_alignment_active' AND global_unit_type_id = '1');

INSERT INTO parameters (global_unit_type_id, `key`, `description`, `format`, default_value, category)
SELECT '3', 'innovation_alliance_alignment_active', 'Show the Alliance Alignment tab of the innovation form when an Alliance center is selected, and validate it.', '1', 'false', '2'
WHERE NOT EXISTS (SELECT 1 FROM parameters WHERE `key` = 'innovation_alliance_alignment_active' AND global_unit_type_id = '3');

INSERT INTO parameters (global_unit_type_id, `key`, `description`, `format`, default_value, category)
SELECT '4', 'innovation_alliance_alignment_active', 'Show the Alliance Alignment tab of the innovation form when an Alliance center is selected, and validate it.', '1', 'false', '2'
WHERE NOT EXISTS (SELECT 1 FROM parameters WHERE `key` = 'innovation_alliance_alignment_active' AND global_unit_type_id = '4');

INSERT INTO custom_parameters (parameter_id, `value`, created_by, is_active, active_since, modified_by, modification_justification, global_unit_id)
SELECT p.id, 'false', (SELECT u.id FROM users u WHERE u.id = 3), 1, NOW(),
  (SELECT u.id FROM users u WHERE u.id = 3), 'A2-2613: innovation form without Alliance Alignment', g.id
FROM global_units AS g
INNER JOIN parameters AS p ON p.global_unit_type_id = g.global_unit_type_id
WHERE p.`key` = 'innovation_alliance_alignment_active'
  AND NOT EXISTS (SELECT 1 FROM custom_parameters cp WHERE cp.parameter_id = p.id AND cp.global_unit_id = g.id);
