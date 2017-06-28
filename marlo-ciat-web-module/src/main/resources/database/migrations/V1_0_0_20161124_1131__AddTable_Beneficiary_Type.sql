SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for beneficiary_types
-- ----------------------------
DROP TABLE IF EXISTS `beneficiary_types`;
CREATE TABLE `beneficiary_types` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` text NOT NULL,
  `is_active` tinyint(1) NOT NULL,
  `active_since` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `ben_type_creted_by` (`created_by`),
  KEY `ben_type_modified_by` (`modified_by`),
  CONSTRAINT `ben_type_creted_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `ben_type_modified_by` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of beneficiary_types
-- ----------------------------
INSERT INTO `beneficiary_types` VALUES ('1', 'Individuals', '1', '2016-11-24 10:21:24', '3', '3', ' ');
INSERT INTO `beneficiary_types` VALUES ('2', 'Farmers', '1', '2016-11-24 10:21:43', '3', '3', ' ');
INSERT INTO `beneficiary_types` VALUES ('3', 'Environment', '1', '2016-11-24 10:22:22', '3', '3', null);
