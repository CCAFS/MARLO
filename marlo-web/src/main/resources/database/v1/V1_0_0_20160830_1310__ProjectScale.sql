START TRANSACTION;
ALTER TABLE `projects`
CHANGE COLUMN `scape` `scale`  int(1) NOT NULL DEFAULT 0 AFTER `file_annual_report_to_donnor`;



COMMIT;