ALTER TABLE `project_expected_study_info`
ADD COLUMN `describe_climate_change`  text NULL AFTER `capdev_focus_level_id`,
ADD COLUMN `climate_change_level_id`  bigint(20) NULL AFTER `describe_climate_change`;

ALTER TABLE `project_expected_study_info` ADD FOREIGN KEY (`climate_change_level_id`) REFERENCES `rep_ind_gender_youth_focus_levels` (`id`);

