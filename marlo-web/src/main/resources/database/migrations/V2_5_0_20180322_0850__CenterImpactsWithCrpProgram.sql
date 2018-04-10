SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for center_impacts
-- ----------------------------
DROP TABLE IF EXISTS `center_impacts`;
CREATE TABLE `center_impacts` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `description` text NOT NULL,
  `short_name` text,
  `impact_statement_id` bigint(20) DEFAULT NULL,
  `subido_id` bigint(20) DEFAULT NULL,
  `target_year` int(11) DEFAULT NULL,
  `color` varchar(8) DEFAULT NULL,
  `program_id` bigint(20) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL,
  `active_since` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `fk_impact_created_by` (`created_by`) USING BTREE,
  KEY `fk_impact_modified_by` (`modified_by`) USING BTREE,
  KEY `research_impacts_statement_id_fk` (`impact_statement_id`) USING BTREE,
  KEY `research_impacts_subido_id_fk` (`subido_id`) USING BTREE,
  KEY `center_impacts_ibfk_6` (`program_id`),
  CONSTRAINT `center_impacts_ibfk_1` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `center_impacts_ibfk_2` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`),
  CONSTRAINT `center_impacts_ibfk_4` FOREIGN KEY (`impact_statement_id`) REFERENCES `center_impact_statement` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `center_impacts_ibfk_5` FOREIGN KEY (`subido_id`) REFERENCES `srf_sub_idos` (`id`),
  CONSTRAINT `center_impacts_ibfk_6` FOREIGN KEY (`program_id`) REFERENCES `crp_programs` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of center_impacts
-- ----------------------------
INSERT INTO `center_impacts` VALUES ('3', 'Improve food security and nutrition, reduce hunger through increased productivity and rice consumption.', '', null, null, null, null, '135', '1', '2018-03-22 08:44:11', '1320', '1137', null);
INSERT INTO `center_impacts` VALUES ('4', 'Restoration projects improved livelihoods, food and income security, and restore ecosystem services.', '', null, null, null, null, '139', '1', '2018-03-22 08:44:20', '1137', '1137', null);
INSERT INTO `center_impacts` VALUES ('5', 'Improved natural resources and ecosystem services through enhanced benefits from ecosystem goods and services', '', '10', null, null, null, '139', '0', '2018-03-22 08:44:22', '1137', '1542', null);
INSERT INTO `center_impacts` VALUES ('6', 'Productive, resilient and ecologically sustainable food systems that encourage healthy diets', '', null, null, null, null, '136', '1', '2018-03-22 08:45:55', '1137', '1806', null);
INSERT INTO `center_impacts` VALUES ('7', 'Bean-based technologies that exploit the genetic diversity of Phaseolus genus for dynamic markets and consumers, contribute to food and nutrition security, health, and the alleviation of poverty in Sub-Saharan Africa and Latin America.', '', null, null, null, null, '134', '1', '2018-03-22 08:46:02', '1542', '1137', null);
INSERT INTO `center_impacts` VALUES ('8', 'Reduced poverty through increased productivity', '', '4', '10', null, null, '137', '1', '2018-03-22 08:46:11', '1137', '1137', null);
INSERT INTO `center_impacts` VALUES ('9', 'Improved the livelihoods and the human wellbeing in agricultural landscapes through enhanced benefits from ecosystem goods and services', '', null, null, null, null, '132', '1', '2018-03-22 08:46:15', '1137', '1137', null);
INSERT INTO `center_impacts` VALUES ('10', 'Reduced poverty through enhanced smallholder market access', '', '2', '4', null, null, '130', '0', '2018-03-22 08:46:19', '1542', '1137', null);
INSERT INTO `center_impacts` VALUES ('11', 'Reduced poverty through increased resilience of the poor to climate change and other shocks', '', '1', null, null, null, '131', '1', '2018-03-22 08:49:33', '1137', '1137', null);
INSERT INTO `center_impacts` VALUES ('12', 'Improved food and nutrition security for health through improved diets for poor and vulnerable people', '', '6', '15', null, null, '130', '0', '2018-03-22 08:49:35', '1137', '1137', null);
INSERT INTO `center_impacts` VALUES ('13', 'Sustainable and profitable cassava crop production for wealth, food security and income generation.', '', null, null, null, null, '133', '1', '2018-03-22 08:49:40', '1137', '1137', null);
INSERT INTO `center_impacts` VALUES ('16', 'Establishment of efficient, resilient, equitable, and carbon-neutral food systems', '', null, null, null, null, '130', '1', '2018-03-22 08:49:42', '1137', '1137', null);
INSERT INTO `center_impacts` VALUES ('17', 'Improved food and nutrition security for health through improved diets for poor and vulnerable people', 'Healthy diets', '6', '15', null, null, '130', '0', '2018-03-22 08:49:44', '55', '1137', null);
INSERT INTO `center_impacts` VALUES ('18', 'Improved natural resources and ecosystem services through natural capital enhanced and protected, especially from climate change', 'Resilience', '9', '22', null, null, '130', '0', '2018-03-22 08:49:45', '55', '1137', null);
