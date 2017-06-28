SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for srf_idos
-- ----------------------------
DROP TABLE IF EXISTS `srf_idos`;
CREATE TABLE `srf_idos` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` text NOT NULL,
  `is_cross_cutting` tinyint(1) NOT NULL,
  `cross_cutting_issue` bigint(20) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `active_since` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified_by` bigint(20) NOT NULL,
  `modification_justification` text NOT NULL,
  PRIMARY KEY (`id`),
  KEY `cross_cutting_issue` (`cross_cutting_issue`) USING BTREE,
  KEY `fk_srf_idos_created_by_users_id` (`created_by`) USING BTREE,
  KEY `fk_srf_idos_modified_by_users_id` (`modified_by`) USING BTREE,
  CONSTRAINT `srf_idos_ibfk_1` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `srf_idos_ibfk_2` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`),
  CONSTRAINT `srf_idos_ibfk_3` FOREIGN KEY (`cross_cutting_issue`) REFERENCES `srf_cross_cutting_issues` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of srf_idos
-- ----------------------------
INSERT INTO `srf_idos` VALUES ('1', 'Increased resilience of the poor to climate change and other shocks', '0', null, '1', '1', '2016-06-10 16:13:26', '1', 'Initial Data');
INSERT INTO `srf_idos` VALUES ('2', 'Enhanced smallholder market access', '0', null, '1', '1', '2016-06-10 16:13:26', '1', 'Initial Data');
INSERT INTO `srf_idos` VALUES ('3', 'Increased incomes and employment', '0', null, '1', '1', '2016-06-10 16:13:26', '1', 'Initial Data');
INSERT INTO `srf_idos` VALUES ('4', 'Increased productivity', '0', null, '1', '1', '2016-06-10 16:13:26', '1', 'Initial Data');
INSERT INTO `srf_idos` VALUES ('5', 'Improved diets for poor and vulnerable people', '0', null, '1', '1', '2016-06-10 16:13:26', '1', 'Initial Data');
INSERT INTO `srf_idos` VALUES ('6', 'Improved food safety', '0', null, '1', '1', '2016-06-10 16:13:26', '1', 'Initial Data');
INSERT INTO `srf_idos` VALUES ('7', 'Improved human and animal health through better agricultural practices', '0', null, '1', '1', '2016-06-10 16:13:26', '1', 'Initial Data');
INSERT INTO `srf_idos` VALUES ('8', 'Natural capital enhanced and protected, especially from climate change', '0', null, '1', '1', '2016-06-10 16:13:26', '1', 'Initial Data');
INSERT INTO `srf_idos` VALUES ('9', 'Enhanced benefits from ecosystem goods and services', '0', null, '1', '1', '2016-06-10 16:13:26', '1', 'Initial Data');
INSERT INTO `srf_idos` VALUES ('10', 'More sustainably managed agroecosystems', '0', null, '1', '1', '2016-06-10 16:13:26', '1', 'Initial Data');
INSERT INTO `srf_idos` VALUES ('11', 'Mitigation and adaptation achieved', '1', '1', '1', '1', '2016-06-10 16:13:26', '1', 'Initial Data');
INSERT INTO `srf_idos` VALUES ('12', 'Equity and inclusion achieved', '1', '2', '1', '1', '2016-06-10 16:13:26', '1', 'Initial Data');
INSERT INTO `srf_idos` VALUES ('13', 'Enabling environment improved', '1', '3', '1', '1', '2016-06-10 16:13:26', '1', 'Initial Data');
INSERT INTO `srf_idos` VALUES ('14', 'National partners and beneficiaries enabled', '1', '4', '1', '1', '2016-06-10 16:13:26', '1', 'Initial Data');
