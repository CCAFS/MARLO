ALTER TABLE `report_synthesis_financial_summary_budgets`
ADD COLUMN `expenditure_area_id`  bigint(20) NULL AFTER `liaison_institution_id`;

ALTER TABLE `report_synthesis_financial_summary_budgets` ADD FOREIGN KEY (`expenditure_area_id`) REFERENCES `powb_expenditure_areas` (`id`);

DELETE from report_synthesis_financial_summary_budgets;
