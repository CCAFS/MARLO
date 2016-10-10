START TRANSACTION;

ALTER TABLE `project_outcomes`
MODIFY COLUMN `expected_value`  decimal(10,0) NULL AFTER `outcome_id`,
MODIFY COLUMN `expected_unit`  bigint(20) NULL AFTER `expected_value`,
MODIFY COLUMN `narrative_target`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL AFTER `achieved_value`;


 

COMMIT;