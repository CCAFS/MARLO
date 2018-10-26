ALTER TABLE `crp_milestones` DROP FOREIGN KEY `crp_milestones_ibfk_1`;

ALTER TABLE `crp_milestones` DROP FOREIGN KEY `crp_milestones_ibfk_10`;

ALTER TABLE `crp_milestones` DROP FOREIGN KEY `crp_milestones_ibfk_11`;

ALTER TABLE `crp_milestones` DROP FOREIGN KEY `crp_milestones_ibfk_2`;

ALTER TABLE `crp_milestones` DROP FOREIGN KEY `crp_milestones_ibfk_3`;

ALTER TABLE `crp_milestones` DROP FOREIGN KEY `crp_milestones_ibfk_4`;

ALTER TABLE `crp_milestones` DROP FOREIGN KEY `crp_milestones_ibfk_5`;

ALTER TABLE `crp_milestones` DROP FOREIGN KEY `crp_milestones_ibfk_6`;

ALTER TABLE `crp_milestones` DROP FOREIGN KEY `crp_milestones_ibfk_7`;

ALTER TABLE `crp_milestones` DROP FOREIGN KEY `crp_milestones_ibfk_8`;

ALTER TABLE `crp_milestones` DROP FOREIGN KEY `crp_milestones_ibfk_9`;

ALTER TABLE `crp_milestones` ADD CONSTRAINT `crp_milestones_ibfk_1` FOREIGN KEY (`target_unit_id`) REFERENCES `srf_target_units` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `crp_milestones` ADD CONSTRAINT `crp_milestones_ibfk_10` FOREIGN KEY (`rep_ind_capdev_focus_level_id`) REFERENCES `rep_ind_gender_youth_focus_levels` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `crp_milestones` ADD CONSTRAINT `crp_milestones_ibfk_11` FOREIGN KEY (`rep_ind_climate_focus_level_id`) REFERENCES `rep_ind_gender_youth_focus_levels` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `crp_milestones` ADD CONSTRAINT `crp_milestones_ibfk_2` FOREIGN KEY (`crp_program_outcome_id`) REFERENCES `crp_program_outcomes` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `crp_milestones` ADD CONSTRAINT `crp_milestones_ibfk_3` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `crp_milestones` ADD CONSTRAINT `crp_milestones_ibfk_4` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `crp_milestones` ADD CONSTRAINT `crp_milestones_ibfk_5` FOREIGN KEY (`powb_ind_following_milestone_id`) REFERENCES `powb_ind_following_milestones` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `crp_milestones` ADD CONSTRAINT `crp_milestones_ibfk_6` FOREIGN KEY (`powb_ind_assesment_risk_id`) REFERENCES `powb_ind_assesment_risk` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `crp_milestones` ADD CONSTRAINT `crp_milestones_ibfk_7` FOREIGN KEY (`powb_ind_milestone_risk_id`) REFERENCES `powb_ind_milestone_risks` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `crp_milestones` ADD CONSTRAINT `crp_milestones_ibfk_8` FOREIGN KEY (`rep_ind_gender_focus_level_id`) REFERENCES `rep_ind_gender_youth_focus_levels` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `crp_milestones` ADD CONSTRAINT `crp_milestones_ibfk_9` FOREIGN KEY (`rep_ind_youth_focus_level_id`) REFERENCES `rep_ind_gender_youth_focus_levels` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

