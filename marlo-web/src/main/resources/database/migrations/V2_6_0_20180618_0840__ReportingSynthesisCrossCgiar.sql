SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for rep_ind_collaboration_type
-- ----------------------------
DROP TABLE IF EXISTS `rep_ind_collaboration_type`;
CREATE TABLE `rep_ind_collaboration_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of rep_ind_collaboration_type
-- ----------------------------
INSERT INTO `rep_ind_collaboration_type` VALUES ('1', 'Contribution to');
INSERT INTO `rep_ind_collaboration_type` VALUES ('2', 'Service needed from');
INSERT INTO `rep_ind_collaboration_type` VALUES ('3', 'Both');

-----------------------------------------------------------------

-- ----------------------------
-- Table structure for report_synthesis_cross_cgiar
-- ----------------------------
DROP TABLE IF EXISTS `report_synthesis_cross_cgiar`;
CREATE TABLE `report_synthesis_cross_cgiar` (
  `id` bigint(20) NOT NULL,
  `highlights` text,
  `is_active` tinyint(1) NOT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `active_since` timestamp NULL DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `created_by` (`created_by`),
  KEY `modified_by` (`modified_by`),
  CONSTRAINT `report_synthesis_cross_cgiar_ibfk_1` FOREIGN KEY (`id`) REFERENCES `report_synthesis` (`id`),
  CONSTRAINT `report_synthesis_cross_cgiar_ibfk_2` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `report_synthesis_cross_cgiar_ibfk_3` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for report_synthesis_cross_cgiar_collaborations
-- ----------------------------
DROP TABLE IF EXISTS `report_synthesis_cross_cgiar_collaborations`;
CREATE TABLE `report_synthesis_cross_cgiar_collaborations` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `report_synthesis_cross_cgiar_id` bigint(20) DEFAULT NULL,
  `rep_ind_collaboration_type_id` bigint(20) DEFAULT NULL,
  `global_unit_id` bigint(20) DEFAULT NULL,
  `crp_program_id` bigint(20) DEFAULT NULL,
  `status` decimal(1,0) DEFAULT NULL,
  `description` text,
  `is_active` tinyint(1) NOT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  `active_since` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `report_synthesis_cross_cgiar_id` (`report_synthesis_cross_cgiar_id`),
  KEY `global_unit_id` (`global_unit_id`),
  KEY `crp_program_id` (`crp_program_id`),
  KEY `created_by` (`created_by`),
  KEY `modified_by` (`modified_by`),
  KEY `rep_ind_collaboration_type_id` (`rep_ind_collaboration_type_id`),
  CONSTRAINT `report_synthesis_cross_cgiar_collaborations_ibfk_1` FOREIGN KEY (`report_synthesis_cross_cgiar_id`) REFERENCES `report_synthesis_cross_cgiar` (`id`),
  CONSTRAINT `report_synthesis_cross_cgiar_collaborations_ibfk_2` FOREIGN KEY (`global_unit_id`) REFERENCES `global_units` (`id`),
  CONSTRAINT `report_synthesis_cross_cgiar_collaborations_ibfk_3` FOREIGN KEY (`crp_program_id`) REFERENCES `crp_programs` (`id`),
  CONSTRAINT `report_synthesis_cross_cgiar_collaborations_ibfk_4` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `report_synthesis_cross_cgiar_collaborations_ibfk_5` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`),
  CONSTRAINT `report_synthesis_cross_cgiar_collaborations_ibfk_6` FOREIGN KEY (`rep_ind_collaboration_type_id`) REFERENCES `rep_ind_collaboration_type` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
