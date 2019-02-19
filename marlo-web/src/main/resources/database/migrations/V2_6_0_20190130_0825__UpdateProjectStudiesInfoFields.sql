ALTER TABLE `project_expected_study_info`
ADD COLUMN `evidence_tag_id`  bigint(20) NULL AFTER `status`,
ADD COLUMN `outcome_story`  text NULL AFTER `outcome_impact_statement`,
ADD COLUMN `is_srf_target`  tinyint(1) NULL AFTER `is_contribution`,
ADD COLUMN `cgiar_innovation`  text NULL AFTER `rep_ind_region_id`,
ADD COLUMN `other_cross_cutting_selection`  text NULL AFTER `capdev_focus_level_id`,
ADD COLUMN `is_public`  tinyint(1) NULL AFTER `commissioning_study`;

ALTER TABLE `project_expected_study_info` ADD FOREIGN KEY (`evidence_tag_id`) REFERENCES `evidence_tags` (`id`);

