ALTER TABLE `project_budgets`
MODIFY COLUMN `amount`  double(30,2) NULL DEFAULT NULL AFTER `project_id`,
MODIFY COLUMN `gender_percentage`  double(10,2) NULL DEFAULT NULL AFTER `institution_id`;

