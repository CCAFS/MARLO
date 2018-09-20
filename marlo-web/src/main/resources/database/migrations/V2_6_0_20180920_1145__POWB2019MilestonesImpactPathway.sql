ALTER TABLE `crp_milestones`
ADD COLUMN `powb_ind_following_milestone_id`  bigint(20) NULL AFTER `crp_program_outcome_id`,
ADD COLUMN `powb_ind_assesment_risk_id`  bigint(20) NULL AFTER `powb_ind_following_milestone_id`,
ADD COLUMN `powb_ind_milestone_risk_id`  bigint(20) NULL AFTER `powb_ind_assesment_risk_id`,
ADD COLUMN `powb_milestone_other_risk`  text NULL AFTER `powb_ind_milestone_risk_id`,
ADD COLUMN `powb_milestone_verification`  text NULL AFTER `powb_milestone_other_risk`,
ADD COLUMN `rep_ind_gender_focus_level_id`  bigint(20) NULL AFTER `powb_milestone_verification`,
ADD COLUMN `rep_ind_youth_focus_level_id`  bigint(20) NULL AFTER `rep_ind_gender_focus_level_id`,
ADD COLUMN `rep_ind_capdev_focus_level_id`  bigint(20) NULL AFTER `rep_ind_youth_focus_level_id`,
ADD COLUMN `rep_ind_climate_focus_level_id`  bigint(20) NULL AFTER `rep_ind_capdev_focus_level_id`;

ALTER TABLE `crp_milestones` ADD FOREIGN KEY (`powb_ind_following_milestone_id`) REFERENCES `powb_ind_following_milestones` (`id`);

ALTER TABLE `crp_milestones` ADD FOREIGN KEY (`powb_ind_assesment_risk_id`) REFERENCES `powb_ind_assesment_risk` (`id`);

ALTER TABLE `crp_milestones` ADD FOREIGN KEY (`powb_ind_milestone_risk_id`) REFERENCES `powb_ind_milestone_risks` (`id`);

ALTER TABLE `crp_milestones` ADD FOREIGN KEY (`rep_ind_gender_focus_level_id`) REFERENCES `rep_ind_gender_youth_focus_levels` (`id`);

ALTER TABLE `crp_milestones` ADD FOREIGN KEY (`rep_ind_youth_focus_level_id`) REFERENCES `rep_ind_gender_youth_focus_levels` (`id`);

ALTER TABLE `crp_milestones` ADD FOREIGN KEY (`rep_ind_capdev_focus_level_id`) REFERENCES `rep_ind_gender_youth_focus_levels` (`id`);

ALTER TABLE `crp_milestones` ADD FOREIGN KEY (`rep_ind_climate_focus_level_id`) REFERENCES `rep_ind_gender_youth_focus_levels` (`id`);

