-- Specificity: innovation_without_alliance_alignment_active (A2-2613)
-- ON  -> the innovation form has no Alliance Alignment tab and Check for missing fields does not validate it
--        (AICCRA-FSRP IV). The stored answers are posted back unchanged on save, so earlier phases and their
--        summaries keep them.
-- OFF -> the Alliance Alignment tab shows when an Alliance center is selected and its levers are validated, as before.
--
-- Safe in every environment:
-- * the parameter inserts are guarded, because parameters has no unique index on `key` and a retry after a failed
--   attempt would otherwise duplicate them;
-- * the only unit turned on is matched by id and acronym together, so a database where id 47 is another unit, or
--   where AICCRA_III does not exist, turns nothing on;
-- * every other unit gets an explicit 'false' row: ValidSessionCrpInterceptor only replaces the keys the new unit has
--   rows for, so a missing row would keep a stale 'true' in the session after switching from AICCRA_III;
-- * created_by and modified_by resolve the seed account 3 when it exists and NULL when it does not.

INSERT INTO parameters (global_unit_type_id, `key`, `description`, `format`, default_value, category)
SELECT '1', 'innovation_without_alliance_alignment_active', 'Remove the Alliance Alignment tab from the innovation form and from its validation. The stored answers are kept.', '1', 'false', '2'
WHERE NOT EXISTS (SELECT 1 FROM parameters WHERE `key` = 'innovation_without_alliance_alignment_active' AND global_unit_type_id = '1');

INSERT INTO parameters (global_unit_type_id, `key`, `description`, `format`, default_value, category)
SELECT '3', 'innovation_without_alliance_alignment_active', 'Remove the Alliance Alignment tab from the innovation form and from its validation. The stored answers are kept.', '1', 'false', '2'
WHERE NOT EXISTS (SELECT 1 FROM parameters WHERE `key` = 'innovation_without_alliance_alignment_active' AND global_unit_type_id = '3');

INSERT INTO parameters (global_unit_type_id, `key`, `description`, `format`, default_value, category)
SELECT '4', 'innovation_without_alliance_alignment_active', 'Remove the Alliance Alignment tab from the innovation form and from its validation. The stored answers are kept.', '1', 'false', '2'
WHERE NOT EXISTS (SELECT 1 FROM parameters WHERE `key` = 'innovation_without_alliance_alignment_active' AND global_unit_type_id = '4');

INSERT INTO custom_parameters (parameter_id, `value`, created_by, is_active, active_since, modified_by, modification_justification, global_unit_id)
SELECT p.id, 'true', (SELECT u.id FROM users u WHERE u.id = 3), 1, NOW(),
  (SELECT u.id FROM users u WHERE u.id = 3), 'A2-2613: AICCRA-FSRP IV innovation form without Alliance Alignment', g.id
FROM global_units AS g
INNER JOIN parameters AS p ON p.global_unit_type_id = g.global_unit_type_id
WHERE p.`key` = 'innovation_without_alliance_alignment_active'
  AND g.id = 47 AND g.acronym = 'AICCRA_III'
  AND NOT EXISTS (SELECT 1 FROM custom_parameters cp WHERE cp.parameter_id = p.id AND cp.global_unit_id = g.id);

INSERT INTO custom_parameters (parameter_id, `value`, created_by, is_active, active_since, modified_by, modification_justification, global_unit_id)
SELECT p.id, 'false', (SELECT u.id FROM users u WHERE u.id = 3), 1, NOW(),
  (SELECT u.id FROM users u WHERE u.id = 3), 'A2-2613: innovation form keeps Alliance Alignment', g.id
FROM global_units AS g
INNER JOIN parameters AS p ON p.global_unit_type_id = g.global_unit_type_id
WHERE p.`key` = 'innovation_without_alliance_alignment_active'
  AND NOT EXISTS (SELECT 1 FROM custom_parameters cp WHERE cp.parameter_id = p.id AND cp.global_unit_id = g.id);
