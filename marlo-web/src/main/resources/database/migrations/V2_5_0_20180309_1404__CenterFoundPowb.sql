ALTER TABLE `powb_financial_planned_budget`
ADD COLUMN `center_founds`  double NOT NULL AFTER `modification_justification`,
ADD COLUMN `carry`  double NOT NULL AFTER `center_founds`;

