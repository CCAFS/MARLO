SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for report_synthesis_key_partnerships
-- ----------------------------
DROP TABLE IF EXISTS `report_synthesis_key_partnerships`;
CREATE TABLE `report_synthesis_key_partnerships` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `summary` text,
  `is_active` tinyint(1) NOT NULL,
  `active_since` timestamp NULL DEFAULT NULL,
  `created_by` bigint(20) NOT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `created_by` (`created_by`),
  KEY `modified_by` (`modified_by`),
  CONSTRAINT `report_synthesis_key_partnerships_ibfk_1` FOREIGN KEY (`id`) REFERENCES `report_synthesis` (`id`),
  CONSTRAINT `report_synthesis_key_partnerships_ibfk_2` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `report_synthesis_key_partnerships_ibfk_3` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of report_synthesis_key_partnerships
-- ----------------------------
SET FOREIGN_KEY_CHECKS=1;


SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for rep_ind_partnership_main_area
-- ----------------------------
DROP TABLE IF EXISTS `rep_ind_partnership_main_area`;
CREATE TABLE `rep_ind_partnership_main_area` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of rep_ind_partnership_main_area
-- ----------------------------
INSERT INTO `rep_ind_partnership_main_area` VALUES ('1', 'Research');
INSERT INTO `rep_ind_partnership_main_area` VALUES ('2', 'Delivery');
INSERT INTO `rep_ind_partnership_main_area` VALUES ('3', 'Policy');
INSERT INTO `rep_ind_partnership_main_area` VALUES ('4', 'Capacity');
INSERT INTO `rep_ind_partnership_main_area` VALUES ('5', 'Development');
INSERT INTO `rep_ind_partnership_main_area` VALUES ('6', 'Other');
SET FOREIGN_KEY_CHECKS=1;



SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for report_synthesis_key_partnership_external
-- ----------------------------
DROP TABLE IF EXISTS `report_synthesis_key_partnership_external`;
CREATE TABLE `report_synthesis_key_partnership_external` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `report_synthesis_key_partnership_id` bigint(20) DEFAULT NULL,
  `description` text,
  `other_partnership_main_area` text,
  `file` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `report_synthesis_key_partnership_id` (`report_synthesis_key_partnership_id`),
  KEY `file` (`file`),
  CONSTRAINT `report_synthesis_key_partnership_external_ibfk_1` FOREIGN KEY (`report_synthesis_key_partnership_id`) REFERENCES `report_synthesis_key_partnerships` (`id`),
  CONSTRAINT `report_synthesis_key_partnership_external_ibfk_2` FOREIGN KEY (`file`) REFERENCES `files` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of report_synthesis_key_partnership_external
-- ----------------------------
SET FOREIGN_KEY_CHECKS=1;



SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for report_synthesis_key_partnership_external_main_areas
-- ----------------------------
DROP TABLE IF EXISTS `report_synthesis_key_partnership_external_main_areas`;
CREATE TABLE `report_synthesis_key_partnership_external_main_areas` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `report_synthesis_key_partnership_external_id` bigint(20) DEFAULT NULL,
  `rep_ind_partnership_main_area_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `report_synthesis_key_partnership_external_id` (`report_synthesis_key_partnership_external_id`),
  KEY `rep_ind_partnership_main_area_id` (`rep_ind_partnership_main_area_id`),
  CONSTRAINT `report_synthesis_key_partnership_external_main_areas_ibfk_1` FOREIGN KEY (`report_synthesis_key_partnership_external_id`) REFERENCES `report_synthesis_key_partnership_external` (`id`),
  CONSTRAINT `report_synthesis_key_partnership_external_main_areas_ibfk_2` FOREIGN KEY (`rep_ind_partnership_main_area_id`) REFERENCES `rep_ind_partnership_main_area` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of report_synthesis_key_partnership_external_main_areas
-- ----------------------------
SET FOREIGN_KEY_CHECKS=1;

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for report_synthesis_key_partnership_external_institutions
-- ----------------------------
DROP TABLE IF EXISTS `report_synthesis_key_partnership_external_institutions`;
CREATE TABLE `report_synthesis_key_partnership_external_institutions` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `report_synthesis_key_partnership_external_id` bigint(20) DEFAULT NULL,
  `institution_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `report_synthesis_key_partnership_external_id` (`report_synthesis_key_partnership_external_id`),
  KEY `institution_id` (`institution_id`),
  CONSTRAINT `report_synthesis_key_partnership_external_institutions_ibfk_1` FOREIGN KEY (`report_synthesis_key_partnership_external_id`) REFERENCES `report_synthesis_key_partnership_external` (`id`),
  CONSTRAINT `report_synthesis_key_partnership_external_institutions_ibfk_2` FOREIGN KEY (`institution_id`) REFERENCES `institutions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of report_synthesis_key_partnership_external_institutions
-- ----------------------------
SET FOREIGN_KEY_CHECKS=1;

