ALTER TABLE `project_budgets`
ADD COLUMN `center_amount`  bigint(20) NULL AFTER `modification_justification`,
ADD COLUMN `center_gender`  double NULL AFTER `center_amount`;

