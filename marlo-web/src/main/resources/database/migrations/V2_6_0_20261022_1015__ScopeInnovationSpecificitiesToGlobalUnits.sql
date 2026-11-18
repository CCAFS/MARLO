-- A2-2613: the AICCRA-FSRP IV changes of the innovation form are scoped by global unit (PM decision, 2026-10-22).
--
-- 1. innovation_alliance_alignment_active (created by V2_6_0_20261021_2318, OFF for every unit): the Alliance
--    Alignment tab leaves AICCRA-FSRP IV only. Every other unit gets it back (ON: the tab shows when an Alliance
--    center is selected, as before A2-2613). Only the rows that migration seeded are touched, so a value an admin
--    set since then is kept.
-- 2. innovation_cross_cutting_alignment_active (new): the Cross-cutting alignment (One CGIAR Alignment) tab, SDGs and
--    Impact Area scores. ON for every unit; OFF hides the tab, skips its validation and keeps the stored data.
-- 3. innovation_value_chains_active (new): the Value chain(s) field of General Information. ON for AICCRA-FSRP IV
--    only; OFF hides the field, skips its validation and keeps the stored data.
--
-- AICCRA-FSRP IV is the global unit with acronym AICCRA_III. To change a unit later, update its custom_parameters
-- row, or use System Admin > Parameters > Specificities.
--
-- Safe in every environment:
-- * the parameter inserts are guarded, because parameters has no unique index on `key`;
-- * no global unit, parameter or user id is assumed: units are matched by global unit type, FSRP IV by acronym;
-- * every unit gets an explicit row: ValidSessionCrpInterceptor only replaces the keys the new unit has rows for, so
--   a missing row would keep a stale value in the session after switching units;
-- * created_by and modified_by resolve the seed account 3 when it exists and NULL when it does not.

-- 1. Alliance Alignment: back for every unit but AICCRA-FSRP IV
UPDATE custom_parameters cp
INNER JOIN parameters p ON p.id = cp.parameter_id
INNER JOIN global_units g ON g.id = cp.global_unit_id
SET cp.`value` = 'true',
  cp.modification_justification = 'A2-2613: Alliance Alignment hidden for AICCRA-FSRP IV only'
WHERE p.`key` = 'innovation_alliance_alignment_active'
  AND (g.acronym IS NULL OR g.acronym <> 'AICCRA_III')
  AND cp.`value` = 'false'
  AND cp.modification_justification = 'A2-2613: innovation form without Alliance Alignment';

-- 2. Cross-cutting alignment tab: ON for every unit
INSERT INTO parameters (global_unit_type_id, `key`, `description`, `format`, default_value, category)
SELECT t.type_id, 'innovation_cross_cutting_alignment_active', 'Show the Cross-cutting alignment (One CGIAR Alignment) tab of the innovation form (SDGs and Impact Area scores), and validate it.', '1', 'true', '2'
FROM (SELECT '1' AS type_id UNION ALL SELECT '3' UNION ALL SELECT '4') AS t
WHERE NOT EXISTS (SELECT 1 FROM parameters WHERE `key` = 'innovation_cross_cutting_alignment_active' AND global_unit_type_id = t.type_id);

INSERT INTO custom_parameters (parameter_id, `value`, created_by, is_active, active_since, modified_by, modification_justification, global_unit_id)
SELECT p.id, 'true', (SELECT u.id FROM users u WHERE u.id = 3), 1, NOW(),
  (SELECT u.id FROM users u WHERE u.id = 3), 'A2-2613: Cross-cutting alignment tab', g.id
FROM global_units AS g
INNER JOIN parameters AS p ON p.global_unit_type_id = g.global_unit_type_id
WHERE p.`key` = 'innovation_cross_cutting_alignment_active'
  AND NOT EXISTS (SELECT 1 FROM custom_parameters cp WHERE cp.parameter_id = p.id AND cp.global_unit_id = g.id);

-- 3. Value chain(s): ON for AICCRA-FSRP IV only
INSERT INTO parameters (global_unit_type_id, `key`, `description`, `format`, default_value, category)
SELECT t.type_id, 'innovation_value_chains_active', 'Ask an innovation for its value chain(s) in General Information, and validate them.', '1', 'false', '2'
FROM (SELECT '1' AS type_id UNION ALL SELECT '3' UNION ALL SELECT '4') AS t
WHERE NOT EXISTS (SELECT 1 FROM parameters WHERE `key` = 'innovation_value_chains_active' AND global_unit_type_id = t.type_id);

INSERT INTO custom_parameters (parameter_id, `value`, created_by, is_active, active_since, modified_by, modification_justification, global_unit_id)
SELECT p.id, IF(g.acronym = 'AICCRA_III', 'true', 'false'), (SELECT u.id FROM users u WHERE u.id = 3), 1, NOW(),
  (SELECT u.id FROM users u WHERE u.id = 3), 'A2-2613: value chains of an innovation', g.id
FROM global_units AS g
INNER JOIN parameters AS p ON p.global_unit_type_id = g.global_unit_type_id
WHERE p.`key` = 'innovation_value_chains_active'
  AND NOT EXISTS (SELECT 1 FROM custom_parameters cp WHERE cp.parameter_id = p.id AND cp.global_unit_id = g.id);
