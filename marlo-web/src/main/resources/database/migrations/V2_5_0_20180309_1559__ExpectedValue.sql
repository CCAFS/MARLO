ALTER TABLE `project_outcomes`
MODIFY COLUMN `expected_value`  double(20,2) NULL DEFAULT NULL AFTER `outcome_id`;

ALTER TABLE `project_milestones`
MODIFY COLUMN `expected_value`  decimal(10,2) NULL DEFAULT NULL AFTER `modification_justification`;

