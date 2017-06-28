SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for beneficiaries
-- ----------------------------
DROP TABLE IF EXISTS `beneficiaries`;
CREATE TABLE `beneficiaries` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` text NOT NULL,
  `beneficiary_type_id` int(11) NOT NULL,
  `is_active` tinyint(1) NOT NULL,
  `active_since` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `beneficiary_type_fk` (`beneficiary_type_id`),
  KEY `beneficiary_created_by_fk` (`created_by`),
  KEY `beneficiary_modified_by` (`modified_by`),
  CONSTRAINT `beneficiary_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `beneficiary_modified_by` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`),
  CONSTRAINT `beneficiary_type_fk` FOREIGN KEY (`beneficiary_type_id`) REFERENCES `beneficiary_types` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of beneficiaries
-- ----------------------------
INSERT INTO `beneficiaries` VALUES ('1', 'Women', '1', '1', '2016-11-24 10:31:35', '3', '3', ' ');
INSERT INTO `beneficiaries` VALUES ('2', 'Youth', '1', '1', '2016-11-24 10:31:46', '3', '3', ' ');
INSERT INTO `beneficiaries` VALUES ('3', 'Victims of Violence', '1', '1', '2016-11-24 10:32:46', '3', '3', ' ');
INSERT INTO `beneficiaries` VALUES ('4', 'Victims of Natural Disasters *incl. CC*', '1', '1', '2016-11-24 10:32:57', '3', '3', ' ');
INSERT INTO `beneficiaries` VALUES ('5', 'Displaced/ Landless populations', '1', '1', '2016-11-24 10:33:10', '3', '3', ' ');
INSERT INTO `beneficiaries` VALUES ('6', 'Tribal or Indigenous', '1', '1', '2016-11-24 10:33:27', '3', '3', ' ');
INSERT INTO `beneficiaries` VALUES ('7', 'Malnourished children and/or mothers', '1', '1', '2016-11-24 10:33:34', '3', '3', ' ');
INSERT INTO `beneficiaries` VALUES ('8', 'Impoverished Rural', '1', '1', '2016-11-24 10:33:45', '3', '3', ' ');
INSERT INTO `beneficiaries` VALUES ('9', 'Smallholders', '1', '1', '2016-11-24 10:33:55', '3', '3', ' ');
INSERT INTO `beneficiaries` VALUES ('10', 'Impoverished Urban', '1', '1', '2016-11-24 11:04:43', '3', '3', ' ');
INSERT INTO `beneficiaries` VALUES ('11', 'Suppliers', '1', '1', '2016-11-24 11:04:55', '3', '3', ' ');
INSERT INTO `beneficiaries` VALUES ('12', 'Producers', '1', '1', '2016-11-24 11:05:08', '3', '3', ' ');
INSERT INTO `beneficiaries` VALUES ('13', 'Community Members', '1', '1', '2016-11-24 11:05:19', '3', '3', ' ');
INSERT INTO `beneficiaries` VALUES ('14', 'Rural', '2', '1', '2016-11-24 11:05:35', '3', '3', ' ');
INSERT INTO `beneficiaries` VALUES ('15', 'Urban', '2', '1', '2016-11-24 11:05:42', '3', '3', ' ');
INSERT INTO `beneficiaries` VALUES ('16', 'Peri-Urban', '2', '1', '2016-11-24 11:05:53', '3', '3', ' ');
INSERT INTO `beneficiaries` VALUES ('17', 'Food Systems (Crops, Forages, Livestock)', '3', '1', '2016-11-24 11:06:12', '3', '3', ' ');
INSERT INTO `beneficiaries` VALUES ('18', 'Water', '3', '1', '2016-11-24 11:06:24', '3', '3', ' ');
INSERT INTO `beneficiaries` VALUES ('19', 'Soil', '3', '1', '2016-11-24 11:06:34', '3', '3', ' ');
INSERT INTO `beneficiaries` VALUES ('20', 'Air', '3', '1', '2016-11-24 11:06:42', '3', '3', ' ');
