-- A2-2607: align the AICCRA III deliverable form with its design.
-- The AICCRA III form has no Soil Health and Fertiliser Road Map (SHFRM) block and does ask the MELIA question, so
-- for AICCRA_III:
-- * shfrm_contribution_active       -> 'false' (the SHFRM block, its save and its validation are switched off)
-- * deliverable_melia_module_active -> 'true'  (the MELIA question, its save and its validation are switched on)
-- No other global unit is touched.
--
-- Safe in every environment:
-- * the unit is matched by id and acronym together, so a database where id 47 is another unit, or where AICCRA_III
--   does not exist, changes 0 rows;
-- * existing rows are updated in place; a missing row is inserted, so the result is the same whether or not the
--   unit already had a value;
-- * re-running the script changes nothing more;
-- * created_by and modified_by resolve the seed account 3 when it exists and NULL when it does not.

UPDATE custom_parameters AS cp
INNER JOIN parameters AS p ON p.id = cp.parameter_id
INNER JOIN global_units AS g ON g.id = cp.global_unit_id
SET cp.`value` = CASE p.`key` WHEN 'shfrm_contribution_active' THEN 'false' ELSE 'true' END,
  cp.modified_by = (SELECT u.id FROM users u WHERE u.id = 3),
  cp.modification_justification = 'A2-2607: align the AICCRA III deliverable form with its design'
WHERE p.`key` IN ('shfrm_contribution_active', 'deliverable_melia_module_active')
  AND g.id = 47 AND g.acronym = 'AICCRA_III';

INSERT INTO custom_parameters (parameter_id, `value`, created_by, is_active, active_since, modified_by, modification_justification, global_unit_id)
SELECT p.id, CASE p.`key` WHEN 'shfrm_contribution_active' THEN 'false' ELSE 'true' END,
  (SELECT u.id FROM users u WHERE u.id = 3), 1, NOW(), (SELECT u.id FROM users u WHERE u.id = 3),
  'A2-2607: align the AICCRA III deliverable form with its design', g.id
FROM global_units AS g
INNER JOIN parameters AS p ON p.global_unit_type_id = g.global_unit_type_id
WHERE p.`key` IN ('shfrm_contribution_active', 'deliverable_melia_module_active')
  AND g.id = 47 AND g.acronym = 'AICCRA_III'
  AND NOT EXISTS (SELECT 1 FROM custom_parameters cp WHERE cp.parameter_id = p.id AND cp.global_unit_id = g.id);
