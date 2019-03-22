/* ReportSynthesisFinancialSummaryBudgets */
ALTER TABLE `report_synthesis_financial_summary_budgets`
ADD COLUMN `comments`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT 'AR2018 Field' AFTER `expenditure_area_id`;