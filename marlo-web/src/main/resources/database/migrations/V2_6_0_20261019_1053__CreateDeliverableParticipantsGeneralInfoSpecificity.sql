-- Specificity: deliverable_participants_general_info_active (A2-2607)
-- ON  -> the deliverable form shows Participants/Trainees as its own section of the General information tab, and
--        the shared Technical Implementation Teams inside the Contribution section (AICCRA III form layout).
-- OFF -> Participants/Trainees stays a question of the Dissemination and metadata tab, and the shared clusters keep
--        their own section at the end of General information (general form layout).
-- Only the layout changes: the fields, their names and the validation are the same in both layouts.
--
-- Safe in every environment:
-- * the parameter inserts are guarded, because parameters has no unique index on `key` and a retry after a failed
--   attempt would otherwise duplicate them;
-- * the only seeded unit is matched by id and acronym together, so a database where id 47 is another unit, or where
--   AICCRA_III does not exist, gets no row and keeps the general layout (a missing row reads as false);
-- * created_by and modified_by resolve the seed account 3 when it exists and NULL when it does not.

INSERT INTO parameters (global_unit_type_id, `key`, `description`, `format`, default_value, category)
SELECT '1', 'deliverable_participants_general_info_active', 'Show Participants/Trainees as a section of the deliverable General information tab and the shared teams inside Contribution, instead of inside Dissemination and metadata.', '1', 'false', '2'
WHERE NOT EXISTS (SELECT 1 FROM parameters WHERE `key` = 'deliverable_participants_general_info_active' AND global_unit_type_id = '1');

INSERT INTO parameters (global_unit_type_id, `key`, `description`, `format`, default_value, category)
SELECT '3', 'deliverable_participants_general_info_active', 'Show Participants/Trainees as a section of the deliverable General information tab and the shared teams inside Contribution, instead of inside Dissemination and metadata.', '1', 'false', '2'
WHERE NOT EXISTS (SELECT 1 FROM parameters WHERE `key` = 'deliverable_participants_general_info_active' AND global_unit_type_id = '3');

INSERT INTO parameters (global_unit_type_id, `key`, `description`, `format`, default_value, category)
SELECT '4', 'deliverable_participants_general_info_active', 'Show Participants/Trainees as a section of the deliverable General information tab and the shared teams inside Contribution, instead of inside Dissemination and metadata.', '1', 'false', '2'
WHERE NOT EXISTS (SELECT 1 FROM parameters WHERE `key` = 'deliverable_participants_general_info_active' AND global_unit_type_id = '4');

INSERT INTO custom_parameters (parameter_id, `value`, created_by, is_active, active_since, modified_by, modification_justification, global_unit_id)
SELECT p.id, 'true', (SELECT u.id FROM users u WHERE u.id = 3), 1, NOW(),
  (SELECT u.id FROM users u WHERE u.id = 3), 'A2-2607: AICCRA III deliverable form layout', g.id
FROM global_units AS g
INNER JOIN parameters AS p ON p.global_unit_type_id = g.global_unit_type_id
WHERE p.`key` = 'deliverable_participants_general_info_active'
  AND g.id = 47 AND g.acronym = 'AICCRA_III'
  AND NOT EXISTS (SELECT 1 FROM custom_parameters cp WHERE cp.parameter_id = p.id AND cp.global_unit_id = g.id);
