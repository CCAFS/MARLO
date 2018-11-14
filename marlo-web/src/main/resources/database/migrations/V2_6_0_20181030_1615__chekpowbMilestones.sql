ALTER TABLE `crp_milestones`
ADD COLUMN `is_powb`  tinyint(1) NULL DEFAULT 1 AFTER `modification_justification`;