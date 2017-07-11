SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for partner_divisions
-- ----------------------------
DROP TABLE IF EXISTS `partner_divisions`;
CREATE TABLE `partner_divisions` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `acronym` text,
  `name` text,
  `is_active` tinyint(1) NOT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `active_since` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `partner_division_created_fk` (`created_by`),
  KEY `partner_division_modified_fk` (`modified_by`),
  CONSTRAINT `partner_division_created_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `partner_division_modified_fk` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of partner_divisions
-- ----------------------------
INSERT INTO `partner_divisions` VALUES ('1', 'DGO ', 'Director General’s Office', '1', '3', '3', '2017-04-12 15:16:44', ' ');
INSERT INTO `partner_divisions` VALUES ('2', 'DSGD', 'Development Strategy and Governance Division', '1', '3', '3', '2017-04-12 15:17:46', ' ');
INSERT INTO `partner_divisions` VALUES ('3', 'EPTD', 'Environment and Production Technology Division', '1', '3', '3', '2017-04-12 15:18:05', ' ');
INSERT INTO `partner_divisions` VALUES ('4', 'MTID', 'Markets, Trade and Institutions Division', '1', '3', '3', '2017-04-12 15:18:30', ' ');
INSERT INTO `partner_divisions` VALUES ('5', 'PHND', 'Poverty, Health and Nutrition Division', '1', '3', '3', '2017-04-12 15:19:09', ' ');
INSERT INTO `partner_divisions` VALUES ('6', 'SAO', 'South Asia Office', '1', '3', '3', '2017-04-12 15:19:29', ' ');
INSERT INTO `partner_divisions` VALUES ('7', 'WCAO', 'West and Central Africa Office', '1', '3', '3', '2017-04-12 15:19:39', ' ');
INSERT INTO `partner_divisions` VALUES ('8', 'A4NH', 'Agriculture for Nutrition and Health', '1', '3', '3', '2017-04-12 15:19:55', ' ');
INSERT INTO `partner_divisions` VALUES ('9', 'PIM', 'Policies, Institutions, and Markets', '1', '3', '3', '2017-04-12 15:20:10', ' ');
