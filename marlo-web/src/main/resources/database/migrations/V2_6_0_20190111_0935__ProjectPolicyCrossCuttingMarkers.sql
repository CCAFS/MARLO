SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for project_policy_cross_cutting_markers
-- ----------------------------
DROP TABLE IF EXISTS `project_policy_cross_cutting_markers`;
CREATE TABLE `project_policy_cross_cutting_markers` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_policy_id` bigint(20) DEFAULT NULL,
  `id_phase` bigint(20) DEFAULT NULL,
  `cgiar_cross_cutting_marker_id` bigint(20) DEFAULT NULL,
  `rep_ind_gender_youth_focus_level_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `project_policy_id` (`project_policy_id`),
  KEY `id_phase` (`id_phase`),
  KEY `cgiar_cross_cutting_marker_id` (`cgiar_cross_cutting_marker_id`),
  KEY `rep_ind_gender_youth_focus_level_id` (`rep_ind_gender_youth_focus_level_id`),
  CONSTRAINT `project_policy_cross_cutting_markers_ibfk_1` FOREIGN KEY (`project_policy_id`) REFERENCES `project_policies` (`id`),
  CONSTRAINT `project_policy_cross_cutting_markers_ibfk_2` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`),
  CONSTRAINT `project_policy_cross_cutting_markers_ibfk_3` FOREIGN KEY (`cgiar_cross_cutting_marker_id`) REFERENCES `cgiar_cross_cutting_markers` (`id`),
  CONSTRAINT `project_policy_cross_cutting_markers_ibfk_4` FOREIGN KEY (`rep_ind_gender_youth_focus_level_id`) REFERENCES `rep_ind_gender_youth_focus_levels` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of project_policy_cross_cutting_markers
-- ----------------------------
SET FOREIGN_KEY_CHECKS=1;
