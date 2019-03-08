SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for report_synthesis_key_partnership_collaborations
-- ----------------------------
DROP TABLE IF EXISTS `report_synthesis_key_partnership_collaborations`;
CREATE TABLE `report_synthesis_key_partnership_collaborations` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `report_synthesis_key_partnership_id` bigint(20) DEFAULT NULL,
  `description` text,
  `value_added` decimal(20,2) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `report_synthesis_key_partnership_id` (`report_synthesis_key_partnership_id`),
  CONSTRAINT `report_synthesis_key_partnership_collaborations_ibfk_1` FOREIGN KEY (`report_synthesis_key_partnership_id`) REFERENCES `report_synthesis_key_partnerships` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of report_synthesis_key_partnership_collaborations
-- ----------------------------
SET FOREIGN_KEY_CHECKS=1;

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for report_synthesis_key_partnership_collaboration_crps
-- ----------------------------
DROP TABLE IF EXISTS `report_synthesis_key_partnership_collaboration_crps`;
CREATE TABLE `report_synthesis_key_partnership_collaboration_crps` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `report_synthesis_key_partnership_collaboration_id` bigint(20) DEFAULT NULL,
  `global_unit_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `report_synthesis_key_partnership_collaboration_id` (`report_synthesis_key_partnership_collaboration_id`),
  KEY `global_unit_id` (`global_unit_id`),
  CONSTRAINT `report_synthesis_key_partnership_collaboration_crps_ibfk_1` FOREIGN KEY (`report_synthesis_key_partnership_collaboration_id`) REFERENCES `report_synthesis_key_partnership_collaborations` (`id`),
  CONSTRAINT `report_synthesis_key_partnership_collaboration_crps_ibfk_2` FOREIGN KEY (`global_unit_id`) REFERENCES `global_units` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of report_synthesis_key_partnership_collaboration_crps
-- ----------------------------
SET FOREIGN_KEY_CHECKS=1;



