ALTER TABLE `project_funding_sources`
MODIFY COLUMN `donor`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL AFTER `funding_source_type_id`,
ADD COLUMN `ocs_code`  text NULL AFTER `project_id`,
ADD COLUMN `title`  text NULL AFTER `ocs_code`,
ADD COLUMN `sync`  tinyint(1) NOT NULL AFTER `donor`;

UPDATE `project_funding_sources` SET sync = 0;