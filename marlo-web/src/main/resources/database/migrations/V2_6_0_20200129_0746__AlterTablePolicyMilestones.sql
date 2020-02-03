ALTER TABLE `policy_milestones`
ADD COLUMN `is_primary`  tinyint(1) NULL AFTER `crp_milestone_id`;