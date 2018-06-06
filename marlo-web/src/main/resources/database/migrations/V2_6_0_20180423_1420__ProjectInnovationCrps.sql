SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for project_innovation_crps
-- ----------------------------
DROP TABLE IF EXISTS `project_innovation_crps`;
CREATE TABLE `project_innovation_crps` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_innovation_id` bigint(20) DEFAULT NULL,
  `id_phase` bigint(20) DEFAULT NULL,
  `global_unit_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `project_innovation_id` (`project_innovation_id`),
  KEY `id_phase` (`id_phase`),
  KEY `global_unit_id` (`global_unit_id`),
  CONSTRAINT `project_innovation_crps_ibfk_1` FOREIGN KEY (`project_innovation_id`) REFERENCES `project_innovations` (`id`),
  CONSTRAINT `project_innovation_crps_ibfk_2` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`),
  CONSTRAINT `project_innovation_crps_ibfk_3` FOREIGN KEY (`global_unit_id`) REFERENCES `global_units` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of project_innovation_crps
-- ----------------------------
