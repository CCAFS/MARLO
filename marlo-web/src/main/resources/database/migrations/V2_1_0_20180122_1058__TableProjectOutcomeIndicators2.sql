SET FOREIGN_KEY_CHECKS = 0;
ALTER TABLE `project_outcome_indicators`
MODIFY COLUMN `value`  double(10,2) NULL DEFAULT NULL AFTER `crp_outcome_indicator`;

