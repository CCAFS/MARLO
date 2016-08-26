START TRANSACTION;
ALTER TABLE `project_milestones`
ADD COLUMN `expected_value`  decimal(10,0) NULL DEFAULT NULL AFTER `modification_justification`,
ADD COLUMN `expected_unit`  bigint(20) NULL DEFAULT NULL AFTER `expected_value`,
ADD COLUMN `achieved_value`  decimal(10,0) NULL DEFAULT NULL AFTER `expected_unit`,
ADD COLUMN `narrative_target`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL AFTER `achieved_value`,
ADD COLUMN `narrative_achieved`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL AFTER `narrative_target`,
ADD COLUMN `year`  int(4) NOT NULL AFTER `narrative_achieved`;

ALTER TABLE `project_outcomes`
DROP COLUMN `year`;

ALTER TABLE `project_communications`
ADD COLUMN `year`  int(4) NOT NULL AFTER `modification_justification`;




 

COMMIT;