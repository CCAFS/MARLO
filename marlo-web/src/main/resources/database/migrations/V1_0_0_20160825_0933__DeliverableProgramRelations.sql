ALTER TABLE `deliverables`
ADD COLUMN `program_id`  bigint(20) NULL AFTER `modification_justification`,
ADD COLUMN `outcome_id`  bigint(20) NULL AFTER `program_id`,
ADD COLUMN `cluster_activity_id`  bigint(20) NULL AFTER `outcome_id`,
ADD COLUMN `key_output_id`  bigint(20) NULL AFTER `cluster_activity_id`;

ALTER TABLE `deliverables` ADD CONSTRAINT `deliverables_program_fk` FOREIGN KEY (`program_id`) REFERENCES `crp_programs` (`id`);

ALTER TABLE `deliverables` ADD CONSTRAINT `deliverables_outcomes_fk` FOREIGN KEY (`outcome_id`) REFERENCES `crp_program_outcomes` (`id`);

ALTER TABLE `deliverables` ADD CONSTRAINT `deliverables_cluster_activities_fk` FOREIGN KEY (`cluster_activity_id`) REFERENCES `crp_cluster_of_activities` (`id`);

ALTER TABLE `deliverables` ADD CONSTRAINT `deliverables_key_output_id` FOREIGN KEY (`key_output_id`) REFERENCES `crp_cluster_key_outputs` (`id`);