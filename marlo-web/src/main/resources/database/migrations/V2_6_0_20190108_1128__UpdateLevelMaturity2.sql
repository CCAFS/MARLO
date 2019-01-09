ALTER TABLE `rep_ind_stage_process`
ADD COLUMN `rep_ind_stage_studies_id`  bigint(20) NULL AFTER `year`;

ALTER TABLE `rep_ind_stage_process` ADD FOREIGN KEY (`rep_ind_stage_studies_id`) REFERENCES `rep_ind_stage_studies` (`id`);

