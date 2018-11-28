INSERT INTO deliverable_types (`name`, description, is_fair, admin_type, global_unit_id)
SELECT `name`, description, is_fair, admin_type, 26 FROM deliverable_types where global_unit_id IN (21,22);