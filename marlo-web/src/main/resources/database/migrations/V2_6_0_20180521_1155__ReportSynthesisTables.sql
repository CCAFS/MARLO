SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for srf_slo_indicator_targets
-- ----------------------------
DROP TABLE IF EXISTS `srf_slo_indicator_targets`;
CREATE TABLE `srf_slo_indicator_targets` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `indicador_id` bigint(20) NOT NULL,
  `narrative` text,
  `value` decimal(10,2) DEFAULT NULL,
  `year` int(11) NOT NULL,
  `target_unit_id` bigint(20) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `active_since` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `target_unit_id` (`target_unit_id`) USING BTREE,
  KEY `indicador_id` (`indicador_id`) USING BTREE,
  KEY `fk_srf_slo_indicator_targets_created_by_users_id` (`created_by`) USING BTREE,
  KEY `fk_srf_slo_indicator_targets_modified_by_users_id` (`modified_by`) USING BTREE,
  CONSTRAINT `srf_slo_indicator_targets_ibfk_1` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `srf_slo_indicator_targets_ibfk_2` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`),
  CONSTRAINT `srf_slo_indicator_targets_ibfk_3` FOREIGN KEY (`target_unit_id`) REFERENCES `srf_target_units` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `srf_slo_indicator_targets_ibfk_4` FOREIGN KEY (`indicador_id`) REFERENCES `srf_slo_indicators` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of srf_slo_indicator_targets
-- ----------------------------
INSERT INTO `srf_slo_indicator_targets` VALUES ('1', '1', '100 million more farm\r\nhouseholds have adopted\r\nimproved varieties, breeds, trees,\r\nand/or management practices.', null, '2022', null, '1', '3', '2018-05-21 09:21:38', '3', null);
INSERT INTO `srf_slo_indicator_targets` VALUES ('2', '2', '30 million people, of which\r\n50% are women, assisted to exit \r\npoverty', null, '2022', null, '1', '3', '2018-05-21 09:45:58', '3', null);
INSERT INTO `srf_slo_indicator_targets` VALUES ('3', '3', 'Improve the rate of yield\r\nincrease for major food staples\r\nfrom current &lt;1% to 1.2-1.5% per\r\nyear', null, '2022', null, '1', '3', '2018-05-21 10:10:17', '3', null);
INSERT INTO `srf_slo_indicator_targets` VALUES ('4', '4', '30 million more people, of which 50% are women, meeting minimum dietary energy requirements', null, '2022', null, '1', '3', '2018-05-21 10:11:51', '3', null);
INSERT INTO `srf_slo_indicator_targets` VALUES ('5', '5', '150 million more people, of which 50% are women, without deficiencies of one or more of the following essential micronutrients: iron, zinc, iodine, vitamin A, folate, and vitamin B12', null, '2022', null, '1', '3', '2018-05-21 10:12:07', '3', null);
INSERT INTO `srf_slo_indicator_targets` VALUES ('6', '6', '10% reduction in women of reproductive age who are consuming less than the adequate number of food groups', null, '2022', null, '1', '3', '2018-05-21 10:12:22', '3', null);
INSERT INTO `srf_slo_indicator_targets` VALUES ('7', '7', '5% increase in water and nutrient (inorganic, biological) use efficiency in agro-ecosystems, including through recycling and reuse', null, '2022', null, '1', '3', '2018-05-21 10:13:08', '3', null);
INSERT INTO `srf_slo_indicator_targets` VALUES ('8', '8', 'Reduce agriculturally-related greenhouse gas emissions by 0.2 Gt CO2-e yr–1 (5%) compared with business-as-usual scenario in 2022', null, '2022', null, '1', '3', '2018-05-21 10:13:24', '3', null);
INSERT INTO `srf_slo_indicator_targets` VALUES ('9', '9', '55 million hectares (ha) degraded land area restored', null, '2022', null, '1', '3', '2018-05-21 10:13:38', '3', null);
INSERT INTO `srf_slo_indicator_targets` VALUES ('10', '10', '2.5 million ha of forest saved from deforestation', null, '2022', null, '1', '3', '2018-05-21 10:13:51', '3', null);
INSERT INTO `srf_slo_indicator_targets` VALUES ('11', '1', '350 million more farm households have adopted improved varieties, breeds or\r\ntrees, and/or improved management practice.\r\n', null, '2030', null, '1', '3', '2018-05-21 10:14:15', '3', null);
INSERT INTO `srf_slo_indicator_targets` VALUES ('12', '2', '100 million people, of which 50% are women, assisted to exit poverty', null, '2030', null, '1', '3', '2018-05-21 10:14:46', '3', null);
INSERT INTO `srf_slo_indicator_targets` VALUES ('13', '3', 'Improve the rate of yield increase for major food staples from current <2.0 to 2.5%/year', null, '2030', null, '1', '3', '2018-05-21 10:14:59', '3', null);
INSERT INTO `srf_slo_indicator_targets` VALUES ('14', '4', '150 million more people, of which 50% are women, meeting minimum dietary energy requirements', null, '2030', null, '1', '3', '2018-05-21 10:15:12', '3', null);
INSERT INTO `srf_slo_indicator_targets` VALUES ('15', '5', '500 million more people, of which 50% are women, without deficiencies of one or more of the following essential micronutrients: iron, zinc, iodine, vitamin A, folate, and vitamin B12', null, '2030', null, '1', '3', '2018-05-21 10:15:25', '3', null);
INSERT INTO `srf_slo_indicator_targets` VALUES ('16', '6', '33% reduction in women of reproductive age who are consuming less than the adequate number of food groups', null, '2030', null, '1', '3', '2018-05-21 10:15:38', '3', null);
INSERT INTO `srf_slo_indicator_targets` VALUES ('17', '7', '20% increase in water and nutrient (inorganic, biological) use efficiency in agroecosystems, including through recycling and reuse', null, '2030', null, '1', '3', '2018-05-21 10:15:56', '3', null);
INSERT INTO `srf_slo_indicator_targets` VALUES ('18', '8', 'Reduce agriculturally-related greenhouse gas emissions by 0.8 Gt CO2-e yr–1 (15%) compared with a business-as-usual scenario in 2030', null, '2030', null, '1', '3', '2018-05-21 10:16:42', '3', null);
INSERT INTO `srf_slo_indicator_targets` VALUES ('19', '9', '190 million ha degraded land area restored', null, '2030', null, '1', '3', '2018-05-21 10:16:56', '3', null);
INSERT INTO `srf_slo_indicator_targets` VALUES ('20', '10', '7.5 million ha of forest saved from deforestation', null, '2030', null, '1', '3', '2018-05-21 10:17:11', '3', null);

--------------------
-- ----------------------------
-- Table structure for report_synthesis
-- ----------------------------
DROP TABLE IF EXISTS `report_synthesis`;
CREATE TABLE `report_synthesis` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `id_phase` bigint(20) NOT NULL,
  `liaison_institution_id` bigint(20) NOT NULL,
  `is_active` tinyint(1) NOT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `active_since` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `synthesis_phase_fk` (`id_phase`),
  KEY `synthesis_liaison_fk` (`liaison_institution_id`),
  KEY `synthesis_created_fk` (`created_by`),
  KEY `synthesis_modified_fk` (`modified_by`),
  CONSTRAINT `synthesis_created_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `synthesis_liaison_fk` FOREIGN KEY (`liaison_institution_id`) REFERENCES `liaison_institutions` (`id`),
  CONSTRAINT `synthesis_modified_fk` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`),
  CONSTRAINT `synthesis_phase_fk` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for report_synthesis_crp_progress
-- ----------------------------
DROP TABLE IF EXISTS `report_synthesis_crp_progress`;
CREATE TABLE `report_synthesis_crp_progress` (
  `id` bigint(20) NOT NULL,
  `overall_progress` text,
  `summaries` text,
  `is_active` tinyint(1) NOT NULL,
  `active_since` timestamp NULL DEFAULT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `created_by` (`created_by`),
  KEY `modified_by` (`modified_by`),
  CONSTRAINT `report_synthesis_crp_progress_ibfk_1` FOREIGN KEY (`id`) REFERENCES `report_synthesis` (`id`),
  CONSTRAINT `report_synthesis_crp_progress_ibfk_2` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `report_synthesis_crp_progress_ibfk_3` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for report_synthesis_crp_progress_studies
-- ----------------------------
DROP TABLE IF EXISTS `report_synthesis_crp_progress_studies`;
CREATE TABLE `report_synthesis_crp_progress_studies` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `report_synhtesis_crp_progress_id` bigint(20) DEFAULT NULL,
  `project_expected_studies_id` bigint(20) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `active_since` timestamp NULL DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `report_synhtesis_crp_progress_id` (`report_synhtesis_crp_progress_id`),
  KEY `project_expected_studies_id` (`project_expected_studies_id`),
  KEY `created_by` (`created_by`),
  KEY `modified_by` (`modified_by`),
  CONSTRAINT `report_synthesis_crp_progress_studies_ibfk_1` FOREIGN KEY (`report_synhtesis_crp_progress_id`) REFERENCES `report_synthesis_crp_progress` (`id`),
  CONSTRAINT `report_synthesis_crp_progress_studies_ibfk_2` FOREIGN KEY (`project_expected_studies_id`) REFERENCES `project_expected_studies` (`id`),
  CONSTRAINT `report_synthesis_crp_progress_studies_ibfk_3` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `report_synthesis_crp_progress_studies_ibfk_4` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for report_synthesis_crp_progress_targets
-- ----------------------------
DROP TABLE IF EXISTS `report_synthesis_crp_progress_targets`;
CREATE TABLE `report_synthesis_crp_progress_targets` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `report_synthesis_crp_progress_id` bigint(20) DEFAULT NULL,
  `srf_slo_indicator_targets_id` bigint(20) DEFAULT NULL,
  `biref_summary` text,
  `additional_contribution` text,
  `is_active` tinyint(1) NOT NULL,
  `active_since` timestamp NULL DEFAULT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `report_synthesis_crp_progress_id` (`report_synthesis_crp_progress_id`),
  KEY `srf_slo_indicator_targets_id` (`srf_slo_indicator_targets_id`),
  KEY `created_by` (`created_by`),
  KEY `modified_by` (`modified_by`),
  CONSTRAINT `report_synthesis_crp_progress_targets_ibfk_1` FOREIGN KEY (`report_synthesis_crp_progress_id`) REFERENCES `report_synthesis_crp_progress` (`id`),
  CONSTRAINT `report_synthesis_crp_progress_targets_ibfk_2` FOREIGN KEY (`srf_slo_indicator_targets_id`) REFERENCES `srf_slo_indicator_targets` (`id`),
  CONSTRAINT `report_synthesis_crp_progress_targets_ibfk_3` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `report_synthesis_crp_progress_targets_ibfk_4` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

