SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for deliverable_cross_cutting_markers
-- ----------------------------
DROP TABLE IF EXISTS `deliverable_cross_cutting_markers`;
CREATE TABLE `deliverable_cross_cutting_markers` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `deliverable_id` bigint(20) DEFAULT NULL,
  `id_phase` bigint(20) DEFAULT NULL,
  `cgiar_cross_cutting_marker_id` bigint(20) DEFAULT NULL,
  `rep_ind_gender_youth_focus_level_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `deliverable_id` (`deliverable_id`),
  KEY `id_phase` (`id_phase`),
  KEY `cgiar_cross_cutting_marker_id` (`cgiar_cross_cutting_marker_id`),
  KEY `rep_ind_gender_youth_focus_level_id` (`rep_ind_gender_youth_focus_level_id`),
  CONSTRAINT `deliverable_cross_cutting_markers_ibfk_1` FOREIGN KEY (`deliverable_id`) REFERENCES `deliverables` (`id`),
  CONSTRAINT `deliverable_cross_cutting_markers_ibfk_2` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`),
  CONSTRAINT `deliverable_cross_cutting_markers_ibfk_3` FOREIGN KEY (`cgiar_cross_cutting_marker_id`) REFERENCES `cgiar_cross_cutting_markers` (`id`),
  CONSTRAINT `deliverable_cross_cutting_markers_ibfk_4` FOREIGN KEY (`rep_ind_gender_youth_focus_level_id`) REFERENCES `rep_ind_gender_youth_focus_levels` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of deliverable_cross_cutting_markers
-- ----------------------------
SET FOREIGN_KEY_CHECKS=1;
