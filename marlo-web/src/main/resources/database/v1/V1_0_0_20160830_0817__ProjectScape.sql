START TRANSACTION;
ALTER TABLE `projects`
DROP COLUMN `is_global`,
DROP COLUMN `is_national`,
CHANGE COLUMN `is_regional` `scape`  int(1) NOT NULL DEFAULT 0 AFTER `file_annual_report_to_donnor`;


COMMIT;