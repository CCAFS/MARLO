ALTER TABLE `report_synthesis_flagship_progress_outcome_milestones`
MODIFY COLUMN `milestones_status`  bigint(20) NULL DEFAULT NULL AFTER `crp_milestone_id`;

ALTER TABLE `report_synthesis_flagship_progress_outcome_milestones` ADD FOREIGN KEY (`milestones_status`) REFERENCES `general_statuses` (`id`);