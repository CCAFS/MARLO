SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for research_impact_statement
-- ----------------------------
DROP TABLE IF EXISTS `research_impact_statement`;
CREATE TABLE `research_impact_statement` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` text,
  `ido_id` bigint(20) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL,
  `active_since` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `impact_statement_created_fk` (`created_by`),
  KEY `impact_statement_modified_fk` (`modified_by`),
  KEY `impact_statement_ido_fk` (`ido_id`),
  CONSTRAINT `impact_statement_created_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `impact_statement_ido_fk` FOREIGN KEY (`ido_id`) REFERENCES `srf_idos` (`id`),
  CONSTRAINT `impact_statement_modified_fk` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of research_impact_statement
-- ----------------------------
INSERT INTO `research_impact_statement` VALUES ('1', 'Reduced poverty through increased resilience of the poor to climate change and other shocks', '1', '1', '2017-06-14 14:13:52', '3', '3', ' ');
INSERT INTO `research_impact_statement` VALUES ('2', 'Reduced poverty through enhanced smallholder market access', '2', '1', '2017-06-14 14:14:02', '3', '3', ' ');
INSERT INTO `research_impact_statement` VALUES ('3', 'Reduced poverty through increased incomes and employment', '3', '1', '2017-06-14 14:14:06', '3', '3', ' ');
INSERT INTO `research_impact_statement` VALUES ('4', 'Reduced poverty through increased productivity', '4', '1', '2017-06-14 14:15:50', '3', '3', ' ');
INSERT INTO `research_impact_statement` VALUES ('5', 'Improved food and nutrition security for health through increased productivity', '4', '1', '2017-06-14 14:15:52', '3', '3', ' ');
INSERT INTO `research_impact_statement` VALUES ('6', 'Improved food and nutrition security for health through improved diets for poor and vulnerable people', '5', '1', '2017-06-14 14:15:59', '3', '3', ' ');
INSERT INTO `research_impact_statement` VALUES ('7', 'Improved food and nutrition security for health through improved food security', '6', '1', '2017-06-14 14:16:07', '3', '3', ' ');
INSERT INTO `research_impact_statement` VALUES ('8', 'Improved food and nutrition security for health through improved human and animal health through better agricultural practices', '7', '1', '2017-06-14 14:16:12', '3', '3', ' ');
INSERT INTO `research_impact_statement` VALUES ('9', 'Improved natural resources and ecosystem services through natural capital enhanced and protected, especially from climate change', '8', '1', '2017-06-14 14:16:20', '3', '3', ' ');
INSERT INTO `research_impact_statement` VALUES ('10', 'Improved natural resources and ecosystem services through enhanced benefits from ecosystem goods and services', '9', '1', '2017-06-14 14:16:27', '3', '3', ' ');
INSERT INTO `research_impact_statement` VALUES ('11', 'Improved natural resources and ecosystem services through more sustainably managed agro-ecosystems', '10', '1', '2017-06-14 14:16:28', '3', '3', ' ');
