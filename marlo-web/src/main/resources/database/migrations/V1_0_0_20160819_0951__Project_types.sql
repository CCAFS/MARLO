ALTER TABLE `projects`
ADD COLUMN `is_regional`  tinyint(1) NOT NULL DEFAULT 0 AFTER `file_annual_report_to_donnor`,
ADD COLUMN `is_national`  tinyint(1) NOT NULL DEFAULT 0 AFTER `is_regional`;

