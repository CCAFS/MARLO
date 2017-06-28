SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for crps
-- ----------------------------
DROP TABLE IF EXISTS `crps`;
CREATE TABLE `crps` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` text NOT NULL,
  `acronym` varchar(50) NOT NULL,
  `is_active` tinyint(1) NOT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `active_since` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified_by` bigint(20) NOT NULL,
  `modification_justification` text NOT NULL,
  `is_marlo` tinyint(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_crps_created_by_users_id` (`created_by`) USING BTREE,
  KEY `fk_crps_modified_by_users_id` (`modified_by`) USING BTREE,
  CONSTRAINT `crps_ibfk_1` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `crps_ibfk_2` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of crps
-- ----------------------------
INSERT INTO `crps` VALUES ('1', 'CCAFS', 'ccafs', '1', null, '2016-06-07 19:08:48', '1', '', '1');
INSERT INTO `crps` VALUES ('3', 'PIM', 'pim', '1', null, '2016-06-07 19:08:48', '1', '', '1');
INSERT INTO `crps` VALUES ('4', 'WLE', 'wle', '1', null, '2016-06-07 19:08:48', '1', '', '1');
INSERT INTO `crps` VALUES ('5', 'A4NH', 'a4nh', '1', null, '2016-06-07 19:08:48', '1', '', '1');
INSERT INTO `crps` VALUES ('7', 'Livestock', 'livestock', '1', '3', '2016-08-05 19:30:00', '3', '  ', '1');
INSERT INTO `crps` VALUES ('8', 'Aquatic Agricultural Systems', '', '1', '3', '2016-08-31 14:59:39', '3', '', '0');
INSERT INTO `crps` VALUES ('9', 'Dryland Cereals', '', '1', '3', '2016-08-31 14:59:54', '3', '', '0');
INSERT INTO `crps` VALUES ('10', 'Dryland Systems', '', '1', '3', '2016-08-31 15:00:07', '3', '', '0');
INSERT INTO `crps` VALUES ('11', 'Forests, Trees and Agroforestry', 'FTA', '1', '3', '2016-08-31 15:00:27', '3', '', '0');
INSERT INTO `crps` VALUES ('12', 'Grain Legumes', '', '1', '3', '2016-08-31 15:00:47', '3', '', '0');
INSERT INTO `crps` VALUES ('13', 'Integrated Systems for the Humid Tropics', '', '1', '3', '2016-08-31 15:03:19', '3', '', '0');
INSERT INTO `crps` VALUES ('14', 'Policies, Institutions and Markets', '', '1', '3', '2016-08-31 15:03:45', '3', '', '0');
INSERT INTO `crps` VALUES ('15', 'Maize', '', '1', '3', '2016-08-31 15:04:16', '3', '', '0');
INSERT INTO `crps` VALUES ('16', 'Rice', 'rice_grisp', '1', '3', '2016-08-31 15:05:33', '3', '', '0');
INSERT INTO `crps` VALUES ('17', 'Roots, Tubers and Bananas', 'RTB', '1', '3', '2016-08-31 15:07:33', '3', '', '0');
INSERT INTO `crps` VALUES ('18', 'Water, Land and Ecosystems', 'WLE', '1', '3', '2016-08-31 15:07:46', '3', '', '0');
INSERT INTO `crps` VALUES ('19', 'Wheat', '', '1', '3', '2016-08-31 15:07:54', '3', '', '0');
INSERT INTO `crps` VALUES ('20', 'Genebank', '', '1', '3', '2016-08-31 15:08:13', '3', '', '0');
INSERT INTO `crps` VALUES ('21', 'Wheat', 'wheat', '1', '3', '2016-12-16 14:46:23', '3', ' ', '1');
INSERT INTO `crps` VALUES ('22', 'Maize', 'maize', '1', '3', '2016-12-16 14:46:23', '3', ' ', '1');
