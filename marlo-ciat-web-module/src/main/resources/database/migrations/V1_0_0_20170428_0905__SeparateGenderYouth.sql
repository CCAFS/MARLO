ALTER TABLE `project_crosscuting_themes`
CHANGE COLUMN `gender_youth` `gender`  tinyint(1) NULL DEFAULT NULL AFTER `climate_change`,
ADD COLUMN `youth`  tinyint(1) NULL AFTER `gender`;