ALTER TABLE `crp_milestones`
ADD COLUMN `milestone_status`  bigint(20) NULL DEFAULT 2 AFTER `crp_program_outcome_id`;

ALTER TABLE `crp_milestones` ADD FOREIGN KEY (`milestone_status`) REFERENCES `general_statuses` (`id`);

