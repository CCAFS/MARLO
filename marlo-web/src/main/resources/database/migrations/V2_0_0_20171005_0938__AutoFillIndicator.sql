ALTER TABLE `center_project_funding_sources`
ADD COLUMN `auto_fill`  tinyint(1) NULL AFTER `sync_date`;

ALTER TABLE `center_projects`
ADD COLUMN `sync`  tinyint(1) NULL AFTER `is_region`,
ADD COLUMN `sync_date`  timestamp NULL AFTER `sync`,
ADD COLUMN `auto_fill`  tinyint(1) NULL AFTER `sync_date`;

