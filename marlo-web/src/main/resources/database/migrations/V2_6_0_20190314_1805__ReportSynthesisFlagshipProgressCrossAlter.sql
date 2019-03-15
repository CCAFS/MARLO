SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for report_synthesis_flagship_progress_cross
-- ----------------------------
DROP TABLE IF EXISTS `report_synthesis_flagship_progress_cross`;
CREATE TABLE `report_synthesis_flagship_progress_cross` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `justification` text,
  `report_synthesis_flagship_progress_outcome_milestone_id` bigint(20) DEFAULT NULL,
  `cgiar_cross_cutting_marker_id` bigint(20) DEFAULT NULL,
  `rep_ind_gender_youth_focus_level_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `report_synthesis_flagship_progress_milestone_id` (`report_synthesis_flagship_progress_outcome_milestone_id`),
  KEY `cgiar_cross_cutting_marker_id` (`cgiar_cross_cutting_marker_id`),
  KEY `rep_ind_gender_youth_focus_level_id` (`rep_ind_gender_youth_focus_level_id`),
  CONSTRAINT `report_synthesis_flagship_progress_cross_ibfk_2` FOREIGN KEY (`cgiar_cross_cutting_marker_id`) REFERENCES `cgiar_cross_cutting_markers` (`id`),
  CONSTRAINT `report_synthesis_flagship_progress_cross_ibfk_3` FOREIGN KEY (`rep_ind_gender_youth_focus_level_id`) REFERENCES `rep_ind_gender_youth_focus_levels` (`id`),
  CONSTRAINT `report_synthesis_flagship_progress_cross_ibfk_4` FOREIGN KEY (`report_synthesis_flagship_progress_outcome_milestone_id`) REFERENCES `report_synthesis_flagship_progress_outcome_milestones` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of report_synthesis_flagship_progress_cross
-- ----------------------------
SET FOREIGN_KEY_CHECKS=1;
