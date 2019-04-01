ALTER TABLE `report_synthesis_flagship_progress_milestones`
ADD COLUMN `rep_ind_milestone_reason_id`  bigint(20) NULL AFTER `evidence`,
ADD COLUMN `other_reason`  text NULL AFTER `rep_ind_milestone_reason_id`;

ALTER TABLE `report_synthesis_flagship_progress_milestones` ADD FOREIGN KEY (`rep_ind_milestone_reason_id`) REFERENCES `rep_ind_milestone_reasons` (`id`);

