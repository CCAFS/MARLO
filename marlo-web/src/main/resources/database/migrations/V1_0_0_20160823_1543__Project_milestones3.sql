START TRANSACTION;

ALTER TABLE `project_outcomes`
ADD COLUMN `achieved_unit`  bigint(20) NULL DEFAULT NULL AFTER `modification_justification`;

ALTER TABLE `project_milestones`
ADD COLUMN `narrative_gender`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL AFTER `year`,
ADD COLUMN `expetcted_gender`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL AFTER `narrative_gender`;




 

COMMIT;