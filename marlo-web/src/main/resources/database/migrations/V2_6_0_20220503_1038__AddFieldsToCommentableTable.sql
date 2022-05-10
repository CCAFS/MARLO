ALTER TABLE `feedback_qa_commentable_fields` 
CHANGE COLUMN `parent_name` `parent_field_description` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT 'Record description' AFTER `section_name`,
CHANGE COLUMN `parent_id` `parent_field_identifier` text NULL COMMENT 'Field with the section ID (parentID field name)' AFTER `parent_field_description`,
CHANGE COLUMN `front_name` `field_description` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT 'Field name to show in de front end modal and reports' AFTER `field_name`,
MODIFY COLUMN `section_name` text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'Section struts name' AFTER `id`,
ADD COLUMN `section_description` text NULL COMMENT 'Section name to show in the reports' AFTER `section_name`;