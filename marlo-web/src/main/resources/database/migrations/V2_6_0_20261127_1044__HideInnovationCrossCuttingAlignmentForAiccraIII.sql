-- A2-2613: the Cross-cutting alignment tab of the innovation form (One CGIAR Alignment in other units: SDGs, Impact
-- Areas and their scores) leaves AICCRA-FSRP IV, the same way the Alliance Alignment tab did.
--
-- innovation_cross_cutting_alignment_active (created ON for every unit by V2_6_0_20261022_1015) is set OFF for the
-- global unit with acronym AICCRA_III. OFF hides the tab, skips its validation and keeps the stored SDGs, Impact Areas
-- and scores as they are, so earlier phases and the summary PDF keep them. Every other unit is left untouched.
--
-- To bring the tab back, set the row to 'true', or use System Admin > Parameters > Specificities.
--
-- Safe in every environment:
-- * no global unit, parameter or user id is assumed: the unit is matched by acronym, the parameter by key and the
--   unit's global unit type;
-- * nothing happens where there is no AICCRA_III unit or no parameter;
-- * the row is created when it is missing: ValidSessionCrpInterceptor only replaces the keys the new unit has rows
--   for, so a missing row would keep a stale 'true' in the session after switching from another unit;
-- * created_by and modified_by resolve the seed account 3 when it exists and NULL when it does not.

UPDATE custom_parameters cp
INNER JOIN parameters p ON p.id = cp.parameter_id
INNER JOIN global_units g ON g.id = cp.global_unit_id
SET cp.`value` = 'false',
  cp.modification_justification = 'A2-2613: Cross-cutting alignment hidden for AICCRA-FSRP IV'
WHERE p.`key` = 'innovation_cross_cutting_alignment_active'
  AND g.acronym = 'AICCRA_III';

INSERT INTO custom_parameters (parameter_id, `value`, created_by, is_active, active_since, modified_by, modification_justification, global_unit_id)
SELECT p.id, 'false', (SELECT u.id FROM users u WHERE u.id = 3), 1, NOW(),
  (SELECT u.id FROM users u WHERE u.id = 3), 'A2-2613: Cross-cutting alignment hidden for AICCRA-FSRP IV', g.id
FROM global_units AS g
INNER JOIN parameters AS p ON p.global_unit_type_id = g.global_unit_type_id
WHERE p.`key` = 'innovation_cross_cutting_alignment_active'
  AND g.acronym = 'AICCRA_III'
  AND NOT EXISTS (SELECT 1 FROM custom_parameters cp WHERE cp.parameter_id = p.id AND cp.global_unit_id = g.id);
