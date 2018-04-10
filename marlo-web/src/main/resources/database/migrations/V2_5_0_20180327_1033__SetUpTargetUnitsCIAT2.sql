INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'Agricultural development initiatives';

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'Organizations and Institutions';

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'Subnational public/private initiatives';

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'Institutions';

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'National/state organizations';

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
VALUES (-1, 0, NOW(), 3, 3, '', 29);

