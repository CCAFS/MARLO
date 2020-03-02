ALTER TABLE `projects_info`
ADD COLUMN `is_evaluated`  tinyint(1) NULL AFTER `created_by`;