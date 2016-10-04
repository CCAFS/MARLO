ALTER TABLE `projects`
ADD COLUMN `is_project_leader_edit`  tinyint(1) NULL DEFAULT 1 AFTER `crp_id`;