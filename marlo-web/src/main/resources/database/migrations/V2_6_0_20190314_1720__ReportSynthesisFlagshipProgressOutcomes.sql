SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for report_synthesis_flagship_progress_outcomes
-- ----------------------------
DROP TABLE IF EXISTS `report_synthesis_flagship_progress_outcomes`;
CREATE TABLE `report_synthesis_flagship_progress_outcomes` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `report_synthesis_flagship_progress_id` bigint(20) DEFAULT NULL,
  `crp_outcome_id` bigint(20) DEFAULT NULL,
  `summary` text,
  PRIMARY KEY (`id`),
  KEY `report_synthesis_flagship_progress_id` (`report_synthesis_flagship_progress_id`),
  KEY `crp_outcome_id` (`crp_outcome_id`),
  CONSTRAINT `report_synthesis_flagship_progress_outcomes_ibfk_1` FOREIGN KEY (`report_synthesis_flagship_progress_id`) REFERENCES `report_synthesis_flagship_progress` (`id`),
  CONSTRAINT `report_synthesis_flagship_progress_outcomes_ibfk_2` FOREIGN KEY (`crp_outcome_id`) REFERENCES `crp_program_outcomes` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of report_synthesis_flagship_progress_outcomes
-- ----------------------------
SET FOREIGN_KEY_CHECKS=1;

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for report_synthesis_flagship_progress_outcome_milestones
-- ----------------------------
DROP TABLE IF EXISTS `report_synthesis_flagship_progress_outcome_milestones`;
CREATE TABLE `report_synthesis_flagship_progress_outcome_milestones` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `report_synthesis_outcome_id` bigint(20) DEFAULT NULL,
  `crp_milestone_id` bigint(20) DEFAULT NULL,
  `milestones_status` decimal(10,0) DEFAULT NULL,
  `evidence` text,
  `rep_ind_milestone_reason_id` bigint(20) DEFAULT NULL,
  `other_reason` text,
  `is_active` tinyint(1) NOT NULL,
  `active_since` timestamp NULL DEFAULT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `report_synthesis_flagship_progress_id` (`report_synthesis_outcome_id`),
  KEY `crp_milestone_id` (`crp_milestone_id`),
  KEY `created_by` (`created_by`),
  KEY `modified_by` (`modified_by`),
  KEY `rep_ind_milestone_reason_id` (`rep_ind_milestone_reason_id`),
  CONSTRAINT `report_synthesis_flagship_progress_outcome_milestones_ibfk_1` FOREIGN KEY (`report_synthesis_outcome_id`) REFERENCES `report_synthesis_flagship_progress_outcomes` (`id`),
  CONSTRAINT `report_synthesis_flagship_progress_outcome_milestones_ibfk_2` FOREIGN KEY (`crp_milestone_id`) REFERENCES `crp_milestones` (`id`),
  CONSTRAINT `report_synthesis_flagship_progress_outcome_milestones_ibfk_3` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `report_synthesis_flagship_progress_outcome_milestones_ibfk_4` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`),
  CONSTRAINT `report_synthesis_flagship_progress_outcome_milestones_ibfk_5` FOREIGN KEY (`rep_ind_milestone_reason_id`) REFERENCES `rep_ind_milestone_reasons` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=88 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of report_synthesis_flagship_progress_outcome_milestones
-- ----------------------------
SET FOREIGN_KEY_CHECKS=1;

