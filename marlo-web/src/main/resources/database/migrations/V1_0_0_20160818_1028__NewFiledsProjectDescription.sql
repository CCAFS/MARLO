START TRANSACTION;

ALTER TABLE `projects`
ADD COLUMN `scale`  int NULL AFTER `file_annual_report_to_donnor`;

ALTER TABLE `project_locations`
ADD COLUMN `scope`  int NULL DEFAULT 0 AFTER `modification_justification`;


COMMIT;