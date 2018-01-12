/* Add subDepartment column to projectPartner */
ALTER TABLE project_partners ADD sub_department text CHARACTER SET utf8 COLLATE utf8_general_ci NULL AFTER institution_id;

/* Add flag subdepartmentActive to institutionTypes */
ALTER TABLE institution_types ADD COLUMN sub_department_active tinyint(1) DEFAULT 0 NOT NULL AFTER `acronym`;

/* Set subDepartmentFlag to governments */
UPDATE institution_types it SET it.sub_department_active = 1 WHERE it.name LIKE 'Government%';