SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for centers
-- ----------------------------
DROP TABLE IF EXISTS `centers`;
CREATE TABLE `centers` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'The primary key of the research center table.',
  `name` varchar(100) NOT NULL COMMENT 'The name of the center',
  `acronym` varchar(10) NOT NULL COMMENT 'The Acronym for the center',
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `active_since` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `fk_rcenter_created_by` (`created_by`) USING BTREE,
  KEY `fk_rcenter_modified_by` (`modified_by`) USING BTREE,
  CONSTRAINT `centers_ibfk_1` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `centers_ibfk_2` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of centers
-- ----------------------------
INSERT INTO `centers` VALUES ('1', 'CIAT', 'CIAT', '1', '2016-11-24 08:08:02', null, '3', null);

-- ----------------------------
-- Table structure for center_all_types
-- ----------------------------
DROP TABLE IF EXISTS `center_all_types`;
CREATE TABLE `center_all_types` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `class_name` varchar(45) NOT NULL COMMENT 'The name of the class where this type belongs. e.g ResearchArea, ResearchProgram.',
  `type_name` varchar(100) NOT NULL COMMENT 'The type of the object or record. e.g research area leader, program coordinator, external partiner, internal partiner, research objective, strategic objective.',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 COMMENT='The table that tracks all types of entity data in the system';

-- ----------------------------
-- Records of center_all_types
-- ----------------------------
INSERT INTO `center_all_types` VALUES ('1', 'ResearchProgram', 'REGIONAL_PROGRAM_TYPE');
INSERT INTO `center_all_types` VALUES ('2', 'ResearchProgram', 'FLAGSHIP_PROGRAM_TYPE');
INSERT INTO `center_all_types` VALUES ('4', 'ResearchLeader', 'Program Coordination');
INSERT INTO `center_all_types` VALUES ('5', 'ResearchLeader', 'Research Area Leader');
INSERT INTO `center_all_types` VALUES ('6', 'ResearchLeader', 'Research Program Leader');
INSERT INTO `center_all_types` VALUES ('7', 'ResearchObjective', 'Strategic Objective');
INSERT INTO `center_all_types` VALUES ('8', 'ResearchObjective', 'Research Area Objective');
INSERT INTO `center_all_types` VALUES ('9', 'ResearchPartner', 'Internal Partner');
INSERT INTO `center_all_types` VALUES ('10', 'ResearchPartner', 'External Partner');

-- ----------------------------
-- Table structure for center_areas
-- ----------------------------
DROP TABLE IF EXISTS `center_areas`;
CREATE TABLE `center_areas` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` text NOT NULL COMMENT 'The Name of the research area such as DAPA',
  `acronym` varchar(50) NOT NULL COMMENT 'The short form or acronym of the research area e.g DAPA',
  `color` varchar(8) DEFAULT NULL,
  `research_center_id` int(11) NOT NULL COMMENT 'The foreign key of the research center.',
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `active_since` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `fk_research_area_center` (`research_center_id`) USING BTREE,
  KEY `fk_research_area_created_by` (`created_by`) USING BTREE,
  KEY `fk_research_area_modified_by` (`modified_by`) USING BTREE,
  CONSTRAINT `center_areas_ibfk_1` FOREIGN KEY (`research_center_id`) REFERENCES `centers` (`id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT `center_areas_ibfk_2` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `center_areas_ibfk_3` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of center_areas
-- ----------------------------
INSERT INTO `center_areas` VALUES ('1', 'Decision and Policy Analysis', 'DAPA', '#228051', '1', '1', '2016-12-14 11:05:43', null, '3', null);
INSERT INTO `center_areas` VALUES ('2', 'Agrobiodiversity', 'AgBIO', '#228051', '1', '1', '2016-12-14 11:05:44', null, '3', null);
INSERT INTO `center_areas` VALUES ('3', 'Soils and Landscapes for Sustainability', 'SOILS', '#228051', '1', '1', '2016-12-14 11:05:45', null, '3', null);

-- ----------------------------
-- Table structure for center_beneficiaries
-- ----------------------------
DROP TABLE IF EXISTS `center_beneficiaries`;
CREATE TABLE `center_beneficiaries` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` text NOT NULL,
  `beneficiary_type_id` int(11) NOT NULL,
  `is_active` tinyint(1) NOT NULL,
  `active_since` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `beneficiary_type_fk` (`beneficiary_type_id`) USING BTREE,
  KEY `beneficiary_created_by_fk` (`created_by`) USING BTREE,
  KEY `beneficiary_modified_by` (`modified_by`) USING BTREE,
  CONSTRAINT `center_beneficiaries_ibfk_1` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `center_beneficiaries_ibfk_2` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`),
  CONSTRAINT `center_beneficiaries_ibfk_3` FOREIGN KEY (`beneficiary_type_id`) REFERENCES `center_beneficiary_types` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of center_beneficiaries
-- ----------------------------
INSERT INTO `center_beneficiaries` VALUES ('1', 'Women', '1', '1', '2016-11-24 10:31:35', '3', '3', ' ');
INSERT INTO `center_beneficiaries` VALUES ('2', 'Youth', '1', '1', '2016-11-24 10:31:46', '3', '3', ' ');
INSERT INTO `center_beneficiaries` VALUES ('3', 'Victims of Violence', '1', '1', '2016-11-24 10:32:46', '3', '3', ' ');
INSERT INTO `center_beneficiaries` VALUES ('4', 'Victims of Natural Disasters *incl. CC*', '1', '1', '2016-11-24 10:32:57', '3', '3', ' ');
INSERT INTO `center_beneficiaries` VALUES ('5', 'Displaced/ Landless populations', '1', '1', '2016-11-24 10:33:10', '3', '3', ' ');
INSERT INTO `center_beneficiaries` VALUES ('6', 'Tribal or Indigenous', '1', '1', '2016-11-24 10:33:27', '3', '3', ' ');
INSERT INTO `center_beneficiaries` VALUES ('7', 'Malnourished children and/or mothers', '1', '1', '2016-11-24 10:33:34', '3', '3', ' ');
INSERT INTO `center_beneficiaries` VALUES ('8', 'Impoverished Rural', '1', '1', '2016-11-24 10:33:45', '3', '3', ' ');
INSERT INTO `center_beneficiaries` VALUES ('9', 'Smallholders', '1', '1', '2016-11-24 10:33:55', '3', '3', ' ');
INSERT INTO `center_beneficiaries` VALUES ('10', 'Impoverished Urban', '1', '1', '2016-11-24 11:04:43', '3', '3', ' ');
INSERT INTO `center_beneficiaries` VALUES ('11', 'Suppliers', '1', '1', '2016-11-24 11:04:55', '3', '3', ' ');
INSERT INTO `center_beneficiaries` VALUES ('12', 'Producers', '1', '1', '2016-11-24 11:05:08', '3', '3', ' ');
INSERT INTO `center_beneficiaries` VALUES ('13', 'Community Members', '1', '1', '2016-11-24 11:05:19', '3', '3', ' ');
INSERT INTO `center_beneficiaries` VALUES ('14', 'Rural', '2', '1', '2016-11-24 11:05:35', '3', '3', ' ');
INSERT INTO `center_beneficiaries` VALUES ('15', 'Urban', '2', '1', '2016-11-24 11:05:42', '3', '3', ' ');
INSERT INTO `center_beneficiaries` VALUES ('16', 'Peri-Urban', '2', '1', '2017-05-03 14:26:49', '3', '3', '  ');
INSERT INTO `center_beneficiaries` VALUES ('17', 'Food Systems (Crops, Forages, Livestock)', '3', '1', '2016-11-24 11:06:12', '3', '3', ' ');
INSERT INTO `center_beneficiaries` VALUES ('18', 'Water', '3', '1', '2016-11-24 11:06:24', '3', '3', ' ');
INSERT INTO `center_beneficiaries` VALUES ('19', 'Soil', '3', '1', '2016-11-24 11:06:34', '3', '3', ' ');
INSERT INTO `center_beneficiaries` VALUES ('20', 'Air', '3', '1', '2016-11-24 11:06:42', '3', '3', ' ');
INSERT INTO `center_beneficiaries` VALUES ('21', 'Non Specific', '1', '1', '2017-05-03 14:26:18', '3', '3', ' ');
INSERT INTO `center_beneficiaries` VALUES ('22', 'Non Specific', '2', '1', '2017-05-03 14:26:51', '3', '3', ' ');
INSERT INTO `center_beneficiaries` VALUES ('23', 'Ecosystems', '3', '1', '2017-05-03 14:27:22', '3', '3', ' ');
INSERT INTO `center_beneficiaries` VALUES ('24', 'Non Specific', '3', '1', '2017-05-03 14:27:33', '3', '3', ' ');

-- ----------------------------
-- Table structure for center_beneficiary_types
-- ----------------------------
DROP TABLE IF EXISTS `center_beneficiary_types`;
CREATE TABLE `center_beneficiary_types` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` text NOT NULL,
  `is_active` tinyint(1) NOT NULL,
  `active_since` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `ben_type_creted_by` (`created_by`) USING BTREE,
  KEY `ben_type_modified_by` (`modified_by`) USING BTREE,
  CONSTRAINT `center_beneficiary_types_ibfk_1` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `center_beneficiary_types_ibfk_2` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of center_beneficiary_types
-- ----------------------------
INSERT INTO `center_beneficiary_types` VALUES ('1', 'Individuals', '1', '2016-11-24 10:21:24', '3', '3', ' ');
INSERT INTO `center_beneficiary_types` VALUES ('2', 'Farmers', '1', '2016-11-24 10:21:43', '3', '3', ' ');
INSERT INTO `center_beneficiary_types` VALUES ('3', 'Environment', '1', '2016-11-24 10:22:22', '3', '3', null);

-- ----------------------------
-- Table structure for center_custom_parameters
-- ----------------------------
DROP TABLE IF EXISTS `center_custom_parameters`;
CREATE TABLE `center_custom_parameters` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `parameter_id` bigint(20) NOT NULL,
  `center_id` int(11) NOT NULL,
  `value` varchar(500) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL,
  `active_since` timestamp NULL DEFAULT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `custom_parameters_paramater_id_fk` (`parameter_id`),
  KEY `custom_parameters_center_id_fk` (`center_id`),
  KEY `custom_parameters_created_fk` (`created_by`),
  KEY `custom_parameters_modified_fk` (`modified_by`),
  CONSTRAINT `custom_parameters_center_id_fk` FOREIGN KEY (`center_id`) REFERENCES `centers` (`id`),
  CONSTRAINT `custom_parameters_created_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `custom_parameters_modified_fk` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`),
  CONSTRAINT `custom_parameters_paramater_id_fk` FOREIGN KEY (`parameter_id`) REFERENCES `center_parameters` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of center_custom_parameters
-- ----------------------------
INSERT INTO `center_custom_parameters` VALUES ('1', '1', '1', '2', '1', '2017-05-30 09:09:42', '3', '3', ' ');

-- ----------------------------
-- Table structure for center_cycles
-- ----------------------------
DROP TABLE IF EXISTS `center_cycles`;
CREATE TABLE `center_cycles` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` text,
  `acronym` text,
  `is_active` tinyint(1) NOT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  `active_since` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `cycle_created_by_fk` (`created_by`),
  KEY `cycle_modified_by_fk` (`modified_by`),
  CONSTRAINT `cycle_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `cycle_modified_by_fk` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of center_cycles
-- ----------------------------
INSERT INTO `center_cycles` VALUES ('1', 'Impact Pathway', 'IP', '1', '3', '3', ' ', '2016-12-02 10:57:31');
INSERT INTO `center_cycles` VALUES ('2', 'Monitoring', 'M&E', '1', '3', '3', ' ', '2016-12-02 10:57:50');

-- ----------------------------
-- Table structure for center_deliverables
-- ----------------------------
DROP TABLE IF EXISTS `center_deliverables`;
CREATE TABLE `center_deliverables` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_id` bigint(20) NOT NULL,
  `type_id` bigint(20) DEFAULT NULL,
  `status_id` int(11) DEFAULT NULL,
  `name` text,
  `date_created` timestamp NULL DEFAULT NULL,
  `start_date` timestamp NULL DEFAULT NULL,
  `end_date` timestamp NULL DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `active_since` timestamp NULL DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `deliverable_project_fk` (`project_id`),
  KEY `deliverable_status_fk` (`status_id`),
  KEY `deliverable_type_fk` (`type_id`),
  KEY `deliverable_created_fk` (`created_by`),
  KEY `deliverable_modified_fk` (`modified_by`),
  CONSTRAINT `deliverable_created_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `deliverable_modified_fk` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`),
  CONSTRAINT `deliverable_project_fk` FOREIGN KEY (`project_id`) REFERENCES `center_projects` (`id`),
  CONSTRAINT `deliverable_status_fk` FOREIGN KEY (`status_id`) REFERENCES `center_project_status` (`id`),
  CONSTRAINT `deliverable_type_fk` FOREIGN KEY (`type_id`) REFERENCES `center_deliverable_types` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of center_deliverables
-- ----------------------------
INSERT INTO `center_deliverables` VALUES ('1', '1', null, '2', 'Test Deliverable', '2017-04-03 11:40:46', '2017-04-03 00:00:00', '2018-04-30 00:00:00', '1', '1057', '1057', '2017-04-03 11:40:46', null);
INSERT INTO `center_deliverables` VALUES ('2', '1', null, '2', null, '2017-04-10 15:20:22', '2017-04-10 15:20:22', null, '1', '1', '1', '2017-04-10 15:20:22', null);
INSERT INTO `center_deliverables` VALUES ('3', '2', null, '2', null, '2017-04-27 13:59:07', '2017-04-27 13:59:07', null, '1', '1115', '1115', '2017-04-27 13:59:07', null);
INSERT INTO `center_deliverables` VALUES ('4', '4', null, '2', null, '2017-05-02 14:30:32', '2017-05-02 14:30:32', null, '1', '1115', '1115', '2017-05-02 14:30:32', null);
INSERT INTO `center_deliverables` VALUES ('5', '5', null, '2', null, '2017-05-02 15:54:25', '2017-05-02 15:54:25', null, '1', '1115', '1115', '2017-05-02 15:54:25', null);
INSERT INTO `center_deliverables` VALUES ('6', '3', null, '2', 'Tool to measure market sensitivity', '2017-05-03 11:12:29', '2017-05-03 00:00:00', '2017-05-16 00:00:00', '1', '1115', '1115', '2017-05-17 08:47:09', null);
INSERT INTO `center_deliverables` VALUES ('7', '3', null, '2', null, '2017-05-08 10:49:05', '2017-05-08 10:49:05', null, '1', '1115', '1115', '2017-05-08 10:49:05', null);
INSERT INTO `center_deliverables` VALUES ('8', '3', null, '2', null, '2017-05-19 11:41:24', '2017-05-19 11:41:24', null, '1', '1115', '1115', '2017-05-19 11:41:24', null);

-- ----------------------------
-- Table structure for center_deliverable_crosscuting_themes
-- ----------------------------
DROP TABLE IF EXISTS `center_deliverable_crosscuting_themes`;
CREATE TABLE `center_deliverable_crosscuting_themes` (
  `id` bigint(20) NOT NULL,
  `climate_change` tinyint(1) DEFAULT NULL,
  `gender` tinyint(1) DEFAULT NULL,
  `youth` tinyint(1) DEFAULT NULL,
  `policies_institutions` tinyint(1) DEFAULT NULL,
  `capacity_development` tinyint(1) DEFAULT NULL,
  `big_data` tinyint(1) DEFAULT NULL,
  `impact_assessment` tinyint(1) DEFAULT NULL,
  `n_a` tinyint(1) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL,
  `active_since` timestamp NULL DEFAULT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `deliverable_cross_created_fk` (`created_by`),
  KEY `deliverable_cross_modified_fk` (`modified_by`),
  CONSTRAINT `deliverable_cross_created_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `deliverable_cross_id_fk` FOREIGN KEY (`id`) REFERENCES `center_deliverables` (`id`),
  CONSTRAINT `deliverable_cross_modified_fk` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of center_deliverable_crosscuting_themes
-- ----------------------------
INSERT INTO `center_deliverable_crosscuting_themes` VALUES ('1', null, null, null, null, null, null, null, null, '1', '2017-05-23 11:33:17', '3', '3', '');
INSERT INTO `center_deliverable_crosscuting_themes` VALUES ('2', null, null, null, null, null, null, null, null, '1', '2017-05-23 11:33:17', '3', '3', '');
INSERT INTO `center_deliverable_crosscuting_themes` VALUES ('3', null, null, null, null, null, null, null, null, '1', '2017-05-23 11:33:17', '3', '3', '');
INSERT INTO `center_deliverable_crosscuting_themes` VALUES ('4', null, null, null, null, null, null, null, null, '1', '2017-05-23 11:33:17', '3', '3', '');
INSERT INTO `center_deliverable_crosscuting_themes` VALUES ('5', null, null, null, null, null, null, null, null, '1', '2017-05-23 11:33:17', '3', '3', '');
INSERT INTO `center_deliverable_crosscuting_themes` VALUES ('6', null, null, null, null, null, null, null, null, '1', '2017-05-23 11:33:17', '3', '3', '');
INSERT INTO `center_deliverable_crosscuting_themes` VALUES ('7', null, null, null, null, null, null, null, null, '1', '2017-05-23 11:33:17', '3', '3', '');
INSERT INTO `center_deliverable_crosscuting_themes` VALUES ('8', null, null, null, null, null, null, null, null, '1', '2017-05-23 11:33:17', '3', '3', '');

-- ----------------------------
-- Table structure for center_deliverable_documents
-- ----------------------------
DROP TABLE IF EXISTS `center_deliverable_documents`;
CREATE TABLE `center_deliverable_documents` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `deliverable_id` bigint(20) NOT NULL,
  `link` text,
  `is_active` tinyint(1) NOT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `active_since` timestamp NULL DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `deliverable_documents_deliverable_id_fk` (`deliverable_id`),
  KEY `deliverable_documents_created_id_fk` (`created_by`),
  KEY `deliverable_documents_modified_id_fk` (`modified_by`),
  CONSTRAINT `deliverable_documents_created_id_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `deliverable_documents_deliverable_id_fk` FOREIGN KEY (`deliverable_id`) REFERENCES `center_deliverables` (`id`),
  CONSTRAINT `deliverable_documents_modified_id_fk` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of center_deliverable_documents
-- ----------------------------
INSERT INTO `center_deliverable_documents` VALUES ('1', '1', 'http://test.com', '1', '1057', '1057', '2017-04-03 11:42:37', '');
INSERT INTO `center_deliverable_documents` VALUES ('2', '6', 'http://hdl.handle.net/10568/81010', '0', '1119', '1119', '2017-05-16 14:51:23', '');
INSERT INTO `center_deliverable_documents` VALUES ('3', '6', '', '0', '1119', '1119', '2017-05-16 14:51:23', '');
INSERT INTO `center_deliverable_documents` VALUES ('4', '6', '', '0', '1119', '1119', '2017-05-16 14:51:23', '');
INSERT INTO `center_deliverable_documents` VALUES ('5', '6', '', '0', '1119', '1119', '2017-05-16 14:52:00', '');

-- ----------------------------
-- Table structure for center_deliverable_outputs
-- ----------------------------
DROP TABLE IF EXISTS `center_deliverable_outputs`;
CREATE TABLE `center_deliverable_outputs` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `deliverable_id` bigint(20) DEFAULT NULL,
  `output_id` int(11) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL,
  `active_since` timestamp NULL DEFAULT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `do_deliverable_fk` (`deliverable_id`),
  KEY `do_outputs_fk` (`output_id`),
  KEY `do_created_fk` (`created_by`),
  KEY `do_modified_fk` (`modified_by`),
  CONSTRAINT `do_created_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `do_deliverable_fk` FOREIGN KEY (`deliverable_id`) REFERENCES `center_deliverables` (`id`),
  CONSTRAINT `do_modified_fk` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`),
  CONSTRAINT `do_outputs_fk` FOREIGN KEY (`output_id`) REFERENCES `center_outputs` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of center_deliverable_outputs
-- ----------------------------

-- ----------------------------
-- Table structure for center_deliverable_types
-- ----------------------------
DROP TABLE IF EXISTS `center_deliverable_types`;
CREATE TABLE `center_deliverable_types` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  `description` text,
  `is_active` tinyint(1) NOT NULL,
  `active_since` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `deliverable_types_parent_id_idx` (`parent_id`) USING BTREE,
  KEY `deliverable_type_created` (`created_by`),
  KEY `deliverable_type_modified` (`modified_by`),
  CONSTRAINT `center_deliverable_types_ibfk_1` FOREIGN KEY (`parent_id`) REFERENCES `center_deliverable_types` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `deliverable_type_created` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `deliverable_type_modified` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=112 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of center_deliverable_types
-- ----------------------------
INSERT INTO `center_deliverable_types` VALUES ('42', 'Data, models and tools', null, null, '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('43', 'Reports and other publications ', null, null, '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('44', 'Outreach products', null, null, '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('46', 'Training materials', null, null, '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('49', 'Articles and Books', null, null, '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('51', 'Database/Dataset/Data documentation', '42', 'Dataset is a collection of data. Database is an organized collection of data. Data paper, dataset, database, repository', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('52', 'Data portal/Tool/Model code/Computer software', '42', 'Data Portals for dissemination, tools, model codes, computer software. search engine, games, algorithms.', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('53', 'Thesis', '43', 'Student thesis', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('54', 'Research workshop report', '43', 'Research workshop report, proceedings, workshop summary paper.', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('55', 'Policy brief/policy note/briefing paper', '43', 'Policy brief, policy note, briefing paper, 2020 synthesis, 2020 Vision focus, brief, policy paper, policy report, policy review, policy statement', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('56', 'Discussion paper/Working paper/White paper', '43', 'Discussion paper, policy working paper, progress report, research paper, research report, technical note, technical report, unpublished paper, white paper, working paper', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('57', 'Conference paper / Seminar paper', '43', 'Conference paper, seminar paper', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('58', 'Lecture/Training Course Material', '46', 'Lecture, training course material', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('59', 'Guidebook/Handbook/Good Practice Note', '46', 'Guidebook, handbook, good practice note', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('60', 'User manual/Technical Guide', '46', 'User manual, technical guide', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('61', 'Article for media/Magazine/Other (not peer-reviewed)', '44', 'Radio, TV, newspapers, newsletters, mazagines, etc', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('62', 'Outcome case study', '43', 'Outcome case study', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('63', 'Journal Article (peer reviewed)', '49', 'Peer-reviewed journal article from scientific journal', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('64', 'Book (peer reviewed)', '49', 'Peer-reviewed books', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('65', 'Book (non-peer reviewed)', '49', 'Non-peer reviewed books', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('66', 'Book chapter (peer reviewed)', '49', 'Peer-reviewed book chapters', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('67', 'Book chapter (non-peer reviewed)', '49', 'Non-peer-reviewed book chapters', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('68', 'Newsletter', '44', 'Newsletter', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('69', 'Social Media Output', '44', 'Wiki, linkedin group, facebook, yammer, etc.', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('70', 'Blog', '44', 'Blog (collection of posts)', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('71', 'Website', '44', 'Website', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('72', 'Presentation/Poster', '44', 'Presentation/Poster', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('73', 'Multimedia', '44', 'Video, audio, images', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('74', 'Maps/Geospatial data', '42', 'Geospatial data - geographic positioning information, CCAFS Sites Atlas, cropland, etc.', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('75', 'Brochure', '44', 'Brochure, Booklet', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('76', 'Outcome note', '43', 'Outcome note', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('77', 'Factsheet, Project Note', '44', 'Factsheet, project note, note', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('78', 'Infographic', '44', 'Infographic', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('79', 'Journal Article (non-peer reviewed)', '49', 'Non-peer reviewed journal article', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('80', 'Special issue', '49', 'Special issue', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('81', 'Policy workshop/Dialogue report', '43', 'Policy workshop report, dialogue report', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('82', 'Donor report', '43', 'Donor report, annual report, project paper, project report', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('83', 'Concept note', '43', 'Concept note', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('84', 'Governance, Administration & Management', null, null, '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('85', 'Management Meetings', '84', 'Management Meetings', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('86', 'Events', '84', 'Events', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('87', 'Governance report', '84', 'Authorizing plans, commitments and evaluation of the organization’s performance', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('88', 'Administration report', '84', 'Formulation of plans, framing policies and objectives. Finance reports.', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('89', 'Management report', '84', 'Putting plans and policies into actions.', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('90', 'Other Capacity', '46', 'Other Capacity', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('91', 'Partnerships', null, null, '1', '2017-06-07 14:54:13', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('92', 'Partnership Agreements', '91', 'Formal agreements/research plan developed with MOA and/or firm resource agreements, between two entities, agreed upon mutual outputs and/or information products', '1', '2017-06-07 14:56:11', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('93', 'Reseach Plan Developed', '91', 'Research plan developed with partners involving firm resource commitments', '1', '2017-06-07 14:56:13', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('94', 'Agrobiodiversity and Genebank (Agricultural technologies)', null, null, '1', '2017-06-07 14:56:28', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('95', 'Varieties/Improved Lines/cultivars', '94', null, '1', '2017-06-07 14:56:47', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('96', 'Reference genomes', '94', null, '1', '2017-06-08 09:43:18', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('97', 'Traits/molecular markers strains', '94', null, '1', '2017-06-08 09:43:25', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('98', 'Tool Kit', '94', null, '1', '2017-06-08 09:43:35', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('99', 'Strategies', '94', null, '1', '2017-06-08 09:43:39', '3', '3', ' ');
INSERT INTO `center_deliverable_types` VALUES ('100', 'Agronomic Practices', '94', null, '1', '2017-06-08 09:43:44', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('101', 'Agro-industrial processes', '94', null, '1', '2017-06-08 09:43:53', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('102', 'Methods/Methodology/approaches/guidelines', '94', null, '1', '2017-06-08 09:43:59', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('103', 'Protocols', '94', null, '1', '2017-06-08 09:44:03', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('104', 'Agricultural Technologies', '94', null, '1', '2017-06-08 09:44:08', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('105', 'Enhanced Genetic Materials/Germplasm', '94', null, '1', '2017-06-08 09:44:17', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('106', 'New accessions', '94', null, '1', '2017-06-08 09:44:23', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('107', 'Genetic Resources inventories', '94', null, '1', '2017-06-07 15:00:36', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('108', 'Genetic Resources characterization ', '94', null, '1', '2017-06-07 15:00:51', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('109', 'Creation of collections', '94', null, '1', '2017-06-07 15:01:04', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('110', 'Preserved/ continued existence of genetic material', '94', null, '1', '2017-06-07 15:01:15', '3', '3', null);
INSERT INTO `center_deliverable_types` VALUES ('111', 'Genebank Management Procedures', '94', 'New management procedures that add value to the geneabank collection and its use', '1', '2017-06-07 15:02:30', '3', '3', null);

-- ----------------------------
-- Table structure for center_funding_source_types
-- ----------------------------
DROP TABLE IF EXISTS `center_funding_source_types`;
CREATE TABLE `center_funding_source_types` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` text,
  `is_active` tinyint(1) NOT NULL,
  `active_since` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `funding_source_type_created_fk` (`created_by`),
  KEY `funding_source_type_modified_fk` (`modified_by`),
  CONSTRAINT `funding_source_type_created_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `funding_source_type_modified_fk` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of center_funding_source_types
-- ----------------------------
INSERT INTO `center_funding_source_types` VALUES ('1', 'W1/W2', '1', '2017-02-28 11:08:50', '3', '3', ' ');
INSERT INTO `center_funding_source_types` VALUES ('2', 'Bilateral', '0', '2017-06-27 10:28:53', '3', '3', ' ');
INSERT INTO `center_funding_source_types` VALUES ('3', 'W3', '0', '2017-06-27 10:28:53', '3', '3', ' ');

-- ----------------------------
-- Table structure for center_impacts
-- ----------------------------
DROP TABLE IF EXISTS `center_impacts`;
CREATE TABLE `center_impacts` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `description` text NOT NULL,
  `short_name` text,
  `impact_statement_id` bigint(20) DEFAULT NULL,
  `target_year` int(11) DEFAULT NULL,
  `color` varchar(8) DEFAULT NULL,
  `research_program_id` int(11) NOT NULL,
  `is_active` tinyint(1) NOT NULL,
  `active_since` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `fk_impact_program` (`research_program_id`) USING BTREE,
  KEY `fk_impact_created_by` (`created_by`) USING BTREE,
  KEY `fk_impact_modified_by` (`modified_by`) USING BTREE,
  KEY `research_impacts_statement_id_fk` (`impact_statement_id`),
  CONSTRAINT `center_impacts_ibfk_1` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `center_impacts_ibfk_2` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`),
  CONSTRAINT `center_impacts_ibfk_3` FOREIGN KEY (`research_program_id`) REFERENCES `center_programs` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `research_impacts_statement_id_fk` FOREIGN KEY (`impact_statement_id`) REFERENCES `center_impact_statement` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of center_impacts
-- ----------------------------
INSERT INTO `center_impacts` VALUES ('1', 'Productive, resilient and ecologically sustainable food systems that encourage healthy diets', '', null, null, null, '7', '1', '2017-03-23 13:48:20', '1114', '1057', null);
INSERT INTO `center_impacts` VALUES ('2', 'Partners and service providers improve feed and forage interventions.', null, null, null, null, '7', '0', '2017-03-23 13:48:20', '1114', '1114', null);
INSERT INTO `center_impacts` VALUES ('3', 'Animal production increased through a better utilization of improved feed and forages technologies.', 'Animal production', null, null, null, '8', '1', '2017-04-07 11:45:48', '1115', '1057', null);
INSERT INTO `center_impacts` VALUES ('4', 'Partners and service providers improve feed and forage interventions.', 'Partners and service', null, null, null, '8', '1', '2017-04-18 11:13:34', '1115', '1057', null);
INSERT INTO `center_impacts` VALUES ('5', 'Improved natural environment of rural-urban landscapes.', 'Improved Rural-Urban Landscape', null, null, null, '3', '1', '2017-04-17 15:11:41', '1109', '1115', null);
INSERT INTO `center_impacts` VALUES ('6', 'Increased resilience of the poor to climate change and other shocks', 'Resilience to climate change', null, null, null, '2', '1', '2017-05-02 10:28:37', '1114', '1114', null);
INSERT INTO `center_impacts` VALUES ('7', 'Reduced poverty through enhanced smallholder market access', 'Smallholder Market Access', '2', null, null, '1', '1', '2017-05-03 11:07:04', '1115', '1114', null);
INSERT INTO `center_impacts` VALUES ('8', 'Sustainable and profitable cassava crop production for wealth, food security and income generation.', 'Yuca for wealth, food security', null, null, null, '4', '1', '2017-05-05 10:11:46', '1115', '1115', null);
INSERT INTO `center_impacts` VALUES ('9', 'NO Farmers increase cassava production through efficient and sustainable crop management practices in Asia and Latin America CHECK', '', null, null, null, '4', '0', '2017-05-05 14:29:45', '1115', '1115', null);
INSERT INTO `center_impacts` VALUES ('10', 'NO Farmers increase cassava production through sustainable access of clean seed varieties in Asia and Latin America', '', null, null, null, '4', '0', '2017-05-05 14:33:11', '1115', '1115', null);
INSERT INTO `center_impacts` VALUES ('11', 'kjkj', 'jk', null, null, null, '5', '1', '2017-05-16 09:35:59', '1115', '1115', null);
INSERT INTO `center_impacts` VALUES ('12', 'Reduced poverty through increased productivity', 'Reduced poverty', '4', null, null, '6', '1', '2017-06-14 14:16:58', '1114', '1114', null);
INSERT INTO `center_impacts` VALUES ('13', '', '', null, null, null, '9', '1', '2017-05-19 16:17:15', '1057', '1057', null);
INSERT INTO `center_impacts` VALUES ('14', '', '', null, null, null, '1', '0', '2017-05-24 11:56:55', '1057', '1057', null);

-- ----------------------------
-- Table structure for center_impact_beneficiaries
-- ----------------------------
DROP TABLE IF EXISTS `center_impact_beneficiaries`;
CREATE TABLE `center_impact_beneficiaries` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `impact_id` int(11) DEFAULT NULL,
  `beneficiary_id` int(11) DEFAULT NULL,
  `research_region_id` int(11) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL,
  `active_since` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `impact_beneficiary_beneficiary_id` (`beneficiary_id`) USING BTREE,
  KEY `impact_beneficiary_region` (`research_region_id`) USING BTREE,
  KEY `impact_beneficiary_created_by` (`created_by`) USING BTREE,
  KEY `impact_beneficiary_modified_by` (`modified_by`) USING BTREE,
  KEY `impact_beneficiary_impact_fk` (`impact_id`) USING BTREE,
  CONSTRAINT `center_impact_beneficiaries_ibfk_1` FOREIGN KEY (`beneficiary_id`) REFERENCES `center_beneficiaries` (`id`),
  CONSTRAINT `center_impact_beneficiaries_ibfk_2` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `center_impact_beneficiaries_ibfk_3` FOREIGN KEY (`impact_id`) REFERENCES `center_impacts` (`id`),
  CONSTRAINT `center_impact_beneficiaries_ibfk_4` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`),
  CONSTRAINT `center_impact_beneficiaries_ibfk_5` FOREIGN KEY (`research_region_id`) REFERENCES `center_regions` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of center_impact_beneficiaries
-- ----------------------------
INSERT INTO `center_impact_beneficiaries` VALUES ('1', '1', '12', '10', '1', '2017-04-26 13:14:53', '1114', '1057', '');
INSERT INTO `center_impact_beneficiaries` VALUES ('2', '1', '14', '3', '0', '2017-03-23 13:48:20', '1114', '1114', '');
INSERT INTO `center_impact_beneficiaries` VALUES ('3', '2', '11', '2', '1', '2017-03-23 13:48:20', '1114', '1114', '');
INSERT INTO `center_impact_beneficiaries` VALUES ('4', '3', '14', '2', '1', '2017-04-26 13:14:30', '1115', '1057', '');
INSERT INTO `center_impact_beneficiaries` VALUES ('5', '3', '14', '3', '1', '2017-04-26 13:14:30', '1115', '1057', '');
INSERT INTO `center_impact_beneficiaries` VALUES ('6', '4', '11', '2', '1', '2017-04-26 13:14:30', '1115', '1057', '');
INSERT INTO `center_impact_beneficiaries` VALUES ('7', '1', '17', '10', '1', '2017-04-26 13:14:53', '1115', '1057', '');
INSERT INTO `center_impact_beneficiaries` VALUES ('8', '5', '14', '2', '1', '2017-04-24 08:34:37', '1109', '1115', '');
INSERT INTO `center_impact_beneficiaries` VALUES ('9', '6', '15', '6', '1', '2017-05-02 10:28:37', '1114', '1114', '');
INSERT INTO `center_impact_beneficiaries` VALUES ('10', '6', '20', '1', '1', '2017-05-02 10:28:37', '1114', '1114', '');
INSERT INTO `center_impact_beneficiaries` VALUES ('11', '7', '15', '1', '1', '2017-06-15 15:41:22', '1115', '1114', '');
INSERT INTO `center_impact_beneficiaries` VALUES ('12', '8', '22', '2', '0', '2017-05-19 10:05:40', '1115', '1114', '');
INSERT INTO `center_impact_beneficiaries` VALUES ('13', '8', '22', '8', '0', '2017-05-19 10:05:40', '1115', '1114', '');
INSERT INTO `center_impact_beneficiaries` VALUES ('14', '9', '22', '1', '0', '2017-05-19 10:05:40', '1115', '1114', '');
INSERT INTO `center_impact_beneficiaries` VALUES ('15', '10', '14', '2', '0', '2017-05-19 10:05:40', '1115', '1114', '');
INSERT INTO `center_impact_beneficiaries` VALUES ('16', '10', '14', '8', '0', '2017-05-19 10:05:40', '1115', '1114', '');
INSERT INTO `center_impact_beneficiaries` VALUES ('17', '12', '14', '2', '1', '2017-06-14 11:31:25', '1114', '1114', '');
INSERT INTO `center_impact_beneficiaries` VALUES ('18', '12', '9', '7', '1', '2017-06-14 11:31:25', '1114', '1114', '');
INSERT INTO `center_impact_beneficiaries` VALUES ('19', '12', '12', '2', '1', '2017-06-14 11:31:25', '1114', '1114', '');
INSERT INTO `center_impact_beneficiaries` VALUES ('20', '8', null, null, '0', '2017-05-19 10:05:40', '1114', '1114', '');
INSERT INTO `center_impact_beneficiaries` VALUES ('21', '14', null, null, '1', '2017-05-24 11:56:56', '1057', '1057', '');
INSERT INTO `center_impact_beneficiaries` VALUES ('22', '8', '14', '6', '1', '2017-06-15 13:09:07', '1114', '1115', '');
INSERT INTO `center_impact_beneficiaries` VALUES ('23', '8', '9', '2', '1', '2017-06-15 13:09:07', '1114', '1115', '');
INSERT INTO `center_impact_beneficiaries` VALUES ('24', '9', null, null, '1', '2017-06-15 13:04:14', '1114', '1115', '');
INSERT INTO `center_impact_beneficiaries` VALUES ('25', '10', null, null, '1', '2017-06-15 13:04:14', '1114', '1115', '');

-- ----------------------------
-- Table structure for center_impact_objectives
-- ----------------------------
DROP TABLE IF EXISTS `center_impact_objectives`;
CREATE TABLE `center_impact_objectives` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `objective_id` int(11) NOT NULL,
  `impact_id` int(11) NOT NULL,
  `is_active` tinyint(1) NOT NULL,
  `active_since` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `fk_impact_objective_impact` (`impact_id`) USING BTREE,
  KEY `fk_impact_objective_objective` (`objective_id`) USING BTREE,
  KEY `fk_impact_objective_created_by` (`created_by`) USING BTREE,
  KEY `fk_impact_objective_modified_by` (`modified_by`) USING BTREE,
  CONSTRAINT `center_impact_objectives_ibfk_1` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `center_impact_objectives_ibfk_2` FOREIGN KEY (`impact_id`) REFERENCES `center_impacts` (`id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT `center_impact_objectives_ibfk_3` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`),
  CONSTRAINT `center_impact_objectives_ibfk_4` FOREIGN KEY (`objective_id`) REFERENCES `center_objectives` (`id`) ON DELETE NO ACTION ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8 COMMENT='The table that tracks the impact objectives.';

-- ----------------------------
-- Records of center_impact_objectives
-- ----------------------------
INSERT INTO `center_impact_objectives` VALUES ('1', '1', '1', '1', '2017-03-23 13:48:20', '1114', '1114', null);
INSERT INTO `center_impact_objectives` VALUES ('2', '1', '2', '0', '2017-03-23 13:48:20', '1114', '1114', null);
INSERT INTO `center_impact_objectives` VALUES ('3', '1', '3', '1', '2017-04-07 11:45:48', '1115', '1115', null);
INSERT INTO `center_impact_objectives` VALUES ('4', '1', '4', '1', '2017-04-07 11:46:27', '1115', '1115', null);
INSERT INTO `center_impact_objectives` VALUES ('5', '3', '1', '1', '2017-04-07 13:35:02', '1115', '1115', null);
INSERT INTO `center_impact_objectives` VALUES ('6', '3', '5', '1', '2017-04-17 15:11:41', '1109', '1109', null);
INSERT INTO `center_impact_objectives` VALUES ('7', '3', '6', '1', '2017-05-02 10:28:37', '1114', '1114', null);
INSERT INTO `center_impact_objectives` VALUES ('8', '2', '7', '1', '2017-05-03 11:07:04', '1115', '1115', null);
INSERT INTO `center_impact_objectives` VALUES ('9', '1', '8', '0', '2017-05-05 10:11:46', '1115', '1115', null);
INSERT INTO `center_impact_objectives` VALUES ('10', '2', '8', '1', '2017-05-05 10:11:46', '1115', '1115', null);
INSERT INTO `center_impact_objectives` VALUES ('11', '3', '9', '0', '2017-05-05 14:29:45', '1115', '1115', null);
INSERT INTO `center_impact_objectives` VALUES ('12', '1', '10', '0', '2017-05-05 14:33:11', '1115', '1115', null);
INSERT INTO `center_impact_objectives` VALUES ('13', '2', '11', '1', '2017-05-16 09:35:59', '1115', '1115', null);
INSERT INTO `center_impact_objectives` VALUES ('14', '1', '12', '1', '2017-05-17 10:21:43', '1114', '1114', null);

-- ----------------------------
-- Table structure for center_impact_statement
-- ----------------------------
DROP TABLE IF EXISTS `center_impact_statement`;
CREATE TABLE `center_impact_statement` (
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
-- Records of center_impact_statement
-- ----------------------------
INSERT INTO `center_impact_statement` VALUES ('1', 'Reduced poverty through increased resilience of the poor to climate change and other shocks', '1', '1', '2017-06-14 14:13:52', '3', '3', ' ');
INSERT INTO `center_impact_statement` VALUES ('2', 'Reduced poverty through enhanced smallholder market access', '2', '1', '2017-06-14 14:14:02', '3', '3', ' ');
INSERT INTO `center_impact_statement` VALUES ('3', 'Reduced poverty through increased incomes and employment', '3', '1', '2017-06-14 14:14:06', '3', '3', ' ');
INSERT INTO `center_impact_statement` VALUES ('4', 'Reduced poverty through increased productivity', '4', '1', '2017-06-14 14:15:50', '3', '3', ' ');
INSERT INTO `center_impact_statement` VALUES ('5', 'Improved food and nutrition security for health through increased productivity', '4', '1', '2017-06-14 14:15:52', '3', '3', ' ');
INSERT INTO `center_impact_statement` VALUES ('6', 'Improved food and nutrition security for health through improved diets for poor and vulnerable people', '5', '1', '2017-06-14 14:15:59', '3', '3', ' ');
INSERT INTO `center_impact_statement` VALUES ('7', 'Improved food and nutrition security for health through improved food security', '6', '1', '2017-06-14 14:16:07', '3', '3', ' ');
INSERT INTO `center_impact_statement` VALUES ('8', 'Improved food and nutrition security for health through improved human and animal health through better agricultural practices', '7', '1', '2017-06-14 14:16:12', '3', '3', ' ');
INSERT INTO `center_impact_statement` VALUES ('9', 'Improved natural resources and ecosystem services through natural capital enhanced and protected, especially from climate change', '8', '1', '2017-06-14 14:16:20', '3', '3', ' ');
INSERT INTO `center_impact_statement` VALUES ('10', 'Improved natural resources and ecosystem services through enhanced benefits from ecosystem goods and services', '9', '1', '2017-06-14 14:16:27', '3', '3', ' ');
INSERT INTO `center_impact_statement` VALUES ('11', 'Improved natural resources and ecosystem services through more sustainably managed agro-ecosystems', '10', '1', '2017-06-14 14:16:28', '3', '3', ' ');

-- ----------------------------
-- Table structure for center_leaders
-- ----------------------------
DROP TABLE IF EXISTS `center_leaders`;
CREATE TABLE `center_leaders` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `research_area_id` int(11) DEFAULT NULL,
  `research_program_id` int(11) DEFAULT NULL,
  `research_center_id` int(11) DEFAULT NULL,
  `type_id` int(11) NOT NULL COMMENT 'The foreign key for the type of leader (research area leader, research program leader, research center leader).',
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `active_since` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `fk_leader_type` (`type_id`) USING BTREE,
  KEY `fk_leader_res_area` (`research_area_id`) USING BTREE,
  KEY `fk_leader_res_program` (`research_program_id`) USING BTREE,
  KEY `fk_leader_res_center` (`research_center_id`) USING BTREE,
  KEY `fk_leader_create_by` (`created_by`) USING BTREE,
  KEY `fk_leader_modified_by` (`modified_by`) USING BTREE,
  KEY `fk_leader_user` (`user_id`) USING BTREE,
  CONSTRAINT `center_leaders_ibfk_1` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `center_leaders_ibfk_2` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`),
  CONSTRAINT `center_leaders_ibfk_3` FOREIGN KEY (`research_area_id`) REFERENCES `center_areas` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `center_leaders_ibfk_4` FOREIGN KEY (`research_center_id`) REFERENCES `centers` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `center_leaders_ibfk_5` FOREIGN KEY (`research_program_id`) REFERENCES `center_programs` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `center_leaders_ibfk_6` FOREIGN KEY (`type_id`) REFERENCES `center_all_types` (`id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT `center_leaders_ibfk_7` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8 COMMENT='The table that tracks the leaders for research area, researc';

-- ----------------------------
-- Records of center_leaders
-- ----------------------------
INSERT INTO `center_leaders` VALUES ('1', '28', '1', null, '1', '5', '1', '2016-10-20 14:47:34', '3', '3', ' ');
INSERT INTO `center_leaders` VALUES ('2', '66', null, '2', '1', '6', '1', '2016-10-20 14:49:09', '3', '3', ' ');
INSERT INTO `center_leaders` VALUES ('3', '55', null, '1', '1', '6', '1', '2016-10-20 14:49:34', '3', '3', ' ');
INSERT INTO `center_leaders` VALUES ('4', '1109', null, '3', '1', '6', '1', '2016-10-20 14:50:47', '3', '3', ' ');
INSERT INTO `center_leaders` VALUES ('5', '1110', '2', null, '1', '5', '1', '2016-10-20 14:51:07', '3', '3', ' ');
INSERT INTO `center_leaders` VALUES ('6', '1111', null, '4', '1', '6', '1', '2016-10-20 14:51:21', '3', '3', ' ');
INSERT INTO `center_leaders` VALUES ('7', '904', null, '5', '1', '6', '1', '2016-10-20 14:51:34', '3', '3', ' ');
INSERT INTO `center_leaders` VALUES ('8', '1112', null, '6', '1', '6', '1', '2016-10-20 14:51:46', '3', '3', ' ');
INSERT INTO `center_leaders` VALUES ('9', '1113', null, '7', '1', '6', '1', '2016-10-20 14:52:33', '3', '3', '  ');
INSERT INTO `center_leaders` VALUES ('10', '1093', null, '8', '1', '6', '1', '2016-10-20 14:52:35', '3', '3', ' ');
INSERT INTO `center_leaders` VALUES ('11', '1025', '3', null, '1', '5', '1', '2016-12-01 14:35:37', '3', '3', ' ');
INSERT INTO `center_leaders` VALUES ('14', '1122', null, '8', '1', '6', '1', '2017-03-23 13:48:49', '3', '3', ' ');

-- ----------------------------
-- Table structure for center_milestones
-- ----------------------------
DROP TABLE IF EXISTS `center_milestones`;
CREATE TABLE `center_milestones` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` text,
  `target_year` int(11) DEFAULT NULL,
  `value` decimal(10,0) DEFAULT NULL,
  `target_unit_id` bigint(20) DEFAULT NULL,
  `impact_outcome_id` int(11) NOT NULL,
  `is_impact_pathway` tinyint(1) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `active_since` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `crp_program_outcome_id` (`impact_outcome_id`) USING BTREE,
  KEY `fk_milestones_created_by` (`created_by`) USING BTREE,
  KEY `fk_milestones_modified_by` (`modified_by`) USING BTREE,
  KEY `fk_milestones_target_unit` (`target_unit_id`) USING BTREE,
  CONSTRAINT `center_milestones_ibfk_1` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `center_milestones_ibfk_2` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`),
  CONSTRAINT `center_milestones_ibfk_3` FOREIGN KEY (`impact_outcome_id`) REFERENCES `center_outcomes` (`id`),
  CONSTRAINT `center_milestones_ibfk_4` FOREIGN KEY (`target_unit_id`) REFERENCES `center_target_units` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of center_milestones
-- ----------------------------
INSERT INTO `center_milestones` VALUES ('5', 'Research and development partners and the private sector (input suppliers) use on-farm feed assessment tools in one priority country', '2019', '1', '5', '1', '1', '1', '2017-03-23 13:50:56', '1114', '1057', null);
INSERT INTO `center_milestones` VALUES ('6', 'Research and development partners and the private sector (input suppliers) use on-farm feed assessment tools in one priority country in a further 3 priority countries ', '2022', '3', '5', '1', '1', '1', '2017-03-23 13:50:56', '1114', '1057', null);
INSERT INTO `center_milestones` VALUES ('7', 'New equations for stationary and mobile NIRS integrated into platform for Colombia and Ethiopia.', '2022', '2', '5', '2', '1', '1', '2017-03-23 13:51:59', '1114', '1114', null);
INSERT INTO `center_milestones` VALUES ('8', 'Research and development partners and the private sector (input suppliers) use on-farm feed assessment tools in one priority country', '2019', '1', '5', '3', '1', '1', '2017-04-07 11:50:18', '1115', '1115', null);
INSERT INTO `center_milestones` VALUES ('9', 'Research and development partners and the private sector (input suppliers) use on-farm feed assessment tools in one priority country in a further 3 priority countries ', '2022', '3', '5', '3', '1', '1', '2017-04-07 11:50:18', '1115', '1115', null);
INSERT INTO `center_milestones` VALUES ('10', 'New equations for stationary and mobile NIRS integrated into platform for Colombia and Ethiopia.', '2022', '2', '5', '4', '1', '1', '2017-04-07 11:51:46', '1115', '1115', null);
INSERT INTO `center_milestones` VALUES ('11', 'A growing community of practice of regional genebanks working on \'genomics-of-genebanks\' projects and dealing with PGR-related policy issues', '2022', null, '-1', '6', '1', '1', '2017-04-07 13:59:38', '1115', '1115', null);
INSERT INTO `center_milestones` VALUES ('12', 'A new generation of genetic-resources scientists conversant in molecular and other techniques to harnesss crop diversity for crop improvement and deployment', '2022', null, '-1', '6', '1', '1', '2017-04-07 13:59:38', '1115', '1115', null);
INSERT INTO `center_milestones` VALUES ('13', 'Disease-free and viable bean, cassava and tropical-forage germplasm available to genebank clients as an input for breeding, research or direct deployment in agrifood systems', '2022', null, '-1', '7', '1', '1', '2017-04-07 14:00:48', '1115', '1115', null);
INSERT INTO `center_milestones` VALUES ('14', 'Disease-free and viable bean, cassava and tropical-forage germplasm available to genebank clients as an input for breeding, research or direct deployment in agrifood systems', '2028', null, '-1', '5', '1', '1', '2017-04-07 14:01:34', '1115', '1115', null);
INSERT INTO `center_milestones` VALUES ('15', 'Policy makers increase awareness of the socioeconomic benefits of mainstreaming Ecosystem Services in rural-urban landscape management.', '2018', '2', '42', '8', '1', '1', '2017-04-17 15:21:44', '1109', '1115', null);
INSERT INTO `center_milestones` VALUES ('16', 'Government, private sector and development agencies increase awareness of ecosystem based adaptation alternatives where management practices are identified and assessed. ', '2020', '2', '42', '8', '1', '1', '2017-04-17 15:21:44', '1109', '1115', null);
INSERT INTO `center_milestones` VALUES ('17', 'Actions and strategies to enable policy environments for CSA', '2019', '4', '42', '9', '1', '1', '2017-05-02 12:01:24', '1114', '1114', null);
INSERT INTO `center_milestones` VALUES ('18', 'Partners and Organizations develop plans with CIAT climate change research', '2019', '4', '42', '10', '1', '1', '2017-05-02 12:20:56', '1114', '1114', null);
INSERT INTO `center_milestones` VALUES ('19', 'CIAT climate change inform organizations and institutions about strategic climate change priority setting investments ', '2019', '4', '12', '10', '1', '1', '2017-05-02 12:28:27', '1114', '1114', null);
INSERT INTO `center_milestones` VALUES ('20', 'Interaction and information shared  with local and national governance systems to share climate change  science', '2019', '3', '5', '9', '1', '1', '2017-05-02 14:26:32', '1114', '1114', null);
INSERT INTO `center_milestones` VALUES ('21', 'Purchasing companies design, test and validate value chain interventions.', '2017', null, '-1', '11', '1', '1', '2017-05-03 11:09:16', '1115', '1115', null);
INSERT INTO `center_milestones` VALUES ('22', 'Purchasing companies implement business models and practices that facilitate the inclusion of small-scale producers.', '2020', null, '-1', '11', '1', '1', '2017-05-03 11:09:16', '1115', '1115', null);
INSERT INTO `center_milestones` VALUES ('23', 'Cassava value chain strategies and approaches developed, tested and validated', '2019', '2', '41', '12', '1', '1', '2017-05-05 10:40:53', '1115', '1115', null);
INSERT INTO `center_milestones` VALUES ('24', 'Seed systems and harvesting technologies and strategies implemented for better cassava crop performance', '2019', '100', '11', '13', '1', '1', '2017-05-05 14:13:21', '1115', '1115', null);
INSERT INTO `center_milestones` VALUES ('25', 'Post harvest solutions and nutrition targets identified, tested and validated for better market value', '2019', '100', '42', '14', '1', '1', '2017-05-05 14:37:39', '1115', '1115', null);
INSERT INTO `center_milestones` VALUES ('26', ' Partners and national systems released new cassava  varieties', '2019', '2', '28', '15', '1', '1', '2017-05-05 16:29:07', '1115', '1114', null);
INSERT INTO `center_milestones` VALUES ('27', 'Agronomic land use practices, tools and technologies tested and validated for highly productive, environmentally sustainable, and economically viable cassava crops', '2019', '100', '11', '16', '1', '1', '2017-05-08 08:23:29', '1115', '1115', null);
INSERT INTO `center_milestones` VALUES ('28', 'Integrated pest management strategies tested and validated to improve planting materials', '2020', '100', '11', '17', '1', '1', '2017-05-08 09:04:07', '1115', '1115', null);
INSERT INTO `center_milestones` VALUES ('29', 'Researchers and partners developed technologies to improve rice crop performance and adaptation', '2019', '5', '24', '29', '1', '1', '2017-05-18 15:34:24', '1114', '1114', null);
INSERT INTO `center_milestones` VALUES ('30', 'Researchers and partners conducted new analysis for monitoring crop performance under climate variability conditions ', '2019', '10', '39', '29', '1', '1', '2017-05-18 15:34:24', '1114', '1114', null);
INSERT INTO `center_milestones` VALUES ('31', 'Researches identified traits of interest for intensive and unfavorable rice production systems', '2019', '5', '39', '30', '1', '1', '2017-05-18 15:41:55', '1114', '1114', null);
INSERT INTO `center_milestones` VALUES ('32', 'Researchers developed methods and protocols to diagnose new diseases and pathogens in rice plants ', '2019', '5', '39', '32', '1', '1', '2017-05-18 15:43:21', '1114', '1114', null);
INSERT INTO `center_milestones` VALUES ('33', 'Researchers have better access to conduct rice data analysis', '2019', '100', '39', '34', '1', '1', '2017-05-18 15:51:02', '1114', '1114', null);
INSERT INTO `center_milestones` VALUES ('34', 'Genotypic and Phenotypic methods developed for multi-environmental trials', '2019', '5', '39', '33', '1', '1', '2017-05-23 15:02:30', '1114', '1114', null);
INSERT INTO `center_milestones` VALUES ('35', 'Rice traits (genes) with the potential to withstand Rice hoja blanca virus (RHBV), drought tolerance, high temperature, and controlling grain quality identified and incorporated into rice varieties', '2019', '2', '28', '35', '1', '1', '2017-05-23 16:16:29', '1114', '1114', null);
INSERT INTO `center_milestones` VALUES ('36', '', '2014', null, '-1', '19', '1', '1', '2017-05-24 11:33:28', '1057', '1057', null);
INSERT INTO `center_milestones` VALUES ('37', '', '2014', null, '-1', '21', '1', '1', '2017-05-24 11:35:22', '1057', '1057', null);
INSERT INTO `center_milestones` VALUES ('38', 'Prediction model for genomic selection developed', '2019', '1', '28', '39', '1', '1', '2017-06-12 12:15:08', '1114', '1114', null);
INSERT INTO `center_milestones` VALUES ('39', 'Better knowledge on defined prediction accuracies of genomic selection available to plant breeding programs through genomic selection ( breeding methodology) and  mapping tools for precision breeding developed', '2018', '3', '40', '39', '1', '1', '2017-06-12 12:54:38', '1114', '1114', null);
INSERT INTO `center_milestones` VALUES ('40', 'At least five rice varieties with 5% higher yield tested and validated for release in LAC', '2019', '50', '11', '40', '1', '1', '2017-06-12 13:08:55', '1114', '1114', null);
INSERT INTO `center_milestones` VALUES ('41', '2-5 promising lines  identified for nomination for variety release. ', '2019', '1', '28', '41', '1', '1', '2017-06-12 14:44:37', '1114', '1114', null);
INSERT INTO `center_milestones` VALUES ('42', 'At least 2 first-generation stress-tolerant varieties from GRiSP released.', '2018', '1', '28', '41', '1', '1', '2017-06-12 14:44:37', '1114', '1114', null);
INSERT INTO `center_milestones` VALUES ('43', 'Stress-tolerant varieties available for seed multiplication and cultivation. ', '2017', '1', '28', '41', '1', '1', '2017-06-12 14:44:37', '1114', '1114', null);
INSERT INTO `center_milestones` VALUES ('44', 'Grain quality markers employed to screen breeding germsplasm', '2021', '2', '39', '42', '1', '1', '2017-06-12 14:54:16', '1114', '1114', null);
INSERT INTO `center_milestones` VALUES ('45', 'Two high zinc rice identified', '2019', '1', '39', '42', '1', '1', '2017-06-12 14:54:16', '1114', '1114', null);
INSERT INTO `center_milestones` VALUES ('46', 'Two varieties with amylose content based on consumer preferences in LAC testes and validated.', '-1', null, '-1', '42', '1', '1', '2017-06-12 14:54:16', '1114', '1114', null);
INSERT INTO `center_milestones` VALUES ('47', 'Development of enhanced cassava germplasm by breeders', '2018', '2', '24', '15', '1', '1', '2017-06-15 12:38:30', '1114', '1115', null);
INSERT INTO `center_milestones` VALUES ('48', 'Cassava pest and disease monitoring and management technologies applied to avoid cassava production losses', '2018', '100', '11', '17', '1', '1', '2017-06-15 12:40:48', '1114', '1115', null);
INSERT INTO `center_milestones` VALUES ('49', 'Technologies and strategies of seed systems and harvesting tested and validated for healthy seeds and yield sustainability', '2018', '50', '42', '13', '1', '1', '2017-06-15 12:43:23', '1114', '1115', null);
INSERT INTO `center_milestones` VALUES ('50', 'Cassava value chain enhanced business relationships among small scale producers and purchasing companies', '2018', '1', '41', '12', '1', '1', '2017-06-15 13:16:50', '1115', '1115', null);

-- ----------------------------
-- Table structure for center_monitoring_milestones
-- ----------------------------
DROP TABLE IF EXISTS `center_monitoring_milestones`;
CREATE TABLE `center_monitoring_milestones` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `monitoring_outcome_id` bigint(20) NOT NULL,
  `milestone_id` bigint(20) NOT NULL,
  `achieved_value` decimal(10,2) DEFAULT NULL,
  `narrative` text,
  `is_active` tinyint(1) NOT NULL,
  `active_since` timestamp NULL DEFAULT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `monitoring_milestone_id_fk` (`milestone_id`),
  KEY `monitoring_milestone_outcome_fk` (`monitoring_outcome_id`),
  KEY `monitoring_milestone_created_fk` (`created_by`),
  KEY `monitoring_milestone_modified_fk` (`modified_by`),
  CONSTRAINT `monitoring_milestone_created_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `monitoring_milestone_id_fk` FOREIGN KEY (`milestone_id`) REFERENCES `center_milestones` (`id`),
  CONSTRAINT `monitoring_milestone_modified_fk` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`),
  CONSTRAINT `monitoring_milestone_outcome_fk` FOREIGN KEY (`monitoring_outcome_id`) REFERENCES `center_monitoring_outcomes` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=72 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of center_monitoring_milestones
-- ----------------------------
INSERT INTO `center_monitoring_milestones` VALUES ('1', '1', '5', '0.00', 'Sample Text.', '1', '2017-04-03 10:42:16', '1057', '1057', '');
INSERT INTO `center_monitoring_milestones` VALUES ('2', '1', '6', '2.00', 'Test Text', '1', '2017-04-03 10:42:16', '1057', '1057', '');
INSERT INTO `center_monitoring_milestones` VALUES ('3', '2', '5', null, '', '1', '2017-04-03 10:42:16', '1057', '1057', '');
INSERT INTO `center_monitoring_milestones` VALUES ('4', '2', '6', null, '', '1', '2017-04-03 10:42:16', '1057', '1057', '');
INSERT INTO `center_monitoring_milestones` VALUES ('5', '3', '5', null, '', '1', '2017-04-03 10:42:16', '1057', '1057', '');
INSERT INTO `center_monitoring_milestones` VALUES ('6', '3', '6', null, '', '1', '2017-04-03 10:42:16', '1057', '1057', '');
INSERT INTO `center_monitoring_milestones` VALUES ('7', '4', '6', null, '', '1', '2017-04-03 10:42:16', '1057', '1057', '');
INSERT INTO `center_monitoring_milestones` VALUES ('8', '5', '6', null, '', '1', '2017-04-03 10:42:16', '1057', '1057', '');
INSERT INTO `center_monitoring_milestones` VALUES ('9', '6', '6', null, '', '1', '2017-04-03 10:42:16', '1057', '1057', '');
INSERT INTO `center_monitoring_milestones` VALUES ('10', '7', '14', null, null, '1', '2017-04-07 15:18:20', '1057', '1057', '');
INSERT INTO `center_monitoring_milestones` VALUES ('11', '8', '14', null, null, '1', '2017-04-07 15:18:20', '1057', '1057', '');
INSERT INTO `center_monitoring_milestones` VALUES ('12', '9', '14', null, null, '1', '2017-04-07 15:18:20', '1057', '1057', '');
INSERT INTO `center_monitoring_milestones` VALUES ('13', '10', '14', null, null, '1', '2017-04-07 15:18:20', '1057', '1057', '');
INSERT INTO `center_monitoring_milestones` VALUES ('14', '11', '14', null, null, '1', '2017-04-07 15:18:20', '1057', '1057', '');
INSERT INTO `center_monitoring_milestones` VALUES ('15', '12', '14', null, null, '1', '2017-04-07 15:18:20', '1057', '1057', '');
INSERT INTO `center_monitoring_milestones` VALUES ('16', '13', '14', null, null, '1', '2017-04-07 15:18:20', '1057', '1057', '');
INSERT INTO `center_monitoring_milestones` VALUES ('17', '14', '14', null, null, '1', '2017-04-07 15:18:20', '1057', '1057', '');
INSERT INTO `center_monitoring_milestones` VALUES ('18', '15', '14', null, null, '1', '2017-04-07 15:18:20', '1057', '1057', '');
INSERT INTO `center_monitoring_milestones` VALUES ('19', '16', '14', null, null, '1', '2017-04-07 15:18:20', '1057', '1057', '');
INSERT INTO `center_monitoring_milestones` VALUES ('20', '17', '14', null, null, '1', '2017-04-07 15:18:20', '1057', '1057', '');
INSERT INTO `center_monitoring_milestones` VALUES ('21', '18', '14', null, null, '1', '2017-04-07 15:18:20', '1057', '1057', '');
INSERT INTO `center_monitoring_milestones` VALUES ('22', '41', '8', null, null, '1', '2017-04-26 15:57:34', '1057', '1057', '');
INSERT INTO `center_monitoring_milestones` VALUES ('23', '41', '9', null, null, '1', '2017-04-26 15:57:34', '1057', '1057', '');
INSERT INTO `center_monitoring_milestones` VALUES ('24', '42', '8', null, null, '1', '2017-04-26 15:57:34', '1057', '1057', '');
INSERT INTO `center_monitoring_milestones` VALUES ('25', '42', '9', null, null, '1', '2017-04-26 15:57:34', '1057', '1057', '');
INSERT INTO `center_monitoring_milestones` VALUES ('26', '43', '8', null, null, '1', '2017-04-26 15:57:34', '1057', '1057', '');
INSERT INTO `center_monitoring_milestones` VALUES ('27', '43', '9', null, null, '1', '2017-04-26 15:57:34', '1057', '1057', '');
INSERT INTO `center_monitoring_milestones` VALUES ('28', '44', '9', null, null, '1', '2017-04-26 15:57:34', '1057', '1057', '');
INSERT INTO `center_monitoring_milestones` VALUES ('29', '45', '9', null, null, '1', '2017-04-26 15:57:34', '1057', '1057', '');
INSERT INTO `center_monitoring_milestones` VALUES ('30', '46', '9', null, null, '1', '2017-04-26 15:57:34', '1057', '1057', '');
INSERT INTO `center_monitoring_milestones` VALUES ('31', '47', '15', null, null, '1', '2017-04-26 16:17:52', '1115', '1115', '');
INSERT INTO `center_monitoring_milestones` VALUES ('32', '47', '16', null, null, '1', '2017-04-26 16:17:52', '1115', '1115', '');
INSERT INTO `center_monitoring_milestones` VALUES ('33', '48', '15', null, null, '1', '2017-04-26 16:17:52', '1115', '1115', '');
INSERT INTO `center_monitoring_milestones` VALUES ('34', '48', '16', null, null, '1', '2017-04-26 16:17:52', '1115', '1115', '');
INSERT INTO `center_monitoring_milestones` VALUES ('35', '49', '16', null, null, '1', '2017-04-26 16:17:52', '1115', '1115', '');
INSERT INTO `center_monitoring_milestones` VALUES ('36', '50', '16', null, null, '1', '2017-04-26 16:17:52', '1115', '1115', '');
INSERT INTO `center_monitoring_milestones` VALUES ('37', '52', '11', null, null, '1', '2017-04-28 10:21:33', '1057', '1057', '');
INSERT INTO `center_monitoring_milestones` VALUES ('38', '52', '12', null, null, '1', '2017-04-28 10:21:33', '1057', '1057', '');
INSERT INTO `center_monitoring_milestones` VALUES ('39', '53', '11', null, null, '1', '2017-04-28 10:21:33', '1057', '1057', '');
INSERT INTO `center_monitoring_milestones` VALUES ('40', '53', '12', null, null, '1', '2017-04-28 10:21:33', '1057', '1057', '');
INSERT INTO `center_monitoring_milestones` VALUES ('41', '54', '11', null, null, '1', '2017-04-28 10:21:33', '1057', '1057', '');
INSERT INTO `center_monitoring_milestones` VALUES ('42', '54', '12', null, null, '1', '2017-04-28 10:21:33', '1057', '1057', '');
INSERT INTO `center_monitoring_milestones` VALUES ('43', '55', '11', null, null, '1', '2017-04-28 10:21:33', '1057', '1057', '');
INSERT INTO `center_monitoring_milestones` VALUES ('44', '55', '12', null, null, '1', '2017-04-28 10:21:33', '1057', '1057', '');
INSERT INTO `center_monitoring_milestones` VALUES ('45', '56', '11', null, null, '1', '2017-04-28 10:21:33', '1057', '1057', '');
INSERT INTO `center_monitoring_milestones` VALUES ('46', '56', '12', null, null, '1', '2017-04-28 10:21:33', '1057', '1057', '');
INSERT INTO `center_monitoring_milestones` VALUES ('47', '57', '11', null, null, '1', '2017-04-28 10:21:33', '1057', '1057', '');
INSERT INTO `center_monitoring_milestones` VALUES ('48', '57', '12', null, null, '1', '2017-04-28 10:21:33', '1057', '1057', '');
INSERT INTO `center_monitoring_milestones` VALUES ('49', '86', '17', null, null, '1', '2017-05-02 15:09:39', '1114', '1114', '');
INSERT INTO `center_monitoring_milestones` VALUES ('50', '86', '20', null, null, '1', '2017-05-02 15:09:39', '1114', '1114', '');
INSERT INTO `center_monitoring_milestones` VALUES ('51', '87', '17', null, null, '1', '2017-05-02 15:09:39', '1114', '1114', '');
INSERT INTO `center_monitoring_milestones` VALUES ('52', '87', '20', null, null, '1', '2017-05-02 15:09:39', '1114', '1114', '');
INSERT INTO `center_monitoring_milestones` VALUES ('53', '88', '17', null, null, '1', '2017-05-02 15:09:39', '1114', '1114', '');
INSERT INTO `center_monitoring_milestones` VALUES ('54', '88', '20', null, null, '1', '2017-05-02 15:09:39', '1114', '1114', '');
INSERT INTO `center_monitoring_milestones` VALUES ('55', '92', '21', null, null, '1', '2017-05-03 11:23:37', '1114', '1114', '');
INSERT INTO `center_monitoring_milestones` VALUES ('56', '92', '22', null, null, '1', '2017-05-03 11:23:37', '1114', '1114', '');
INSERT INTO `center_monitoring_milestones` VALUES ('57', '93', '22', null, null, '1', '2017-05-03 11:23:37', '1114', '1114', '');
INSERT INTO `center_monitoring_milestones` VALUES ('58', '94', '22', null, null, '1', '2017-05-03 11:23:37', '1114', '1114', '');
INSERT INTO `center_monitoring_milestones` VALUES ('59', '95', '22', null, null, '1', '2017-05-03 11:23:37', '1114', '1114', '');
INSERT INTO `center_monitoring_milestones` VALUES ('60', '98', '23', null, null, '1', '2017-05-10 12:13:36', '1114', '1114', '');
INSERT INTO `center_monitoring_milestones` VALUES ('61', '99', '23', null, null, '1', '2017-05-10 12:13:36', '1114', '1114', '');
INSERT INTO `center_monitoring_milestones` VALUES ('62', '100', '23', null, null, '1', '2017-05-10 12:13:36', '1114', '1114', '');
INSERT INTO `center_monitoring_milestones` VALUES ('63', '104', '24', null, null, '1', '2017-05-16 11:23:18', '1057', '1057', '');
INSERT INTO `center_monitoring_milestones` VALUES ('64', '105', '24', null, null, '1', '2017-05-16 11:23:18', '1057', '1057', '');
INSERT INTO `center_monitoring_milestones` VALUES ('65', '110', '26', null, null, '1', '2017-05-18 09:15:41', '1057', '1057', '');
INSERT INTO `center_monitoring_milestones` VALUES ('66', '116', '29', null, null, '1', '2017-06-16 09:32:24', '1115', '1115', '');
INSERT INTO `center_monitoring_milestones` VALUES ('67', '116', '30', null, null, '1', '2017-06-16 09:32:24', '1115', '1115', '');
INSERT INTO `center_monitoring_milestones` VALUES ('68', '117', '29', null, null, '1', '2017-06-16 09:32:24', '1115', '1115', '');
INSERT INTO `center_monitoring_milestones` VALUES ('69', '117', '30', null, null, '1', '2017-06-16 09:32:24', '1115', '1115', '');
INSERT INTO `center_monitoring_milestones` VALUES ('70', '118', '29', null, null, '1', '2017-06-16 09:32:24', '1115', '1115', '');
INSERT INTO `center_monitoring_milestones` VALUES ('71', '118', '30', null, null, '1', '2017-06-16 09:32:24', '1115', '1115', '');

-- ----------------------------
-- Table structure for center_monitoring_outcomes
-- ----------------------------
DROP TABLE IF EXISTS `center_monitoring_outcomes`;
CREATE TABLE `center_monitoring_outcomes` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `outcome_id` int(11) NOT NULL,
  `year` decimal(4,0) NOT NULL,
  `narrative` text,
  `is_active` tinyint(1) NOT NULL,
  `active_since` timestamp NULL DEFAULT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `outcome_monitoring_id_fk` (`outcome_id`),
  KEY `outcome_monitoring_created_fk` (`created_by`),
  KEY `outcome_monitoring_modified_fk` (`modified_by`),
  CONSTRAINT `outcome_monitoring_created_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `outcome_monitoring_id_fk` FOREIGN KEY (`outcome_id`) REFERENCES `center_outcomes` (`id`),
  CONSTRAINT `outcome_monitoring_modified_fk` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=128 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of center_monitoring_outcomes
-- ----------------------------
INSERT INTO `center_monitoring_outcomes` VALUES ('1', '1', '2017', 'Sample narrative.', '1', '2017-04-03 10:42:16', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('2', '1', '2018', '', '1', '2017-04-03 10:42:16', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('3', '1', '2019', '', '1', '2017-04-03 10:42:16', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('4', '1', '2020', '', '1', '2017-04-03 10:42:16', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('5', '1', '2021', '', '1', '2017-04-03 10:42:16', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('6', '1', '2022', '', '1', '2017-04-03 10:42:16', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('7', '5', '2017', null, '1', '2017-04-07 15:18:20', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('8', '5', '2018', null, '1', '2017-04-07 15:18:20', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('9', '5', '2019', null, '1', '2017-04-07 15:18:20', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('10', '5', '2020', null, '1', '2017-04-07 15:18:20', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('11', '5', '2021', null, '1', '2017-04-07 15:18:20', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('12', '5', '2022', null, '1', '2017-04-07 15:18:20', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('13', '5', '2023', null, '1', '2017-04-07 15:18:20', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('14', '5', '2024', null, '1', '2017-04-07 15:18:20', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('15', '5', '2025', null, '1', '2017-04-07 15:18:20', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('16', '5', '2026', null, '1', '2017-04-07 15:18:20', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('17', '5', '2027', null, '1', '2017-04-07 15:18:20', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('18', '5', '2028', null, '1', '2017-04-07 15:18:20', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('19', '5', '2029', null, '1', '2017-04-07 15:18:20', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('20', '5', '2030', null, '1', '2017-04-07 15:18:20', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('21', '5', '2031', null, '1', '2017-04-07 15:18:20', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('22', '5', '2032', null, '1', '2017-04-07 15:18:20', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('23', '5', '2033', null, '1', '2017-04-07 15:18:20', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('24', '5', '2034', null, '1', '2017-04-07 15:18:20', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('25', '5', '2035', null, '1', '2017-04-07 15:18:20', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('26', '5', '2036', null, '1', '2017-04-07 15:18:20', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('27', '5', '2037', null, '1', '2017-04-07 15:18:20', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('28', '5', '2038', null, '1', '2017-04-07 15:18:20', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('29', '5', '2039', null, '1', '2017-04-07 15:18:20', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('30', '5', '2040', null, '1', '2017-04-07 15:18:20', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('31', '5', '2041', null, '1', '2017-04-07 15:18:20', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('32', '5', '2042', null, '1', '2017-04-07 15:18:20', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('33', '5', '2043', null, '1', '2017-04-07 15:18:20', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('34', '5', '2044', null, '1', '2017-04-07 15:18:20', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('35', '5', '2045', null, '1', '2017-04-07 15:18:20', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('36', '5', '2046', null, '1', '2017-04-07 15:18:20', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('37', '5', '2047', null, '1', '2017-04-07 15:18:20', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('38', '5', '2048', null, '1', '2017-04-07 15:18:20', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('39', '5', '2049', null, '1', '2017-04-07 15:18:20', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('40', '5', '2050', null, '1', '2017-04-07 15:18:20', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('41', '3', '2017', null, '1', '2017-04-26 15:57:34', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('42', '3', '2018', null, '1', '2017-04-26 15:57:34', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('43', '3', '2019', null, '1', '2017-04-26 15:57:34', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('44', '3', '2020', null, '1', '2017-04-26 15:57:34', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('45', '3', '2021', null, '1', '2017-04-26 15:57:34', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('46', '3', '2022', null, '1', '2017-04-26 15:57:34', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('47', '8', '2017', null, '1', '2017-04-26 16:17:52', '1115', '1115', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('48', '8', '2018', null, '1', '2017-04-26 16:17:52', '1115', '1115', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('49', '8', '2019', null, '1', '2017-04-26 16:17:52', '1115', '1115', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('50', '8', '2020', null, '1', '2017-04-26 16:17:52', '1115', '1115', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('51', '8', '2021', null, '1', '2017-04-26 16:17:52', '1115', '1115', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('52', '6', '2017', null, '1', '2017-04-28 10:21:33', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('53', '6', '2018', null, '1', '2017-04-28 10:21:33', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('54', '6', '2019', null, '1', '2017-04-28 10:21:33', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('55', '6', '2020', null, '1', '2017-04-28 10:21:33', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('56', '6', '2021', null, '1', '2017-04-28 10:21:33', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('57', '6', '2022', null, '1', '2017-04-28 10:21:33', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('58', '6', '2023', null, '1', '2017-04-28 10:21:33', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('59', '6', '2024', null, '1', '2017-04-28 10:21:33', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('60', '6', '2025', null, '1', '2017-04-28 10:21:33', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('61', '6', '2026', null, '1', '2017-04-28 10:21:33', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('62', '6', '2027', null, '1', '2017-04-28 10:21:33', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('63', '6', '2028', null, '1', '2017-04-28 10:21:33', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('64', '6', '2029', null, '1', '2017-04-28 10:21:33', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('65', '6', '2030', null, '1', '2017-04-28 10:21:33', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('66', '6', '2031', null, '1', '2017-04-28 10:21:33', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('67', '6', '2032', null, '1', '2017-04-28 10:21:33', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('68', '6', '2033', null, '1', '2017-04-28 10:21:33', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('69', '6', '2034', null, '1', '2017-04-28 10:21:33', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('70', '6', '2035', null, '1', '2017-04-28 10:21:33', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('71', '6', '2036', null, '1', '2017-04-28 10:21:33', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('72', '6', '2037', null, '1', '2017-04-28 10:21:33', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('73', '6', '2038', null, '1', '2017-04-28 10:21:33', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('74', '6', '2039', null, '1', '2017-04-28 10:21:33', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('75', '6', '2040', null, '1', '2017-04-28 10:21:34', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('76', '6', '2041', null, '1', '2017-04-28 10:21:34', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('77', '6', '2042', null, '1', '2017-04-28 10:21:34', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('78', '6', '2043', null, '1', '2017-04-28 10:21:34', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('79', '6', '2044', null, '1', '2017-04-28 10:21:34', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('80', '6', '2045', null, '1', '2017-04-28 10:21:34', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('81', '6', '2046', null, '1', '2017-04-28 10:21:34', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('82', '6', '2047', null, '1', '2017-04-28 10:21:34', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('83', '6', '2048', null, '1', '2017-04-28 10:21:34', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('84', '6', '2049', null, '1', '2017-04-28 10:21:34', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('85', '6', '2050', null, '1', '2017-04-28 10:21:34', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('86', '9', '2017', null, '1', '2017-05-02 15:09:39', '1114', '1114', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('87', '9', '2018', null, '1', '2017-05-02 15:09:39', '1114', '1114', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('88', '9', '2019', null, '1', '2017-05-02 15:09:39', '1114', '1114', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('89', '9', '2020', null, '1', '2017-05-02 15:09:39', '1114', '1114', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('90', '9', '2021', null, '1', '2017-05-02 15:09:39', '1114', '1114', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('91', '9', '2022', null, '1', '2017-05-02 15:09:39', '1114', '1114', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('92', '11', '2017', null, '1', '2017-05-03 11:23:37', '1114', '1114', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('93', '11', '2018', null, '1', '2017-05-03 11:23:37', '1114', '1114', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('94', '11', '2019', null, '1', '2017-05-03 11:23:37', '1114', '1114', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('95', '11', '2020', null, '1', '2017-05-03 11:23:37', '1114', '1114', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('96', '11', '2021', null, '1', '2017-05-03 11:23:37', '1114', '1114', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('97', '11', '2022', null, '1', '2017-05-03 11:23:37', '1114', '1114', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('98', '12', '2017', null, '1', '2017-05-10 12:13:36', '1114', '1114', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('99', '12', '2018', null, '1', '2017-05-10 12:13:36', '1114', '1114', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('100', '12', '2019', null, '1', '2017-05-10 12:13:36', '1114', '1114', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('101', '12', '2020', null, '1', '2017-05-10 12:13:36', '1114', '1114', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('102', '12', '2021', null, '1', '2017-05-10 12:13:36', '1114', '1114', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('103', '12', '2022', null, '1', '2017-05-10 12:13:36', '1114', '1114', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('104', '13', '2017', null, '1', '2017-05-16 11:23:18', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('105', '13', '2018', null, '1', '2017-05-16 11:23:18', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('106', '13', '2019', null, '1', '2017-05-16 11:23:18', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('107', '13', '2020', null, '1', '2017-05-16 11:23:18', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('108', '13', '2021', null, '1', '2017-05-16 11:23:18', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('109', '13', '2022', null, '1', '2017-05-16 11:23:18', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('110', '15', '2017', null, '1', '2017-05-18 09:15:41', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('111', '15', '2018', null, '1', '2017-05-18 09:15:41', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('112', '15', '2019', null, '1', '2017-05-18 09:15:41', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('113', '15', '2020', null, '1', '2017-05-18 09:15:41', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('114', '15', '2021', null, '1', '2017-05-18 09:15:42', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('115', '15', '2022', null, '1', '2017-05-18 09:15:42', '1057', '1057', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('116', '29', '2017', null, '1', '2017-06-16 09:32:24', '1115', '1115', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('117', '29', '2018', null, '1', '2017-06-16 09:32:24', '1115', '1115', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('118', '29', '2019', null, '1', '2017-06-16 09:32:24', '1115', '1115', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('119', '29', '2020', null, '1', '2017-06-16 09:32:24', '1115', '1115', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('120', '29', '2021', null, '1', '2017-06-16 09:32:24', '1115', '1115', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('121', '29', '2022', null, '1', '2017-06-16 09:32:24', '1115', '1115', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('122', '28', '2017', null, '1', '2017-06-16 10:30:05', '1114', '1114', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('123', '28', '2018', null, '1', '2017-06-16 10:30:05', '1114', '1114', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('124', '28', '2019', null, '1', '2017-06-16 10:30:05', '1114', '1114', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('125', '28', '2020', null, '1', '2017-06-16 10:30:05', '1114', '1114', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('126', '28', '2021', null, '1', '2017-06-16 10:30:06', '1114', '1114', '');
INSERT INTO `center_monitoring_outcomes` VALUES ('127', '28', '2022', null, '1', '2017-06-16 10:30:06', '1114', '1114', '');

-- ----------------------------
-- Table structure for center_monitoring_outcome_evidences
-- ----------------------------
DROP TABLE IF EXISTS `center_monitoring_outcome_evidences`;
CREATE TABLE `center_monitoring_outcome_evidences` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `monitoring_outcome_id` bigint(20) NOT NULL,
  `evidence_link` text NOT NULL,
  `is_active` tinyint(1) NOT NULL,
  `active_since` timestamp NULL DEFAULT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `evidence_outcome_monitoring_id_fk` (`monitoring_outcome_id`),
  KEY `evidence_created_fk` (`created_by`),
  KEY `evidence_modified_fk` (`modified_by`),
  CONSTRAINT `evidence_created_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `evidence_modified_fk` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`),
  CONSTRAINT `evidence_outcome_monitoring_id_fk` FOREIGN KEY (`monitoring_outcome_id`) REFERENCES `center_monitoring_outcomes` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of center_monitoring_outcome_evidences
-- ----------------------------
INSERT INTO `center_monitoring_outcome_evidences` VALUES ('1', '1', 'http://test.com', '1', '2017-04-03 11:53:23', '1057', '1057', '');

-- ----------------------------
-- Table structure for center_nextuser_types
-- ----------------------------
DROP TABLE IF EXISTS `center_nextuser_types`;
CREATE TABLE `center_nextuser_types` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(60) NOT NULL,
  `active_since` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `is_active` tinyint(1) DEFAULT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  `parent_type_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_nextusertypes_created_by` (`created_by`),
  KEY `fk_nextusertypes_modified_by` (`modified_by`),
  KEY `fk_nextusertypes_types` (`parent_type_id`),
  CONSTRAINT `fk_nextusertypes_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_nextusertypes_modified_by` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_nextusertypes_types` FOREIGN KEY (`parent_type_id`) REFERENCES `center_nextuser_types` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of center_nextuser_types
-- ----------------------------
INSERT INTO `center_nextuser_types` VALUES ('1', 'Policy Makers', '2016-11-30 10:16:15', '1', '3', '3', ' ', null);
INSERT INTO `center_nextuser_types` VALUES ('2', 'General Public/Public Institutions', '2016-11-30 10:16:40', '1', '3', '3', ' ', null);
INSERT INTO `center_nextuser_types` VALUES ('3', 'Private Industry Actors/Individuals', '2017-05-19 08:50:16', '1', '3', '3', ' ', null);
INSERT INTO `center_nextuser_types` VALUES ('4', 'Ministry of Environment', '2016-11-30 10:17:02', '1', '3', '3', ' ', '1');
INSERT INTO `center_nextuser_types` VALUES ('5', 'Ministry of Agriculture', '2016-11-30 10:17:24', '1', '3', '3', ' ', '1');
INSERT INTO `center_nextuser_types` VALUES ('6', 'Land-use planners', '2016-11-30 10:18:47', '1', '3', '3', ' ', '1');
INSERT INTO `center_nextuser_types` VALUES ('7', 'Farmers and land managers', '2017-05-03 14:30:53', '0', '3', '3', ' ', '1');
INSERT INTO `center_nextuser_types` VALUES ('8', 'local governments', '2016-11-30 10:18:46', '1', '3', '3', ' ', '1');
INSERT INTO `center_nextuser_types` VALUES ('9', 'national governments', '2016-11-30 10:18:45', '1', '3', '3', ' ', '1');
INSERT INTO `center_nextuser_types` VALUES ('10', 'Other Government Ministry', '2016-11-30 10:18:46', '1', '3', '3', ' ', '1');
INSERT INTO `center_nextuser_types` VALUES ('11', 'land managers', '2016-11-30 10:18:44', '1', '3', '3', ' ', '1');
INSERT INTO `center_nextuser_types` VALUES ('12', 'NGOs', '2016-11-30 10:18:50', '1', '3', '3', ' ', '2');
INSERT INTO `center_nextuser_types` VALUES ('13', 'Local Community Members', '2017-05-03 14:31:37', '1', '3', '3', ' ', '2');
INSERT INTO `center_nextuser_types` VALUES ('14', 'Decision makers', '2017-05-03 14:36:09', '0', '3', '3', ' ', '2');
INSERT INTO `center_nextuser_types` VALUES ('15', 'Development agencies', '2017-05-03 14:32:02', '1', '3', '3', ' ', '2');
INSERT INTO `center_nextuser_types` VALUES ('16', 'Donors', '2017-05-03 14:32:05', '1', '3', '3', ' ', '2');
INSERT INTO `center_nextuser_types` VALUES ('17', 'research and development organizations', '2017-05-03 14:36:20', '0', '3', '3', ' ', '2');
INSERT INTO `center_nextuser_types` VALUES ('18', 'Public Scientific community', '2017-05-03 14:35:39', '1', '3', '3', ' ', '2');
INSERT INTO `center_nextuser_types` VALUES ('19', 'Producers/Farmers', '2017-05-19 08:51:36', '1', '3', '3', ' ', '3');
INSERT INTO `center_nextuser_types` VALUES ('20', 'Breeders (private)', '2017-05-19 08:51:45', '1', '3', '3', ' ', '3');
INSERT INTO `center_nextuser_types` VALUES ('21', 'SMEs', '2016-11-30 10:26:30', '1', '3', '3', ' ', '3');
INSERT INTO `center_nextuser_types` VALUES ('22', 'Product transformation/processing companies', '2017-05-19 08:51:52', '1', '3', '3', ' ', '3');
INSERT INTO `center_nextuser_types` VALUES ('23', 'Product Commercialization Companies', '2017-05-19 08:52:09', '1', '3', '3', ' ', '3');
INSERT INTO `center_nextuser_types` VALUES ('24', 'Input suppliers', '2017-05-19 08:52:14', '1', '3', '3', ' ', '3');
INSERT INTO `center_nextuser_types` VALUES ('25', 'Financial Institutions (private)', '2017-05-19 08:52:17', '1', '3', '3', ' ', '3');
INSERT INTO `center_nextuser_types` VALUES ('26', 'National Agriculture Research System (NARS)', '2017-05-03 14:32:16', '1', '3', '3', ' ', '2');
INSERT INTO `center_nextuser_types` VALUES ('27', 'Public Scientific Community', '2017-05-03 14:32:38', '1', '3', '3', ' ', '2');
INSERT INTO `center_nextuser_types` VALUES ('28', 'Financial institutions (public)', '2017-05-03 14:33:03', '1', '3', '3', ' ', '2');
INSERT INTO `center_nextuser_types` VALUES ('29', 'Extension Agencies (public)', '2017-05-03 14:33:19', '1', '3', '3', ' ', '2');
INSERT INTO `center_nextuser_types` VALUES ('30', 'Breeders (public)', '2017-05-03 14:35:33', '1', '3', '3', ' ', '2');
INSERT INTO `center_nextuser_types` VALUES ('31', 'Research Partners (public)', '2017-05-03 14:35:52', '1', '3', '3', ' ', '2');
INSERT INTO `center_nextuser_types` VALUES ('32', 'Extension Agencies (private)', '2017-05-19 08:52:25', '1', '3', '3', null, '3');
INSERT INTO `center_nextuser_types` VALUES ('33', 'Research Partners (private)', '2017-05-19 08:52:37', '1', '3', '3', null, '3');

-- ----------------------------
-- Table structure for center_objectives
-- ----------------------------
DROP TABLE IF EXISTS `center_objectives`;
CREATE TABLE `center_objectives` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `objective` text NOT NULL,
  `research_center_id` int(11) NOT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `active_since` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `fk_objective_center` (`research_center_id`) USING BTREE,
  KEY `fk_objective_created_by` (`created_by`) USING BTREE,
  KEY `fk_objective_modified_by` (`modified_by`) USING BTREE,
  CONSTRAINT `center_objectives_ibfk_1` FOREIGN KEY (`research_center_id`) REFERENCES `centers` (`id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT `center_objectives_ibfk_2` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `center_objectives_ibfk_3` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of center_objectives
-- ----------------------------
INSERT INTO `center_objectives` VALUES ('1', 'Make affordable, high-quality food readily available to the rural and urban poor by boosting agricultural productivity and enhancing the nutritional quality of staple crops.', '1', '1', '2016-11-24 08:08:03', '1', '3', ' ');
INSERT INTO `center_objectives` VALUES ('2', 'Promote rural income growth by making smallholder agriculture more competitive and market oriented through improvements in agricultural value chains.', '1', '1', '2016-11-24 08:08:03', '1', '3', ' ');
INSERT INTO `center_objectives` VALUES ('3', 'Provide the means to make a more intensive and competitive agriculture both environmentally sustainable and climate smart.', '1', '1', '2016-11-24 08:08:03', '1', '3', ' ');

-- ----------------------------
-- Table structure for center_outcomes
-- ----------------------------
DROP TABLE IF EXISTS `center_outcomes`;
CREATE TABLE `center_outcomes` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `description` text,
  `short_name` text,
  `year` int(11) DEFAULT NULL,
  `value` decimal(10,0) DEFAULT NULL,
  `target_unit_id` bigint(20) DEFAULT NULL,
  `research_impact_id` int(11) DEFAULT NULL,
  `research_topic_id` int(11) DEFAULT NULL,
  `baseline` decimal(10,0) DEFAULT NULL,
  `is_impact_pathway` tinyint(1) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL,
  `active_since` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `fk_outcomes_rimpact` (`research_impact_id`) USING BTREE,
  KEY `fk_outcomes_rtopic` (`research_topic_id`) USING BTREE,
  KEY `fk_outcomes_created_by` (`created_by`) USING BTREE,
  KEY `fk_outcomes_modified_by` (`modified_by`) USING BTREE,
  KEY `fk_outcomes_target_unit` (`target_unit_id`) USING BTREE,
  CONSTRAINT `center_outcomes_ibfk_1` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `center_outcomes_ibfk_2` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`),
  CONSTRAINT `center_outcomes_ibfk_3` FOREIGN KEY (`research_impact_id`) REFERENCES `center_impacts` (`id`),
  CONSTRAINT `center_outcomes_ibfk_4` FOREIGN KEY (`research_topic_id`) REFERENCES `center_topics` (`id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT `center_outcomes_ibfk_5` FOREIGN KEY (`target_unit_id`) REFERENCES `center_target_units` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=44 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of center_outcomes
-- ----------------------------
INSERT INTO `center_outcomes` VALUES ('1', 'Local, national and international research and development partners, the private sector, decision-makers and livestock producers are able to diagnose feed constraints and opportunities and to effectively prioritize and target feed and forage interventions.', '', '2022', '4', '5', '1', '1', '2', '1', '0', '2017-05-24 11:35:11', '1114', '1057', 'redo with correct info');
INSERT INTO `center_outcomes` VALUES ('2', 'Research and development partners and service providers (private sector) are using up-to-date feed and forage technology which is cost effective (accurate and rapid)', null, '2022', '2', '5', '1', '1', null, '1', '0', '2017-03-23 13:51:59', '1114', '1115', 're-do section with gene info');
INSERT INTO `center_outcomes` VALUES ('3', 'Local, national and international research and development partners, the private sector, decision-makers and livestock producers are able to diagnose feed constraints and opportunities and to effectively prioritize and target feed and forage interventions.', null, '2022', '4', '5', '3', '2', null, '1', '1', '2017-04-07 11:50:18', '1115', '1115', null);
INSERT INTO `center_outcomes` VALUES ('4', 'Research and development partners and service providers (private sector) are using up-to-date feed and forage technology which is cost effective (accurate and rapid)', null, '2022', '2', '5', '3', '2', null, '1', '1', '2017-04-07 11:51:46', '1115', '1115', null);
INSERT INTO `center_outcomes` VALUES ('5', 'Novel genetic variation underpinning desirable traits identified; genetic gains in breeding programs accelerated; productive, nutritious and/or environmentally sustainable germplasm incorporated into agrifood systems', null, '2050', null, '-1', '1', '1', null, '1', '1', '2017-04-07 14:01:34', '1115', '1115', null);
INSERT INTO `center_outcomes` VALUES ('6', 'Crop diversity conserved in regional genebanks more widely and effectively used to enhance and diversify tropical food systems', null, '2050', null, '-1', '1', '4', null, '1', '1', '2017-04-07 13:59:38', '1115', '1115', null);
INSERT INTO `center_outcomes` VALUES ('7', 'Novel genetic variation underpinning desirable traits identified; genetic gains in breeding programs accelerated; productive, nutritious and/or environmentally sustainable germplasm incorporated into agrifood systems', null, '2050', null, '-1', '1', '3', null, '1', '1', '2017-04-07 14:00:48', '1115', '1115', null);
INSERT INTO `center_outcomes` VALUES ('8', 'Policy makers incorporate Ecosystem services social and economic value in land use policy and planning. ', 'incorporate in landuse plannig', '2021', '4', '26', '5', '5', null, '1', '1', '2017-04-24 09:28:11', '1109', '1115', null);
INSERT INTO `center_outcomes` VALUES ('9', 'Policy makers design and implement interventions to improve food and nutrition security under a changing climate and promote Climate Smart Agriculture based on CIAT climate change science', 'Food and Nutrition security', '2022', '3', '42', '6', '7', null, '1', '1', '2017-05-02 16:13:25', '1114', '1114', null);
INSERT INTO `center_outcomes` VALUES ('10', 'Organizations and Institutions adapt plans and direct investments to optimise consumption of diverse nutrient-rich foods, with all plans and investments examined for their gender implications', 'Plans and Investments ', '2022', '5', '42', '6', '7', null, '1', '1', '2017-05-02 12:28:27', '1114', '1114', null);
INSERT INTO `center_outcomes` VALUES ('11', 'Purchasing  companies and small-scale producers participate in and benefit from inclusive business ', 'Inclusive Business Benefits', '2022', '11', '42', '7', '11', null, '1', '1', '2017-06-23 14:22:30', '1115', '1115', null);
INSERT INTO `center_outcomes` VALUES ('12', 'Improved cassava value chain in LAC and the ASEAN region', 'Value Chain Strategies', '2022', '5', '23', '8', '17', null, '1', '1', '2017-06-15 13:16:50', '1115', '1115', null);
INSERT INTO `center_outcomes` VALUES ('13', 'Increased stability and performance of cassava production', 'Cassava production performance', '2022', '1000', '11', '8', '15', null, '1', '1', '2017-06-15 13:19:37', '1115', '1115', null);
INSERT INTO `center_outcomes` VALUES ('14', 'New crop processing technologies promoted and adopted for cassava micronutrient conservation and commercial value', 'New crop processing technologi', '2022', '1000', '11', '8', '16', null, '1', '1', '2017-06-15 13:18:01', '1115', '1115', null);
INSERT INTO `center_outcomes` VALUES ('15', 'Adoption of Cassava varieties by farmers', 'Adoption of cassava varieties', '2022', '100000', '11', '8', '12', null, '1', '1', '2017-06-15 13:23:46', '1115', '1115', null);
INSERT INTO `center_outcomes` VALUES ('16', 'Practices, tools and technologies implemented to make cassava productive, sustainable and economically viable in LAC and ASEAN region', 'Agro Practices Adopted', '2022', '1000', '11', '8', '13', null, '1', '1', '2017-06-15 13:22:34', '1115', '1115', null);
INSERT INTO `center_outcomes` VALUES ('17', 'Improved cassava health to safeguard the quality of planting materials and productivity in LAC and ASEAN', 'Pest and Disease Monitoring', '2022', '500', '11', '8', '14', null, '1', '1', '2017-06-15 13:21:49', '1115', '1115', null);
INSERT INTO `center_outcomes` VALUES ('18', 'Farm households adopting Climate Smart Agriculture practices and technologies that potentially reduce production risks', 'Adoption of CSA practices', '2022', '1000000', '11', '6', '8', null, '1', '1', '2017-05-16 09:55:18', '1114', '1114', null);
INSERT INTO `center_outcomes` VALUES ('19', '', '', '2014', '0', '1', null, '18', null, '1', '0', '2017-05-24 11:33:28', '1115', '1057', 'test');
INSERT INTO `center_outcomes` VALUES ('20', 'Development organisations, with the focus on investments for CSA activities, adapting their plans or directing investment to increase women’s access to, and control over, productive assets and resources', 'Investments for CSA activities', '2022', '5', '9', '6', '8', null, '1', '1', '2017-05-16 10:10:24', '1114', '1114', null);
INSERT INTO `center_outcomes` VALUES ('21', '', '', '2014', null, '-1', null, '18', null, '1', '1', '2017-05-24 11:35:23', '1115', '1057', null);
INSERT INTO `center_outcomes` VALUES ('22', '', '', '2014', null, '-1', '6', '8', null, '1', '0', '2017-05-24 11:35:59', '1114', '1057', 'no accurate');
INSERT INTO `center_outcomes` VALUES ('23', 'Decision makers promote the implementation of  Climate Smart Technologies and Practices including Climate Site Specific Management Systems', 'Implementation of CST', '2022', '3', '32', '6', '8', null, '1', '1', '2017-05-16 10:44:36', '1114', '1114', null);
INSERT INTO `center_outcomes` VALUES ('24', null, null, '-1', null, null, null, '8', null, '1', '0', '2017-05-16 10:44:43', '1114', '1114', 'typo');
INSERT INTO `center_outcomes` VALUES ('25', 'Policy makers incorporate agricultural development initiatives where CCAFS science is used to target and implement interventions to increase input efficiency', 'Agricultural development initi', '2022', '3', '32', '6', '9', null, '1', '1', '2017-05-16 10:56:31', '1114', '1114', null);
INSERT INTO `center_outcomes` VALUES ('26', null, null, '-1', null, null, null, '11', null, '1', '0', '2017-05-17 08:35:19', '1115', '1115', 'test');
INSERT INTO `center_outcomes` VALUES ('27', null, null, '-1', null, null, null, '11', null, '1', '1', '2017-05-17 08:48:39', '1115', '1115', null);
INSERT INTO `center_outcomes` VALUES ('28', 'Understanding  the use of targeting rice technologies to farmers and producers', 'Understanding of targeting tec', '2022', '4', '39', '12', '19', null, '1', '1', '2017-05-17 16:35:51', '1114', '1114', null);
INSERT INTO `center_outcomes` VALUES ('29', 'Rice crop performance and adaptation enhaced through a worldwide field laboratory', 'Crop performance', '2022', '3', '4', '12', '20', null, '1', '1', '2017-06-15 10:05:55', '1114', '1114', null);
INSERT INTO `center_outcomes` VALUES ('30', 'Researchers and partners tested and evaluated promising genetic lines', 'Phenotyping tools', '2022', '10', '39', '12', '21', null, '1', '1', '2017-05-23 10:28:27', '1114', '1114', null);
INSERT INTO `center_outcomes` VALUES ('31', null, null, '-1', null, null, null, '20', null, '1', '0', '2017-05-17 15:48:41', '1114', '1114', 'wrong outcome');
INSERT INTO `center_outcomes` VALUES ('32', 'Technological solutions developed to tackle biotic factors in rice plants', 'Genetics of rice plant ', '2022', '5', '39', '12', '22', null, '1', '1', '2017-05-23 14:57:12', '1114', '1114', null);
INSERT INTO `center_outcomes` VALUES ('33', 'Researchers tested and identified genomic associations in multi-environmental trials', 'Genotypic and Phenotypic metho', '2022', '10', '39', '12', '23', null, '1', '1', '2017-05-23 15:02:30', '1114', '1114', null);
INSERT INTO `center_outcomes` VALUES ('34', 'Research and knowledge shared on data rice', 'Big data platform', '2022', '20', '39', '12', '24', null, '1', '1', '2017-05-18 15:51:02', '1114', '1114', null);
INSERT INTO `center_outcomes` VALUES ('35', 'Rice varieties with novel genes developed, tested and validated in field by farmers and partners', 'Rice varieties developed', '2022', '100', '11', '12', '25', null, '1', '1', '2017-05-26 12:19:41', '1114', '1114', null);
INSERT INTO `center_outcomes` VALUES ('36', null, null, '-1', null, null, null, '11', null, '1', '1', '2017-05-24 14:21:48', '1115', '1115', null);
INSERT INTO `center_outcomes` VALUES ('37', null, null, '-1', null, null, null, '12', null, '1', '0', '2017-05-25 15:44:47', '1106', '1115', 'test');
INSERT INTO `center_outcomes` VALUES ('38', null, null, '-1', null, null, null, '11', null, '1', '1', '2017-06-02 10:18:18', '1115', '1115', null);
INSERT INTO `center_outcomes` VALUES ('39', 'Tools and models on genomic selection for precision breeding developed and available for plant breeders', 'tools and models genomic selec', '2022', '1', '28', '12', '26', null, '1', '1', '2017-06-12 12:54:38', '1114', '1114', null);
INSERT INTO `center_outcomes` VALUES ('40', 'At least two new rice varieties with higher yield released by partners and adopted by farmers in LAC.', 'Rice variety releases', '2022', '1000', '11', '12', '27', null, '1', '1', '2017-06-12 14:16:44', '1114', '1114', null);
INSERT INTO `center_outcomes` VALUES ('41', 'At least two new rice stress-tolerant varieties released by partners and adopted by farmers in LAC.', 'stress-tolerant varietites', '2022', '100', '11', '12', '28', null, '1', '1', '2017-06-12 14:44:45', '1114', '1114', null);
INSERT INTO `center_outcomes` VALUES ('42', 'At least two rice varieities with 25 ppm Zinc tested and validated for release by partners', 'Rice zinc varieties', '2022', '100', '11', '12', '29', null, '1', '1', '2017-06-12 14:54:23', '1114', '1114', null);
INSERT INTO `center_outcomes` VALUES ('43', null, null, '-1', null, null, null, '11', null, '1', '1', '2017-06-15 08:39:32', '1115', '1115', null);

-- ----------------------------
-- Table structure for center_outputs
-- ----------------------------
DROP TABLE IF EXISTS `center_outputs`;
CREATE TABLE `center_outputs` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `title` text,
  `short_name` text,
  `date_added` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `research_outcome_id` int(11) NOT NULL,
  `is_active` tinyint(1) DEFAULT NULL,
  `active_since` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `fk_output_outcome` (`research_outcome_id`) USING BTREE,
  KEY `fk_output_created_by` (`created_by`) USING BTREE,
  KEY `fk_output_modified_by` (`modified_by`) USING BTREE,
  CONSTRAINT `center_outputs_ibfk_1` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `center_outputs_ibfk_2` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`),
  CONSTRAINT `center_outputs_ibfk_3` FOREIGN KEY (`research_outcome_id`) REFERENCES `center_outcomes` (`id`) ON DELETE NO ACTION ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=95 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of center_outputs
-- ----------------------------
INSERT INTO `center_outputs` VALUES ('1', 'Research priority setting based on ex-ante and ex-post impact assessments of feed and forage innovations.      ', '', '2017-03-23 13:52:12', '1', '0', '2017-05-24 11:34:49', '1114', '1057', 'redo with gene');
INSERT INTO `center_outputs` VALUES ('2', 'Refined local, national and regional feed supply and demand analysis and option tools                                 ', null, '2017-03-23 13:53:58', '1', '0', '2017-03-23 14:13:41', '1114', '1115', 're-do with gene info');
INSERT INTO `center_outputs` VALUES ('3', 'Research priority setting based on ex-ante and ex-post impact assessments of feed and forage innovations.      ', null, '2017-04-07 11:52:40', '3', '1', '2017-04-07 11:53:22', '1115', '1115', null);
INSERT INTO `center_outputs` VALUES ('4', 'Refined local, national and regional feed supply and demand analysis and option tools             ', null, '2017-04-07 11:53:47', '3', '1', '2017-04-07 11:54:08', '1115', '1115', null);
INSERT INTO `center_outputs` VALUES ('5', 'Bean, cassava and tropical-forage collections savely conserved and backed-up', null, '2017-04-07 14:06:31', '5', '1', '2017-04-07 14:07:51', '1115', '1115', null);
INSERT INTO `center_outputs` VALUES ('6', 'Majority of (prioritized) bean, cassava and tropical-forage accessions of satisfactory quality and available in sufficient quantities for immediate distribution to genebank clients', null, '2017-04-07 14:08:07', '5', '1', '2017-04-07 14:08:43', '1115', '1115', null);
INSERT INTO `center_outputs` VALUES ('7', 'Bean, cassava and tropical-forage collections curated and structured according to genetic relationships', null, '2017-04-07 14:08:57', '5', '1', '2017-04-07 14:09:42', '1115', '1115', null);
INSERT INTO `center_outputs` VALUES ('8', 'Knowledge connecting genetic diversity of beans, cassava and tropical forages to traits affecting the productivity, resilience and nutritional value of agrifood systems', null, '2017-04-07 14:10:05', '7', '1', '2017-04-07 14:11:14', '1115', '1115', null);
INSERT INTO `center_outputs` VALUES ('9', 'Scientific interactions and collaborative projects with regional genebanks', null, '2017-04-07 14:11:33', '6', '1', '2017-04-07 14:12:12', '1115', '1115', null);
INSERT INTO `center_outputs` VALUES ('10', 'Capacity building in linkage with regional universities and ARIs elsewhere', null, '2017-04-07 14:12:29', '6', '1', '2017-04-07 14:13:12', '1115', '1115', null);
INSERT INTO `center_outputs` VALUES ('11', 'Knowledge for policy makers on socio-economic benefits of mainstreaming ES in landscape management taking gender into account.', 'knowledge for policy makers', '2017-04-17 15:22:15', '8', '1', '2017-04-24 08:37:40', '1109', '1115', null);
INSERT INTO `center_outputs` VALUES ('12', 'Knowledge for government, private sector, development agencies, and farmers on socio-economic benefits of mainstreaming ES in landscape management taking gender into account. ', 'knowledge for gov/private/ngos', '2017-04-17 15:29:08', '8', '1', '2017-04-24 08:38:22', '1109', '1115', null);
INSERT INTO `center_outputs` VALUES ('13', 'Learning alliances, strategic engagements and partnerships that promote capacity building, learning and co-creation and disseminate CSA information and tools', 'alliances, partnerships ', '2017-05-02 15:53:20', '9', '1', '2017-05-24 11:34:37', '1114', '1057', null);
INSERT INTO `center_outputs` VALUES ('14', 'Tools and methods for the design and evaluation of  business models in terms of efficiency, nutrition, gender, and environmental sustainability/climate-smart value chains.', 'Tools & Methods', '2017-05-03 11:09:31', '11', '1', '2017-05-24 11:34:26', '1115', '1057', null);
INSERT INTO `center_outputs` VALUES ('15', 'Foresight and impact assessment analysis to understand cassava value chain process taking into account gender', 'Impact Assessment Analysis', '2017-05-05 10:41:28', '12', '1', '2017-05-05 10:42:13', '1115', '1115', null);
INSERT INTO `center_outputs` VALUES ('16', 'Platforms and technologies that allow cassava small-scale producers (men and women) to participate in value chain decision making', 'Platforms and Technologies', '2017-05-05 10:42:26', '12', '1', '2017-05-05 10:49:56', '1115', '1115', null);
INSERT INTO `center_outputs` VALUES ('17', 'Recommendations for effective extension, market and cost effective approaches to improve cassava value chain', 'Recommendations', '2017-05-05 10:50:07', '12', '1', '2017-05-05 10:50:36', '1115', '1115', null);
INSERT INTO `center_outputs` VALUES ('18', 'Capacity development on gender and youth engagement in market innovations', 'Cap Dev on Gender & Youth', '2017-05-05 10:50:53', '12', '1', '2017-05-05 10:51:29', '1115', '1115', null);
INSERT INTO `center_outputs` VALUES ('19', 'Somatic embryogenesis protocols for cassava standardised', 'Protocols', '2017-05-05 14:14:18', '13', '1', '2017-05-05 14:16:12', '1115', '1115', null);
INSERT INTO `center_outputs` VALUES ('20', 'Implementation of seed decontamination protocols', 'Decontamination Protocols', '2017-05-05 14:16:23', '13', '1', '2017-05-05 14:17:10', '1115', '1115', null);
INSERT INTO `center_outputs` VALUES ('21', 'Baseline surveys of geographical distribution and spread of cassava varieties', 'Surveys', '2017-05-05 14:18:09', '13', '1', '2017-05-05 14:18:36', '1115', '1115', null);
INSERT INTO `center_outputs` VALUES ('22', 'Development of artificial seed production technology', 'Artificial Seed Technology', '2017-05-05 14:19:35', '13', '1', '2017-05-05 14:20:12', '1115', '1115', null);
INSERT INTO `center_outputs` VALUES ('23', 'Mechanized stake harvesting for high quality seed production', 'Mechanized Harvesting', '2017-05-05 14:21:14', '13', '1', '2017-05-05 14:25:42', '1115', '1115', null);
INSERT INTO `center_outputs` VALUES ('24', 'Capacity building on clean seed production', 'Cap Dev Clean Seed Production', '2017-05-05 14:25:57', '13', '1', '2017-05-05 14:26:25', '1115', '1115', null);
INSERT INTO `center_outputs` VALUES ('25', 'Post-harvest innovation to better manage products and waste', 'Manage Product and Waste', '2017-05-05 15:18:08', '14', '1', '2017-05-05 15:18:43', '1115', '1115', null);
INSERT INTO `center_outputs` VALUES ('26', 'Sensory analysis and assessments of end user preferences for fresh and processed foods', 'End User Preference Analysis', '2017-05-05 15:19:02', '14', '1', '2017-05-05 15:19:48', '1115', '1115', null);
INSERT INTO `center_outputs` VALUES ('27', 'contribute to increase consumption of nutritious cassava foods', 'Increase Consumption', '2017-05-05 15:22:26', '14', '1', '2017-05-05 15:24:57', '1115', '1115', null);
INSERT INTO `center_outputs` VALUES ('28', 'Diversification of cassava starch uses', 'Starch Uses', '2017-05-05 15:25:16', '14', '1', '2017-05-05 15:28:53', '1115', '1115', null);
INSERT INTO `center_outputs` VALUES ('29', 'New methods to assess quality traits of preferred cassava varieties', 'Trait Assessment Methods', '2017-05-05 15:29:42', '14', '1', '2017-05-05 15:30:34', '1115', '1115', null);
INSERT INTO `center_outputs` VALUES ('30', 'Develop equipment adapted to cassava processing at small and medium scale', 'Cassava Processing', '2017-05-05 15:30:57', '14', '1', '2017-05-05 15:31:45', '1115', '1115', null);
INSERT INTO `center_outputs` VALUES ('31', 'Develop high-throughput methods, including NIRS, to elucidate the links between consumer preferences and product quality', 'Throughput Methods', '2017-05-05 15:32:28', '14', '1', '2017-05-05 15:33:28', '1115', '1115', null);
INSERT INTO `center_outputs` VALUES ('32', 'New uses for cassava in animal feed', 'New Users for Feed', '2017-05-05 15:48:28', '14', '1', '2017-05-05 15:48:54', '1115', '1115', null);
INSERT INTO `center_outputs` VALUES ('33', 'Genome sequencing for genotyping profiling and diversity analysis', 'Genome Sequencing', '2017-05-08 08:09:48', '15', '1', '2017-05-08 08:10:36', '1115', '1115', null);
INSERT INTO `center_outputs` VALUES ('34', 'Genetic stocks and populations development for trait discovery', 'Dev. for Trait Discovery', '2017-05-08 08:10:54', '15', '1', '2017-05-08 08:11:36', '1115', '1115', null);
INSERT INTO `center_outputs` VALUES ('35', 'Molecular marker assisted breeding to optimize process at the gene level', 'Molecular Marker Breeding', '2017-05-08 08:11:50', '15', '1', '2017-05-08 08:12:29', '1115', '1115', null);
INSERT INTO `center_outputs` VALUES ('36', 'Tools and protocols for genome editing', 'tools and protocols', '2017-05-08 08:12:50', '15', '1', '2017-05-08 08:13:11', '1115', '1115', null);
INSERT INTO `center_outputs` VALUES ('37', 'Qualitative research to differentiate preferred cassava traits by gender', 'Qualitative Research: Gender', '2017-05-08 08:13:30', '15', '1', '2017-05-08 08:14:30', '1115', '1115', null);
INSERT INTO `center_outputs` VALUES ('38', 'High-Throughput phenotyping tools and technologies for trait definition', 'Phenotyping tools', '2017-05-08 08:14:47', '15', '1', '2017-05-08 08:15:24', '1115', '1115', null);
INSERT INTO `center_outputs` VALUES ('39', 'Data and Knowledge Management', 'Data & Knowledge Mgmt', '2017-05-08 08:15:39', '15', '1', '2017-05-08 08:16:56', '1115', '1115', null);
INSERT INTO `center_outputs` VALUES ('40', 'Optimized land use agronomic practices', 'Land use agro practices', '2017-05-08 08:23:55', '16', '1', '2017-05-08 08:24:46', '1115', '1115', null);
INSERT INTO `center_outputs` VALUES ('41', 'Soil management tools', 'Soil mgmt Tools', '2017-05-08 08:25:00', '16', '1', '2017-05-08 08:32:54', '1115', '1115', null);
INSERT INTO `center_outputs` VALUES ('42', 'Develop a system for monitoring change in soil properties over time', 'system for monitoring soil ', '2017-05-08 08:36:11', '16', '1', '2017-05-08 08:53:48', '1115', '1115', null);
INSERT INTO `center_outputs` VALUES ('43', 'Maps of functional soil properties at appropriate resolutions', 'Maps of Soil properties', '2017-05-08 08:54:10', '16', '1', '2017-05-08 08:55:08', '1115', '1115', null);
INSERT INTO `center_outputs` VALUES ('44', 'Develop a system to forecast future condition of soil', 'Forecast System', '2017-05-08 08:55:27', '16', '1', '2017-05-08 08:56:00', '1115', '1115', null);
INSERT INTO `center_outputs` VALUES ('45', 'Microbial treatment to improve soil fertility - Microbiomics', 'Microbial Treatment', '2017-05-08 08:56:40', '16', '1', '2017-05-08 08:57:17', '1115', '1115', null);
INSERT INTO `center_outputs` VALUES ('46', 'Chemical treatment to improve soil fertility', 'Chemical Treatment', '2017-05-08 08:58:20', '16', '1', '2017-05-08 08:59:29', '1115', '1115', null);
INSERT INTO `center_outputs` VALUES ('47', 'Crop rotation system', 'Crop Rotation System', '2017-05-08 08:59:50', '16', '1', '2017-05-08 09:01:19', '1115', '1115', null);
INSERT INTO `center_outputs` VALUES ('48', 'Biological crop protection solutions - Beyond today’s control strategy', 'Crop Protection Solutions', '2017-05-08 09:04:39', '17', '1', '2017-05-08 09:05:40', '1115', '1115', null);
INSERT INTO `center_outputs` VALUES ('49', 'Identification and evaluation of next generation insecticides', 'Insecticides', '2017-05-08 09:05:58', '17', '1', '2017-05-08 09:07:22', '1115', '1115', null);
INSERT INTO `center_outputs` VALUES ('50', 'Baseline surveys of geographical spread of cassava pests and diseases in LAC & ASEAN regions', 'Baseline Surveys', '2017-05-08 09:07:46', '17', '1', '2017-05-08 09:08:15', '1115', '1115', null);
INSERT INTO `center_outputs` VALUES ('51', 'System to screen for natural resistance to pest and diseases', 'system screen pest & diseases', '2017-05-08 09:08:57', '17', '1', '2017-05-08 09:12:52', '1115', '1115', null);
INSERT INTO `center_outputs` VALUES ('52', 'Seed treatment to elicit natural resistance and reduce pest and disease attack during establishment', 'Seed Treatment', '2017-05-08 09:13:11', '17', '1', '2017-05-08 09:13:39', '1115', '1115', null);
INSERT INTO `center_outputs` VALUES ('53', 'LAC and Asia disease and pest surveillance system', 'Surveillance System', '2017-05-08 09:14:04', '17', '1', '2017-05-08 09:14:40', '1115', '1115', null);
INSERT INTO `center_outputs` VALUES ('54', 'Cassava disease diagnostic tools and portable field detection system', 'Diagnostic Tools', '2017-05-08 09:15:13', '17', '1', '2017-05-08 09:16:07', '1115', '1115', null);
INSERT INTO `center_outputs` VALUES ('55', 'Geo databases to set priorities of pest and diseases', 'Geo databases', '2017-05-08 09:16:29', '17', '1', '2017-05-08 09:17:03', '1115', '1115', null);
INSERT INTO `center_outputs` VALUES ('56', 'Establish a physical network of field laboratories and trial sites (LAC) to enhance crop performance and adaptation to climate variability and climate change (GxExM)', 'network of field laboratories', '2017-05-17 12:12:03', '29', '0', '2017-05-17 12:13:38', '1114', '1114', 'wrong output');
INSERT INTO `center_outputs` VALUES ('57', 'Global phenotyping tools \r\n\r\n\r\n\r\n', 'Phenotyping tools', '2017-05-17 12:13:44', '29', '0', '2017-05-17 12:17:03', '1114', '1114', 'wrong output');
INSERT INTO `center_outputs` VALUES ('58', 'Establishing a worldwide field laboratory\r\n\r\n\r\n\r\n', 'Field Laboratory', '2017-05-17 12:15:13', '29', '0', '2017-05-17 12:15:56', '1114', '1114', 'wrong output');
INSERT INTO `center_outputs` VALUES ('59', 'Genetics of rice plant interaction with the biotic environment', 'Genetics of rice plant ', '2017-05-17 12:17:09', '29', '0', '2017-05-17 12:17:45', '1114', '1114', 'wrong output');
INSERT INTO `center_outputs` VALUES ('60', ' Discovery of genomic associations', 'Genomic associations', '2017-05-17 12:17:49', '29', '0', '2017-05-17 12:18:17', '1114', '1114', 'wrong output');
INSERT INTO `center_outputs` VALUES ('61', 'Big Data integration platform', 'Big data platform', '2017-05-17 12:18:22', '29', '0', '2017-05-17 12:19:01', '1114', '1114', 'wrong output');
INSERT INTO `center_outputs` VALUES ('62', 'Harnessing rice diversity for target traits in breeding', 'Harnessing rice diversity', '2017-05-17 12:49:03', '30', '0', '2017-05-17 12:55:14', '1114', '1114', 'wrong output');
INSERT INTO `center_outputs` VALUES ('63', 'Precision upstream breeding to enhance rice traits', 'Upstream breeding', '2017-05-17 12:55:20', '30', '0', '2017-05-17 13:02:10', '1114', '1114', 'wrong output');
INSERT INTO `center_outputs` VALUES ('64', 'Intensive systems for the use of new germsplam resources', 'Intensive systems', '2017-05-17 12:56:28', '30', '0', '2017-05-17 12:58:33', '1114', '1114', 'wrong output');
INSERT INTO `center_outputs` VALUES ('65', 'New rice lines suitable for unfavorable ecosystems', '', '2017-05-17 12:58:38', '30', '0', '2017-05-17 13:02:47', '1114', '1114', 'wrong output');
INSERT INTO `center_outputs` VALUES ('66', null, null, '2017-05-17 13:02:20', '30', '0', '2017-05-17 13:02:20', '1114', '1114', 'wrong output');
INSERT INTO `center_outputs` VALUES ('67', 'Research knowledge on Rice grain quality and nutrition to enhance rice varieties', 'Rice grain quality and nutriti', '2017-05-17 13:03:06', '30', '0', '2017-05-17 13:06:11', '1114', '1114', 'wrong output');
INSERT INTO `center_outputs` VALUES ('68', 'GRiSP indica panel of 300 lines  screened for Rice hoja blanca virus (RHBV), blast and high temperature', 'Panel of 300 lines screened', '2017-06-12 12:00:04', '35', '1', '2017-06-12 12:02:43', '1114', '1114', null);
INSERT INTO `center_outputs` VALUES ('69', 'Gene discovery for root architecture, drought tolerance, RHBV and high temperature and promising transgenic lines identified for water and Nitrogen use efficiency; root architecture QTLs (five) introgressed in at least two Colombian rice varieties, genes controlling grain quality traits for LAC- CIAT ', 'Gene discovery and transgenic ', '2017-06-12 12:03:46', '35', '1', '2017-06-12 12:13:39', '1114', '1114', null);
INSERT INTO `center_outputs` VALUES ('70', null, null, '2017-06-12 12:13:45', '35', '1', '2017-06-12 12:13:45', '1114', '1114', null);
INSERT INTO `center_outputs` VALUES ('71', 'Prediction model for genomic selection', '', '2017-06-12 12:54:56', '39', '1', '2017-06-12 12:56:45', '1114', '1114', null);
INSERT INTO `center_outputs` VALUES ('72', 'Multilocation evaluation of approximately  300 lines across 20 locations in Asia, Africa and Latin America.   ', '', '2017-06-12 14:19:53', '40', '1', '2017-06-12 14:21:11', '1114', '1114', null);
INSERT INTO `center_outputs` VALUES ('73', 'Five lines from GRiSP with 5% higher yield nominated for release.  ', '', '2017-06-12 14:21:17', '40', '1', '2017-06-12 14:21:35', '1114', '1114', null);
INSERT INTO `center_outputs` VALUES ('74', 'Establish a physical network of field laboratories and trial sites (LAC) to enhance crop performance and adaptation to climate variability and climate change (GxExM)', 'network of field laboratories', '2017-06-12 15:16:23', '29', '1', '2017-06-12 15:17:09', '1114', '1114', null);
INSERT INTO `center_outputs` VALUES ('75', 'Long term observatory of disease impacts on yield, yield variability and grain quality representative sets of genetic diversity', 'Long term observatory', '2017-06-12 15:17:22', '29', '1', '2017-06-12 15:17:53', '1114', '1114', null);
INSERT INTO `center_outputs` VALUES ('76', 'Develop and use models to quantify and map the impact of abiotic/biotic factors on yield', 'Models and maps', '2017-06-12 15:18:04', '29', '1', '2017-06-12 15:18:45', '1114', '1114', null);
INSERT INTO `center_outputs` VALUES ('77', 'Effects of climate change on rice determined (CCAFs principle and Target Prone Environment definition) using past, present and future climates', 'climate change effects', '2017-06-12 15:19:04', '29', '1', '2017-06-12 15:19:22', '1114', '1114', null);
INSERT INTO `center_outputs` VALUES ('78', 'Establish a physical network of field laboratories and trial sites (LAC) to enhance crop performance and adaptation to climate variability and climate change (GxExM)', 'physical network of filed labo', '2017-06-12 15:21:49', '30', '0', '2017-06-12 15:46:21', '1114', '1114', 'wrong output');
INSERT INTO `center_outputs` VALUES ('79', 'Long term observatory of disease impacts on yield, yield variability and grain quality representative sets of genetic diversity', 'Observatory of disease impacts', '2017-06-12 15:44:47', '30', '0', '2017-06-12 15:47:01', '1114', '1114', 'wrong output');
INSERT INTO `center_outputs` VALUES ('80', 'Phenotyping network representative for each ecosystem and breeding environment ', 'Phenotyping network', '2017-06-12 15:48:12', '30', '1', '2017-06-12 15:52:29', '1114', '1114', null);
INSERT INTO `center_outputs` VALUES ('81', 'Establishing of new or /Adaptation of  High-throughput (HTP) phenotypic tools for intensive, unfavorable rice production system and for grain quality ', '', '2017-06-12 15:52:35', '30', '1', '2017-06-12 15:52:55', '1114', '1114', null);
INSERT INTO `center_outputs` VALUES ('82', 'Identification of traits of interest for climate variability and climate change adaptation', 'Traits of interest', '2017-06-12 15:53:46', '30', '1', '2017-06-12 15:54:14', '1114', '1114', null);
INSERT INTO `center_outputs` VALUES ('83', 'Identification of promising genetic donors for intensive and unfavorable rice production systems ', 'Promising genetic donors', '2017-06-12 15:54:20', '30', '1', '2017-06-12 15:54:42', '1114', '1114', null);
INSERT INTO `center_outputs` VALUES ('84', 'Development of cost effective methods and protocols for field diagnosis ', 'Methods and protocols', '2017-06-12 15:55:05', '32', '1', '2017-06-12 15:56:21', '1114', '1114', null);
INSERT INTO `center_outputs` VALUES ('85', 'Identification of new disease resistance genes ', 'New disease resistance genes', '2017-06-12 15:56:27', '32', '1', '2017-06-12 15:56:56', '1114', '1114', null);
INSERT INTO `center_outputs` VALUES ('86', 'Monitoring pathogen populations diversity in specific environments and hot spots screening ', 'Monitoring pathogen ', '2017-06-12 15:57:01', '32', '1', '2017-06-12 15:57:24', '1114', '1114', null);
INSERT INTO `center_outputs` VALUES ('87', 'Genes profiles developed to predict performance of gene combinations and genetic gain ', 'Breeding lines', '2017-06-13 12:08:55', '41', '1', '2017-06-13 14:42:59', '1114', '1114', null);
INSERT INTO `center_outputs` VALUES ('88', 'Consumer and market oriented product profile developed keeping  traits preferred for 2 markets', 'Market product profile', '2017-06-13 14:43:08', '41', '1', '2017-06-13 14:44:06', '1114', '1114', null);
INSERT INTO `center_outputs` VALUES ('89', 'New breeding lines with higher yield and  at least 8% reduced loss under abiotic stress in rainfed environment developed', 'New breeding lines for rainfed', '2017-06-13 14:44:20', '41', '1', '2017-06-13 14:48:08', '1114', '1114', null);
INSERT INTO `center_outputs` VALUES ('90', ' New breeding lines with higher yield and  at least 5% reduced loss under abiotic stress in upland environment developed.', 'New breeding lines upland ', '2017-06-13 14:49:11', '41', '1', '2017-06-13 14:49:38', '1114', '1114', null);
INSERT INTO `center_outputs` VALUES ('91', null, null, '2017-06-13 14:49:58', '41', '0', '2017-06-13 14:49:58', '1114', '1114', 'wrong output');
INSERT INTO `center_outputs` VALUES ('92', 'Develop cooking quality markers (amylose, rheology and texture)', 'Quality markers', '2017-06-13 14:50:51', '42', '1', '2017-06-13 14:52:34', '1114', '1114', null);
INSERT INTO `center_outputs` VALUES ('93', ' Rice improvement for Zinc content. ', 'Rice improvement Zinc', '2017-06-13 14:52:51', '42', '1', '2017-06-13 14:53:12', '1114', '1114', null);
INSERT INTO `center_outputs` VALUES ('94', null, null, '2017-06-16 09:50:16', '28', '1', '2017-06-16 09:50:16', '1114', '1114', null);

-- ----------------------------
-- Table structure for center_outputs_next_users
-- ----------------------------
DROP TABLE IF EXISTS `center_outputs_next_users`;
CREATE TABLE `center_outputs_next_users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `research_output_id` int(11) DEFAULT NULL,
  `nextuser_type_id` int(11) DEFAULT NULL,
  `active_since` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `is_active` tinyint(1) DEFAULT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `fk_output_nextsubuser` (`nextuser_type_id`) USING BTREE,
  KEY `fk_output_researchoutput` (`research_output_id`) USING BTREE,
  KEY `fk_outputnextsubusers_created_by` (`created_by`) USING BTREE,
  KEY `fk_outputnextsubusers_modified_by` (`modified_by`) USING BTREE,
  CONSTRAINT `center_outputs_next_users_ibfk_1` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `center_outputs_next_users_ibfk_2` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `center_outputs_next_users_ibfk_3` FOREIGN KEY (`research_output_id`) REFERENCES `center_outputs` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `center_outputs_next_users_ibfk_4` FOREIGN KEY (`nextuser_type_id`) REFERENCES `center_nextuser_types` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=112 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of center_outputs_next_users
-- ----------------------------
INSERT INTO `center_outputs_next_users` VALUES ('2', '1', '25', '2017-03-23 13:53:10', '1', '1114', '1114', '');
INSERT INTO `center_outputs_next_users` VALUES ('3', '1', '15', '2017-03-23 13:53:10', '1', '1114', '1114', '');
INSERT INTO `center_outputs_next_users` VALUES ('4', '2', '15', '2017-03-23 14:12:22', '1', '1', '1', '');
INSERT INTO `center_outputs_next_users` VALUES ('5', '2', null, '2017-03-23 14:12:22', '0', '1', '1', '');
INSERT INTO `center_outputs_next_users` VALUES ('6', '3', '25', '2017-04-07 11:53:22', '1', '1115', '1115', '');
INSERT INTO `center_outputs_next_users` VALUES ('7', '3', '15', '2017-04-07 11:53:22', '1', '1115', '1115', '');
INSERT INTO `center_outputs_next_users` VALUES ('8', '4', '15', '2017-04-07 11:54:08', '1', '1115', '1115', '');
INSERT INTO `center_outputs_next_users` VALUES ('9', '5', '20', '2017-04-07 14:07:51', '1', '1115', '1115', '');
INSERT INTO `center_outputs_next_users` VALUES ('10', '5', '22', '2017-04-07 14:07:51', '1', '1115', '1115', '');
INSERT INTO `center_outputs_next_users` VALUES ('11', '5', '25', '2017-04-07 14:07:51', '1', '1115', '1115', '');
INSERT INTO `center_outputs_next_users` VALUES ('12', '5', '24', '2017-04-07 14:07:51', '1', '1115', '1115', '');
INSERT INTO `center_outputs_next_users` VALUES ('14', '5', '18', '2017-04-07 14:07:51', '1', '1115', '1115', '');
INSERT INTO `center_outputs_next_users` VALUES ('15', '6', '18', '2017-04-07 14:08:43', '1', '1115', '1115', '');
INSERT INTO `center_outputs_next_users` VALUES ('16', '7', '18', '2017-04-07 14:09:42', '1', '1115', '1115', '');
INSERT INTO `center_outputs_next_users` VALUES ('17', '8', '18', '2017-04-07 14:11:14', '1', '1115', '1115', '');
INSERT INTO `center_outputs_next_users` VALUES ('20', '10', '18', '2017-04-07 14:13:12', '1', '1115', '1115', '');
INSERT INTO `center_outputs_next_users` VALUES ('21', '11', '4', '2017-04-17 15:22:50', '1', '1109', '1109', '');
INSERT INTO `center_outputs_next_users` VALUES ('22', '12', '15', '2017-04-17 15:30:54', '1', '1109', '1109', '');
INSERT INTO `center_outputs_next_users` VALUES ('24', '13', '9', '2017-05-02 15:56:44', '1', '1114', '1114', '');
INSERT INTO `center_outputs_next_users` VALUES ('25', '14', null, '2017-05-24 11:34:26', '1', '1115', '1057', '');
INSERT INTO `center_outputs_next_users` VALUES ('26', '15', '19', '2017-05-05 10:42:13', '1', '1115', '1115', '');
INSERT INTO `center_outputs_next_users` VALUES ('27', '16', '19', '2017-05-05 10:49:07', '1', '1115', '1115', '');
INSERT INTO `center_outputs_next_users` VALUES ('28', '17', '9', '2017-05-05 10:50:36', '1', '1115', '1115', '');
INSERT INTO `center_outputs_next_users` VALUES ('29', '18', '13', '2017-05-05 10:51:29', '1', '1115', '1115', '');
INSERT INTO `center_outputs_next_users` VALUES ('30', '19', '27', '2017-05-05 14:15:35', '1', '1115', '1115', '');
INSERT INTO `center_outputs_next_users` VALUES ('31', '20', '27', '2017-05-05 14:17:10', '1', '1115', '1115', '');
INSERT INTO `center_outputs_next_users` VALUES ('32', '21', '18', '2017-05-05 14:18:36', '1', '1115', '1115', '');
INSERT INTO `center_outputs_next_users` VALUES ('33', '22', '18', '2017-05-05 14:20:12', '1', '1115', '1115', '');
INSERT INTO `center_outputs_next_users` VALUES ('34', '23', '19', '2017-05-05 14:25:26', '1', '1115', '1115', '');
INSERT INTO `center_outputs_next_users` VALUES ('35', '23', '24', '2017-05-05 14:25:42', '1', '1115', '1115', '');
INSERT INTO `center_outputs_next_users` VALUES ('36', '24', '24', '2017-05-05 14:26:25', '1', '1115', '1115', '');
INSERT INTO `center_outputs_next_users` VALUES ('37', '25', '24', '2017-05-05 15:18:43', '1', '1115', '1115', '');
INSERT INTO `center_outputs_next_users` VALUES ('38', '26', '27', '2017-05-05 15:19:48', '1', '1115', '1115', '');
INSERT INTO `center_outputs_next_users` VALUES ('39', '27', '13', '2017-05-05 15:23:01', '1', '1115', '1115', '');
INSERT INTO `center_outputs_next_users` VALUES ('40', '28', '24', '2017-05-05 15:28:53', '1', '1115', '1115', '');
INSERT INTO `center_outputs_next_users` VALUES ('41', '29', '18', '2017-05-05 15:30:34', '1', '1115', '1115', '');
INSERT INTO `center_outputs_next_users` VALUES ('42', '30', '21', '2017-05-05 15:31:45', '1', '1115', '1115', '');
INSERT INTO `center_outputs_next_users` VALUES ('43', '31', '19', '2017-05-05 15:33:28', '1', '1115', '1115', '');
INSERT INTO `center_outputs_next_users` VALUES ('44', '32', '20', '2017-05-05 15:48:54', '1', '1115', '1115', '');
INSERT INTO `center_outputs_next_users` VALUES ('45', '33', '18', '2017-05-08 08:10:36', '1', '1115', '1115', '');
INSERT INTO `center_outputs_next_users` VALUES ('46', '34', '18', '2017-05-08 08:11:36', '1', '1115', '1115', '');
INSERT INTO `center_outputs_next_users` VALUES ('47', '35', '18', '2017-05-08 08:12:29', '1', '1115', '1115', '');
INSERT INTO `center_outputs_next_users` VALUES ('48', '36', '18', '2017-05-08 08:13:11', '1', '1115', '1115', '');
INSERT INTO `center_outputs_next_users` VALUES ('49', '37', '18', '2017-05-08 08:14:19', '1', '1115', '1115', '');
INSERT INTO `center_outputs_next_users` VALUES ('50', '38', '18', '2017-05-08 08:15:24', '1', '1115', '1115', '');
INSERT INTO `center_outputs_next_users` VALUES ('51', '39', '18', '2017-05-08 08:16:56', '1', '1115', '1115', '');
INSERT INTO `center_outputs_next_users` VALUES ('52', '40', '24', '2017-05-08 08:24:46', '1', '1115', '1115', '');
INSERT INTO `center_outputs_next_users` VALUES ('53', '41', '24', '2017-05-08 08:32:54', '1', '1115', '1115', '');
INSERT INTO `center_outputs_next_users` VALUES ('54', '42', '24', '2017-05-08 08:53:48', '1', '1115', '1115', '');
INSERT INTO `center_outputs_next_users` VALUES ('55', '43', '20', '2017-05-08 08:55:08', '1', '1115', '1115', '');
INSERT INTO `center_outputs_next_users` VALUES ('56', '44', '20', '2017-05-08 08:56:00', '1', '1115', '1115', '');
INSERT INTO `center_outputs_next_users` VALUES ('57', '45', '20', '2017-05-08 08:57:17', '1', '1115', '1115', '');
INSERT INTO `center_outputs_next_users` VALUES ('58', '46', '20', '2017-05-08 08:59:29', '1', '1115', '1115', '');
INSERT INTO `center_outputs_next_users` VALUES ('59', '47', '24', '2017-05-08 09:01:19', '1', '1115', '1115', '');
INSERT INTO `center_outputs_next_users` VALUES ('60', '48', '18', '2017-05-08 09:05:40', '1', '1115', '1115', '');
INSERT INTO `center_outputs_next_users` VALUES ('61', '49', '20', '2017-05-08 09:07:22', '1', '1115', '1115', '');
INSERT INTO `center_outputs_next_users` VALUES ('62', '50', '18', '2017-05-08 09:08:15', '1', '1115', '1115', '');
INSERT INTO `center_outputs_next_users` VALUES ('63', '51', '20', '2017-05-08 09:12:52', '1', '1115', '1115', '');
INSERT INTO `center_outputs_next_users` VALUES ('64', '52', '20', '2017-05-08 09:13:39', '1', '1115', '1115', '');
INSERT INTO `center_outputs_next_users` VALUES ('65', '53', '20', '2017-05-08 09:14:40', '1', '1115', '1115', '');
INSERT INTO `center_outputs_next_users` VALUES ('66', '54', '20', '2017-05-08 09:16:07', '1', '1115', '1115', '');
INSERT INTO `center_outputs_next_users` VALUES ('67', '55', '18', '2017-05-08 09:17:03', '1', '1115', '1115', '');
INSERT INTO `center_outputs_next_users` VALUES ('68', '55', '20', '2017-05-08 09:17:03', '1', '1115', '1115', '');
INSERT INTO `center_outputs_next_users` VALUES ('69', '56', '18', '2017-05-17 12:13:11', '1', '1114', '1114', '');
INSERT INTO `center_outputs_next_users` VALUES ('70', '56', '31', '2017-05-17 12:13:11', '1', '1114', '1114', '');
INSERT INTO `center_outputs_next_users` VALUES ('71', '56', '9', '2017-05-17 12:13:38', '1', '1114', '1114', '');
INSERT INTO `center_outputs_next_users` VALUES ('72', '58', '20', '2017-05-17 12:15:36', '1', '1114', '1114', '');
INSERT INTO `center_outputs_next_users` VALUES ('73', '58', '31', '2017-05-17 12:15:56', '1', '1114', '1114', '');
INSERT INTO `center_outputs_next_users` VALUES ('74', '57', '31', '2017-05-17 12:16:39', '1', '1114', '1114', '');
INSERT INTO `center_outputs_next_users` VALUES ('75', '57', '22', '2017-05-17 12:17:03', '1', '1114', '1114', '');
INSERT INTO `center_outputs_next_users` VALUES ('76', '59', '20', '2017-05-17 12:17:45', '1', '1114', '1114', '');
INSERT INTO `center_outputs_next_users` VALUES ('77', '60', '30', '2017-05-17 12:18:17', '1', '1114', '1114', '');
INSERT INTO `center_outputs_next_users` VALUES ('78', '61', '22', '2017-05-17 12:18:53', '1', '1114', '1114', '');
INSERT INTO `center_outputs_next_users` VALUES ('79', '61', '30', '2017-05-17 12:19:01', '1', '1114', '1114', '');
INSERT INTO `center_outputs_next_users` VALUES ('80', '62', '20', '2017-05-17 12:55:00', '1', '1114', '1114', '');
INSERT INTO `center_outputs_next_users` VALUES ('81', '63', '20', '2017-05-17 12:56:08', '1', '1114', '1114', '');
INSERT INTO `center_outputs_next_users` VALUES ('82', '63', '31', '2017-05-17 12:56:22', '1', '1114', '1114', '');
INSERT INTO `center_outputs_next_users` VALUES ('83', '64', '20', '2017-05-17 12:57:35', '1', '1114', '1114', '');
INSERT INTO `center_outputs_next_users` VALUES ('84', '64', '31', '2017-05-17 12:57:48', '1', '1114', '1114', '');
INSERT INTO `center_outputs_next_users` VALUES ('85', '65', '30', '2017-05-17 13:02:47', '1', '1114', '1114', '');
INSERT INTO `center_outputs_next_users` VALUES ('86', '67', '30', '2017-05-17 13:05:58', '1', '1114', '1114', '');
INSERT INTO `center_outputs_next_users` VALUES ('87', '67', '31', '2017-05-17 13:06:11', '1', '1114', '1114', '');
INSERT INTO `center_outputs_next_users` VALUES ('88', '68', '33', '2017-06-12 12:02:43', '1', '1114', '1114', '');
INSERT INTO `center_outputs_next_users` VALUES ('89', '69', '33', '2017-06-12 12:13:39', '1', '1114', '1114', '');
INSERT INTO `center_outputs_next_users` VALUES ('90', '71', '20', '2017-06-12 12:56:45', '1', '1114', '1114', '');
INSERT INTO `center_outputs_next_users` VALUES ('91', '72', '33', '2017-06-12 14:21:11', '1', '1114', '1114', '');
INSERT INTO `center_outputs_next_users` VALUES ('92', '73', '19', '2017-06-12 14:21:35', '1', '1114', '1114', '');
INSERT INTO `center_outputs_next_users` VALUES ('93', '74', '27', '2017-06-12 15:17:09', '1', '1114', '1114', '');
INSERT INTO `center_outputs_next_users` VALUES ('94', '75', '18', '2017-06-12 15:17:53', '1', '1114', '1114', '');
INSERT INTO `center_outputs_next_users` VALUES ('95', '76', '18', '2017-06-12 15:18:45', '1', '1114', '1114', '');
INSERT INTO `center_outputs_next_users` VALUES ('96', '77', '18', '2017-06-12 15:19:22', '1', '1114', '1114', '');
INSERT INTO `center_outputs_next_users` VALUES ('97', '78', '18', '2017-06-12 15:22:15', '1', '1114', '1114', '');
INSERT INTO `center_outputs_next_users` VALUES ('98', '79', '31', '2017-06-12 15:47:01', '1', '1114', '1114', '');
INSERT INTO `center_outputs_next_users` VALUES ('99', '80', '31', '2017-06-12 15:49:01', '1', '1114', '1114', '');
INSERT INTO `center_outputs_next_users` VALUES ('100', '81', '18', '2017-06-12 15:52:55', '1', '1114', '1114', '');
INSERT INTO `center_outputs_next_users` VALUES ('101', '82', '18', '2017-06-12 15:54:13', '1', '1114', '1114', '');
INSERT INTO `center_outputs_next_users` VALUES ('102', '83', '18', '2017-06-12 15:54:42', '1', '1114', '1114', '');
INSERT INTO `center_outputs_next_users` VALUES ('103', '84', '18', '2017-06-12 15:55:35', '1', '1114', '1114', '');
INSERT INTO `center_outputs_next_users` VALUES ('104', '85', '18', '2017-06-12 15:56:56', '1', '1114', '1114', '');
INSERT INTO `center_outputs_next_users` VALUES ('105', '86', '18', '2017-06-12 15:57:24', '1', '1114', '1114', '');
INSERT INTO `center_outputs_next_users` VALUES ('106', '87', '31', '2017-06-13 14:42:59', '1', '1114', '1114', '');
INSERT INTO `center_outputs_next_users` VALUES ('107', '88', '31', '2017-06-13 14:44:06', '1', '1114', '1114', '');
INSERT INTO `center_outputs_next_users` VALUES ('108', '89', '31', '2017-06-13 14:48:08', '1', '1114', '1114', '');
INSERT INTO `center_outputs_next_users` VALUES ('109', '90', '31', '2017-06-13 14:49:38', '1', '1114', '1114', '');
INSERT INTO `center_outputs_next_users` VALUES ('110', '92', '31', '2017-06-13 14:52:34', '1', '1114', '1114', '');
INSERT INTO `center_outputs_next_users` VALUES ('111', '93', '31', '2017-06-13 14:53:12', '1', '1114', '1114', '');

-- ----------------------------
-- Table structure for center_parameters
-- ----------------------------
DROP TABLE IF EXISTS `center_parameters`;
CREATE TABLE `center_parameters` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `key` varchar(500) DEFAULT NULL,
  `description` text,
  `format` int(11) DEFAULT NULL,
  `default_value` varchar(500) DEFAULT NULL,
  `category` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of center_parameters
-- ----------------------------
INSERT INTO `center_parameters` VALUES ('1', 'center_coord_role', 'Program Coordinator Role Id', '3', null, '1');

-- ----------------------------
-- Table structure for center_programs
-- ----------------------------
DROP TABLE IF EXISTS `center_programs`;
CREATE TABLE `center_programs` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` text NOT NULL,
  `acronym` varchar(50) NOT NULL,
  `program_type` int(11) NOT NULL,
  `color` varchar(8) DEFAULT NULL,
  `impact_color` varchar(8) DEFAULT NULL,
  `research_area_id` int(11) NOT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `active_since` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `fk_rprogram_rarea` (`research_area_id`) USING BTREE,
  KEY `fk_rprogram_type` (`program_type`) USING BTREE,
  KEY `fk_rprogram_created_by` (`created_by`) USING BTREE,
  KEY `fk_rprogram_modified_by` (`modified_by`) USING BTREE,
  CONSTRAINT `center_programs_ibfk_1` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `center_programs_ibfk_2` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`),
  CONSTRAINT `center_programs_ibfk_3` FOREIGN KEY (`research_area_id`) REFERENCES `center_areas` (`id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT `center_programs_ibfk_4` FOREIGN KEY (`program_type`) REFERENCES `center_all_types` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of center_programs
-- ----------------------------
INSERT INTO `center_programs` VALUES ('1', 'Linking Farmers to Markets', 'LFM', '1', '#D5D5D5', '#7D3C98', '1', '1', '2017-06-12 16:21:52', '1', '1057', '');
INSERT INTO `center_programs` VALUES ('2', 'Climate Change', 'CC', '1', '#D5D5D5', '#2980b9', '1', '1', '2017-05-11 08:35:28', '1', '1114', '');
INSERT INTO `center_programs` VALUES ('3', 'Ecosystem Services', 'ES', '1', '#D5D5D5', '#c0392b', '1', '1', '2017-04-24 08:34:51', '1', '1115', '');
INSERT INTO `center_programs` VALUES ('4', 'Cassava', 'C', '1', '#D5D5D5', '#F1C40F', '2', '1', '2017-06-15 13:09:07', '1', '1115', '');
INSERT INTO `center_programs` VALUES ('5', 'Bean', 'B', '1', '#D5D5D5', '#2980b9', '2', '1', '2017-05-16 09:40:47', '1', '1115', '');
INSERT INTO `center_programs` VALUES ('6', 'Rice', 'R', '1', '#D5D5D5', '#c0392b', '2', '1', '2017-05-30 10:54:50', '1', '1114', ' ');
INSERT INTO `center_programs` VALUES ('7', 'Genetic Resources', 'GR', '1', '#D5D5D5', '#1ABC9C', '2', '1', '2017-04-26 13:14:53', '1', '1057', ' ');
INSERT INTO `center_programs` VALUES ('8', 'Tropical Forages', 'TF', '1', '#D5D5D5', '#d35400', '2', '1', '2017-04-26 13:14:30', '1', '1057', ' ');
INSERT INTO `center_programs` VALUES ('9', 'Soils and Climate Change', 'SCC', '1', '#D5D5D5', '#67809F', '3', '1', '2017-05-19 16:17:15', '1', '1057', ' ');
INSERT INTO `center_programs` VALUES ('10', 'Restoring Degraded Land', 'RDL', '1', '#D5D5D5', '#F1C40F', '3', '1', '2016-12-14 11:12:34', '1', '3', ' ');
INSERT INTO `center_programs` VALUES ('11', 'Sustaining Soil Fertility and Health', 'SH', '1', '#D5D5D5', '#c0392b', '3', '1', '2016-12-14 11:12:36', '1', '3', ' ');

-- ----------------------------
-- Table structure for center_projects
-- ----------------------------
DROP TABLE IF EXISTS `center_projects`;
CREATE TABLE `center_projects` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ocs_code` text,
  `program_id` int(11) NOT NULL,
  `name` text,
  `suggested_name` text,
  `description` text,
  `project_type_id` bigint(20) DEFAULT NULL,
  `status_id` int(11) DEFAULT NULL,
  `date_created` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `start_date` timestamp NULL DEFAULT NULL,
  `end_date` timestamp NULL DEFAULT NULL,
  `extension_date` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `project_leader_id` bigint(20) DEFAULT NULL,
  `contact_person_id` bigint(20) DEFAULT NULL,
  `original_donor` text,
  `direct_donor` text,
  `total_amount` double(30,2) DEFAULT NULL,
  `is_global` tinyint(1) DEFAULT NULL,
  `is_region` tinyint(1) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL,
  `active_since` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `project_status_fk` (`status_id`),
  KEY `project_leader_fk` (`project_leader_id`),
  KEY `project_contact_fk` (`contact_person_id`),
  KEY `project_created_fk` (`created_by`),
  KEY `project_modified_fk` (`modified_by`),
  KEY `project_program_fk` (`program_id`),
  KEY `project_type_fk` (`project_type_id`),
  CONSTRAINT `project_contact_fk` FOREIGN KEY (`contact_person_id`) REFERENCES `users` (`id`),
  CONSTRAINT `project_created_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `project_leader_fk` FOREIGN KEY (`project_leader_id`) REFERENCES `users` (`id`),
  CONSTRAINT `project_modified_fk` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`),
  CONSTRAINT `project_program_fk` FOREIGN KEY (`program_id`) REFERENCES `center_programs` (`id`),
  CONSTRAINT `project_status_fk` FOREIGN KEY (`status_id`) REFERENCES `center_project_status` (`id`),
  CONSTRAINT `project_type_fk` FOREIGN KEY (`project_type_id`) REFERENCES `center_project_types` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of center_projects
-- ----------------------------
INSERT INTO `center_projects` VALUES ('1', null, '7', 'Test Project ', null, null, null, '2', '2017-05-24 11:54:07', '2017-04-03 00:00:00', '2019-12-31 00:00:00', '2017-05-24 11:54:07', '1', null, null, null, null, '0', '0', '1', '2017-05-24 11:54:07', '1057', '1', null);
INSERT INTO `center_projects` VALUES ('2', null, '1', 'Test', null, null, null, '2', '2017-05-24 11:54:07', '2017-04-27 00:00:00', null, '2017-05-24 11:54:07', null, null, null, null, null, '0', '0', '1', '2017-05-24 11:54:07', '1115', '1057', null);
INSERT INTO `center_projects` VALUES ('3', '', '1', 'Creating a learning community for public-private climate smart value chains and landscapes benefiting smallholders', '', '', '2', '2', '2017-05-24 11:54:07', '2011-05-04 00:00:00', '2017-06-06 00:00:00', '2017-05-24 00:00:00', '55', null, '', '', '0.00', '1', null, '1', '2017-06-15 08:03:40', '1115', '1115', null);
INSERT INTO `center_projects` VALUES ('4', null, '3', 'example', null, null, null, '2', '2017-05-24 11:54:07', '2017-01-01 00:00:00', '2022-12-31 00:00:00', '2017-05-24 11:54:07', '55', null, null, null, null, '0', '0', '1', '2017-05-24 11:54:07', '1115', '1115', null);
INSERT INTO `center_projects` VALUES ('5', null, '8', null, null, null, null, '2', '2017-05-24 11:54:07', '2017-05-02 15:19:57', null, '2017-05-24 11:54:07', null, null, null, null, null, '0', '0', '1', '2017-05-24 11:54:07', '1115', '1115', null);
INSERT INTO `center_projects` VALUES ('6', null, '1', null, null, null, null, '2', '2017-05-24 11:54:07', '2017-05-03 13:25:06', null, '2017-05-24 11:54:07', null, null, null, null, null, '0', '0', '1', '2017-05-24 11:54:07', '1115', '1115', null);
INSERT INTO `center_projects` VALUES ('7', null, '4', 'Cassava Varietal Development Agreement\r\n', null, null, null, '2', '2017-05-24 11:54:07', '2012-05-10 00:00:00', '2017-05-31 00:00:00', '2017-05-24 11:54:07', '1128', null, null, null, null, '0', '0', '1', '2017-05-24 11:54:07', '1114', '1114', null);
INSERT INTO `center_projects` VALUES ('8', null, '2', null, null, null, null, '2', '2017-05-24 11:54:07', '2017-05-09 10:07:35', null, '2017-05-24 11:54:07', null, null, null, null, null, '0', '0', '1', '2017-05-24 11:54:07', '1', '1', null);
INSERT INTO `center_projects` VALUES ('9', null, '2', null, null, null, null, '2', '2017-05-24 11:54:07', '2017-05-10 09:21:49', null, '2017-05-24 11:54:07', null, null, null, null, null, '0', '0', '1', '2017-05-24 11:54:07', '1114', '1114', null);
INSERT INTO `center_projects` VALUES ('10', null, '1', 'TESTT', null, null, null, '2', '2017-05-24 11:54:07', '2017-05-17 00:00:00', '2017-05-11 00:00:00', '2017-05-24 11:54:07', '1114', null, null, null, null, '0', '0', '0', '2017-05-24 11:54:07', '1115', '1115', 'tester');
INSERT INTO `center_projects` VALUES ('11', 'A210', '6', 'Hybrid Technologies for Heterosis in Rice and Related Cereals (Funds National Science Foundation)\r\n', '', 'NATIONAL SCIENCE FOUNDATION', '2', '2', '2017-05-24 11:54:07', '2010-04-10 00:00:00', '2017-03-31 00:00:00', '2017-03-31 00:00:00', '1110', null, 'Yale University', '', '0.00', '0', '0', '1', '2017-06-08 11:03:16', '1114', '1115', null);
INSERT INTO `center_projects` VALUES ('12', null, '1', null, null, null, null, '2', '2017-06-02 11:04:25', '2017-06-02 11:04:25', null, null, null, null, null, null, null, '0', '0', '1', '2017-06-02 11:04:25', '1115', '1115', null);
INSERT INTO `center_projects` VALUES ('13', 'A108', '6', 'Global Rice Science Partnership', null, 'Global Rice Science Partnership', '1', '2', '2017-06-07 11:41:16', '2011-01-01 00:00:00', '2012-12-31 00:00:00', '2016-12-31 00:00:00', '1112', null, 'IRRI-International Rice Research Institute', '', '17121444.00', '1', '0', '1', '2017-06-07 13:51:15', '1115', '1115', null);
INSERT INTO `center_projects` VALUES ('14', 'A284', '6', 'Delivering of context-specific knowledge databases that allow for regional calibrations of the WARM, WOFOST and/or CROPSYST models for rice and bean crops in Latin America', null, 'Knowledge delivered for regional calibrations of xxxxx', '2', '2', '2017-06-07 12:16:23', '2016-08-01 00:00:00', null, null, '902', null, 'EC-European Commission', '', '45204.00', '1', '0', '0', '2017-06-07 12:21:47', '1114', '1115', 'project ended');
INSERT INTO `center_projects` VALUES ('15', null, '6', null, null, null, null, '2', '2017-06-08 08:38:11', '2017-06-08 08:38:11', null, null, null, null, null, null, null, null, null, '0', '2017-06-08 08:38:11', '1115', '1115', 'tester');
INSERT INTO `center_projects` VALUES ('16', 'A175', '6', 'Desarrollo e implementación de nuevas tecnologías que ayuden a aumentar los rendimientos del cultivo del arroz en colombia, con el fin de mejorar la competitividad del cultivo a nivel regional', '', 'Desarrollo e implementación de nuevas tecnologías que ayuden a aumentar los rendimientos del cultivo del arroz en colombia, con el fin de mejorar la competitividad del cultivo a nivel regional', '2', '2', '2017-06-08 10:10:58', '2014-02-07 00:00:00', '2018-02-07 00:00:00', null, '1130', null, 'COLCIENCIAS-Instituto Colombiano para el Desarrollo de la Ciencia y la Tecnologia', '', '560098.72', '0', '0', '1', '2017-06-08 10:35:01', '1115', '1115', null);
INSERT INTO `center_projects` VALUES ('17', 'A296', '6', 'Iron-rice', '', 'Iron-rice', '3', '2', '2017-06-08 10:47:00', '2016-10-01 00:00:00', '2017-09-30 00:00:00', null, '1110', null, 'USAID-United States Agency for International Development', '', '245000.00', '0', '0', '1', '2017-06-08 10:50:25', '1115', '1115', null);
INSERT INTO `center_projects` VALUES ('18', 'A297', '6', 'CGIAR Research Program: Rice', '', 'CGIAR Research Program: Rice', '1', '2', '2017-06-08 10:51:24', '2017-01-01 00:00:00', '2017-12-31 00:00:00', null, '1112', null, 'IRRI-International Rice Research Institute', '', '1753000.00', '1', '0', '1', '2017-06-08 10:54:16', '1115', '1115', null);
INSERT INTO `center_projects` VALUES ('19', 'A310', '6', 'NEWEST Rice', '', 'NEWEST Rice', '2', '2', '2017-06-08 10:54:54', '2016-04-01 00:00:00', '2019-03-31 00:00:00', null, '1131', null, 'USAID-United States Agency for International Development', 'AATF-African Agricultural Technology Foundation', '362724.00', '0', '0', '1', '2017-06-08 10:59:47', '1115', '1115', null);
INSERT INTO `center_projects` VALUES ('20', 'D145', '3', 'Sustainable development options and land-use based alternatives to: enhance climate change mitigation and adaptation capacities in the Colombian and Peruvian Amazon, while enhancing ecosystem services and local livelihoods', '', 'Project: 14_III_057_A_Lateinam ', '2', '2', '2017-06-16 15:24:26', '2014-09-01 00:00:00', '2018-06-30 00:00:00', '2018-06-30 00:00:00', '1109', null, 'Germany-BMU-Bundesministerium für Umwelt, Naturschutz, Bau und Reaktorsicherheit', '', '2882680.00', '0', '0', '1', '2017-06-16 15:59:55', '1115', '1115', null);
INSERT INTO `center_projects` VALUES ('21', 'D189', '3', 'Transformando Evidencia en Cambio: Un Enfoque Holístico para la Gobernanza y la Adaptación basada en Ecosistemas', 'Proyecto AVE - Adaptacion', 'Ref. P01457', '2', '2', '2017-06-16 15:37:37', '2016-05-12 00:00:00', '2017-06-15 00:00:00', null, '1109', null, 'IUCN-International Union for Conservation of Nature and Natural Resources', '', '104326.00', '0', '0', '1', '2017-06-16 15:59:25', '1115', '1115', null);
INSERT INTO `center_projects` VALUES ('22', 'D196', '3', 'Filling Gaps in NPGS GRIN-GLOBAL Database:  Geo-reference Validation and U.S. Crop Wild Relative Gap Analysis', '', 'Agreement # 59-3012-6-001F', '2', '2', '2017-06-16 16:00:06', '2016-07-01 00:00:00', '2017-12-31 00:00:00', '2017-06-30 00:00:00', '28', null, 'USDA-United States Department of Agriculture', '', '25000.00', '0', '0', '1', '2017-06-16 16:20:53', '1115', '1115', null);
INSERT INTO `center_projects` VALUES ('23', 'D204', '3', 'Elucidating pathways from agrobiodiversity to dietary diversity in Perú and Vietnam', '', 'Elucidating pathways from agrobiodiversity to dietary diversity in Perú and Vietnam', '2', '2', '2017-06-16 16:21:18', '2016-02-01 00:00:00', '2017-12-31 00:00:00', null, null, null, 'IIN-Instituto de Investigación Nutricional', '', '67754.00', '0', '0', '1', '2017-06-16 16:23:41', '1115', '1115', null);
INSERT INTO `center_projects` VALUES ('24', 'D211', '3', 'CGIAR Research Program 6: Forests, Trees & Agroforestry', 'CGIAR CRP 6', 'CGIAR Research Program 6: Forests, Trees & Agroforestry', '1', '2', '2017-06-16 16:23:55', '2017-01-01 00:00:00', '2017-12-01 00:00:00', null, '127', null, 'CIFOR-Center for International Forestry Research', '', '170585.00', '0', '0', '1', '2017-06-16 16:26:21', '1115', '1115', null);
INSERT INTO `center_projects` VALUES ('25', 'D222', '3', 'SNAPP para la Orinoquia de Colombia', 'SNAPP', 'SNAPP para la Orinoquia de Colombia\r\nOCS-CIAT-170220', '2', '2', '2017-06-16 16:26:28', '2017-05-01 00:00:00', '2017-12-31 00:00:00', null, '127', null, 'TNC-The Nature Conservancy', '', '31000.00', '0', '0', '1', '2017-06-16 16:29:24', '1115', '1115', null);
INSERT INTO `center_projects` VALUES ('26', null, '6', null, null, null, null, '2', '2017-06-20 07:45:24', '2017-06-20 07:45:24', null, null, null, null, null, null, null, null, null, '1', '2017-06-20 07:45:24', '1115', '1115', null);
INSERT INTO `center_projects` VALUES ('27', 'D108', '3', 'CGIAR Research Program 6: Forests, Trees & Agroforestry', '', 'CGIAR System Organization', '1', '2', '2017-06-20 07:45:40', '2011-07-01 00:00:00', '2014-06-30 00:00:00', '2016-12-31 00:00:00', '127', null, 'CIFOR-Center for International Forestry Research', '', '1966075.00', '0', '0', '1', '2017-06-20 07:51:27', '1115', '1115', null);
INSERT INTO `center_projects` VALUES ('28', 'D112', '3', 'ASSETS: Managing ecosystem services for food security and the nutritional health of the rural poor at the forest-agricultural interface', '', 'NE/J001058/1', '2', '2', '2017-06-20 07:52:00', '2012-03-01 00:00:00', '2016-02-29 00:00:00', '2016-08-30 00:00:00', '28', null, 'NERC-Natural Environment Research Council', '', '1216055.00', '0', '0', '1', '2017-06-20 07:56:46', '1115', '1115', null);
INSERT INTO `center_projects` VALUES ('29', 'D191', '3', 'Integrate Terra-I data into the GFW platform', '', '', '2', '2', '2017-06-20 07:57:04', '2016-03-20 00:00:00', '2016-12-31 00:00:00', null, '1133', null, 'WRI-World Resources Institute', '', '151698.00', '0', '0', '1', '2017-06-20 08:00:28', '1115', '1115', null);
INSERT INTO `center_projects` VALUES ('30', 'A223', '4', 'Innovative crop and soil-based technologies in Haiti', 'Innovative Crop', 'Innovative crop and soil-based technologies in Haiti', '3', '2', '2017-06-27 08:08:09', '2015-02-16 00:00:00', '2017-02-15 00:00:00', '2017-09-30 00:00:00', '904', null, 'IFAD-International Fund for Agricultural Development', '', '500000.00', '0', '0', '1', '2017-06-27 08:39:14', '1115', '1115', null);
INSERT INTO `center_projects` VALUES ('31', 'A130', '4', 'Cassava Varietal Development Agreement', 'Cassava Varieties', 'Cassava Varietal Development Agreement', '2', '2', '2017-06-27 08:46:36', '2012-05-10 00:00:00', '2017-05-31 00:00:00', '2017-05-31 00:00:00', '1128', null, 'Ingredion Colombia S.A.Ingredion Colombia S.A.', '', '750000.00', '0', '0', '1', '2017-06-27 08:57:05', '1115', '1115', null);
INSERT INTO `center_projects` VALUES ('32', 'A124', '4', 'Complementary Funding for Cross-Cutting Projects', 'Complementary Funding for Cross-Cutting Projects', 'Complementary Funding for Cross-Cutting Projects', '1', '2', '2017-06-27 09:12:21', '2012-01-01 00:00:00', '2014-12-31 00:00:00', '2016-12-31 00:00:00', '1111', null, 'CGIAR System Organization', 'CIP-International Potato Center', '1970659.00', '0', '0', '1', '2017-06-27 09:44:30', '1115', '1115', null);

-- ----------------------------
-- Table structure for center_project_crosscuting_themes
-- ----------------------------
DROP TABLE IF EXISTS `center_project_crosscuting_themes`;
CREATE TABLE `center_project_crosscuting_themes` (
  `id` bigint(20) NOT NULL,
  `climate_change` tinyint(1) DEFAULT NULL,
  `gender` tinyint(1) DEFAULT NULL,
  `youth` tinyint(1) DEFAULT NULL,
  `policies_institutions` tinyint(1) DEFAULT NULL,
  `capacity_development` tinyint(1) DEFAULT NULL,
  `big_data` tinyint(1) DEFAULT NULL,
  `impact_assessment` tinyint(1) DEFAULT NULL,
  `n_a` tinyint(1) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL,
  `active_since` timestamp NULL DEFAULT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `project_cc_theme_created_fk` (`created_by`),
  KEY `project_cc_theme_modified_fk` (`modified_by`),
  CONSTRAINT `project_cc_theme_created_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `project_cc_theme_modified_fk` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`),
  CONSTRAINT `project_cc_theme_project_id_fk` FOREIGN KEY (`id`) REFERENCES `center_projects` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of center_project_crosscuting_themes
-- ----------------------------
INSERT INTO `center_project_crosscuting_themes` VALUES ('1', '0', '1', null, '0', '0', '0', '0', '0', '1', '2017-04-03 11:26:27', '1057', '1057', '');
INSERT INTO `center_project_crosscuting_themes` VALUES ('2', '0', '1', '1', '0', '0', '0', '0', '0', '1', '2017-04-27 13:57:47', '1115', '1115', '');
INSERT INTO `center_project_crosscuting_themes` VALUES ('3', '1', '0', '0', '0', '0', '0', '0', '0', '1', '2017-05-02 09:46:04', '1115', '1115', '');
INSERT INTO `center_project_crosscuting_themes` VALUES ('4', '1', '0', '0', '0', '0', '0', '0', '0', '1', '2017-05-02 09:47:14', '1115', '1115', '');
INSERT INTO `center_project_crosscuting_themes` VALUES ('5', '0', '0', '0', '0', '0', '0', '0', '0', '1', '2017-05-02 15:19:57', '1115', '1115', '');
INSERT INTO `center_project_crosscuting_themes` VALUES ('6', '0', '0', '0', '0', '0', '0', '0', '0', '1', '2017-05-03 13:25:06', '1115', '1115', '');
INSERT INTO `center_project_crosscuting_themes` VALUES ('7', '0', '0', '0', '0', '0', '0', '0', '1', '1', '2017-05-08 15:26:41', '1114', '1114', '');
INSERT INTO `center_project_crosscuting_themes` VALUES ('8', '0', '0', '0', '0', '0', '0', '0', '0', '1', '2017-05-09 10:07:35', '1', '1', '');
INSERT INTO `center_project_crosscuting_themes` VALUES ('9', '0', '0', '0', '0', '0', '0', '0', '0', '1', '2017-05-10 09:21:49', '1114', '1114', '');
INSERT INTO `center_project_crosscuting_themes` VALUES ('10', '0', '1', '0', '0', '0', '0', '0', '0', '1', '2017-05-17 08:42:49', '1115', '1115', '');
INSERT INTO `center_project_crosscuting_themes` VALUES ('11', '0', '0', '0', '0', '0', '0', '0', '1', '1', '2017-05-17 13:20:29', '1114', '1114', '');
INSERT INTO `center_project_crosscuting_themes` VALUES ('12', '0', '0', '0', '0', '0', '0', '0', '0', '1', '2017-06-02 11:04:25', '1115', '1115', '');
INSERT INTO `center_project_crosscuting_themes` VALUES ('13', '0', '0', '0', '1', '0', '0', '0', '0', '1', '2017-06-07 11:41:16', '1115', '1115', '');
INSERT INTO `center_project_crosscuting_themes` VALUES ('14', '0', '0', '0', '0', '1', '0', '0', '0', '1', '2017-06-07 12:16:23', '1114', '1114', '');
INSERT INTO `center_project_crosscuting_themes` VALUES ('15', '0', '0', '0', '0', '0', '0', '0', '0', '1', '2017-06-08 08:38:11', '1115', '1115', '');
INSERT INTO `center_project_crosscuting_themes` VALUES ('16', '0', '0', '0', '0', '0', '0', '0', '0', '1', '2017-06-08 10:10:58', '1115', '1115', '');
INSERT INTO `center_project_crosscuting_themes` VALUES ('17', '0', '0', '0', '0', '0', '0', '0', '0', '1', '2017-06-08 10:47:00', '1115', '1115', '');
INSERT INTO `center_project_crosscuting_themes` VALUES ('18', '0', '0', '0', '0', '0', '0', '0', '0', '1', '2017-06-08 10:51:24', '1115', '1115', '');
INSERT INTO `center_project_crosscuting_themes` VALUES ('19', '0', '0', '0', '0', '0', '0', '0', '0', '1', '2017-06-08 10:54:54', '1115', '1115', '');
INSERT INTO `center_project_crosscuting_themes` VALUES ('20', '1', '0', '0', '1', '0', '0', '0', '0', '1', '2017-06-16 15:24:26', '1115', '1115', '');
INSERT INTO `center_project_crosscuting_themes` VALUES ('21', '1', '0', '0', '1', '0', '0', '0', '0', '1', '2017-06-16 15:37:37', '1115', '1115', '');
INSERT INTO `center_project_crosscuting_themes` VALUES ('22', '0', '0', '0', '0', '0', '1', '0', '0', '1', '2017-06-16 16:00:06', '1115', '1115', '');
INSERT INTO `center_project_crosscuting_themes` VALUES ('23', '0', '0', '0', '1', '0', '0', '0', '0', '1', '2017-06-16 16:21:18', '1115', '1115', '');
INSERT INTO `center_project_crosscuting_themes` VALUES ('24', '1', '0', '0', '1', '0', '0', '0', '0', '1', '2017-06-16 16:23:55', '1115', '1115', '');
INSERT INTO `center_project_crosscuting_themes` VALUES ('25', '0', '0', '0', '0', '0', '0', '0', '0', '1', '2017-06-16 16:26:28', '1115', '1115', '');
INSERT INTO `center_project_crosscuting_themes` VALUES ('26', '0', '0', '0', '0', '0', '0', '0', '0', '1', '2017-06-20 07:45:24', '1115', '1115', '');
INSERT INTO `center_project_crosscuting_themes` VALUES ('27', '0', '0', '0', '0', '0', '0', '0', '1', '1', '2017-06-20 07:45:40', '1115', '1115', '');
INSERT INTO `center_project_crosscuting_themes` VALUES ('28', '0', '0', '0', '0', '0', '0', '0', '1', '1', '2017-06-20 07:52:00', '1115', '1115', '');
INSERT INTO `center_project_crosscuting_themes` VALUES ('29', '0', '0', '0', '0', '0', '0', '0', '0', '1', '2017-06-20 07:57:04', '1115', '1115', '');
INSERT INTO `center_project_crosscuting_themes` VALUES ('30', '0', '0', '0', '0', '0', '0', '0', '0', '1', '2017-06-27 08:08:09', '1115', '1115', '');
INSERT INTO `center_project_crosscuting_themes` VALUES ('31', '0', '0', '0', '0', '0', '0', '0', '0', '1', '2017-06-27 08:46:36', '1115', '1115', '');
INSERT INTO `center_project_crosscuting_themes` VALUES ('32', '0', '0', '0', '0', '0', '0', '0', '0', '1', '2017-06-27 09:12:21', '1115', '1115', '');

-- ----------------------------
-- Table structure for center_project_funding_sources
-- ----------------------------
DROP TABLE IF EXISTS `center_project_funding_sources`;
CREATE TABLE `center_project_funding_sources` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `project_id` bigint(20) NOT NULL,
  `crp_id` bigint(20) DEFAULT NULL,
  `title` text,
  `funding_source_type_id` int(11) NOT NULL,
  `is_active` tinyint(1) NOT NULL,
  `active_since` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `project_funding_project_id_fk` (`project_id`),
  KEY `project_funding_funding_type_id_fk` (`funding_source_type_id`),
  KEY `project_funding_created_id_fk` (`created_by`),
  KEY `project_funding_modified_id_fk` (`modified_by`),
  KEY `project_funding_crp_fk` (`crp_id`),
  CONSTRAINT `project_funding_created_id_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `project_funding_crp_fk` FOREIGN KEY (`crp_id`) REFERENCES `crps` (`id`),
  CONSTRAINT `project_funding_funding_type_id_fk` FOREIGN KEY (`funding_source_type_id`) REFERENCES `center_funding_source_types` (`id`),
  CONSTRAINT `project_funding_modified_id_fk` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`),
  CONSTRAINT `project_funding_project_id_fk` FOREIGN KEY (`project_id`) REFERENCES `center_projects` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of center_project_funding_sources
-- ----------------------------
INSERT INTO `center_project_funding_sources` VALUES ('1', '1', null, 'Test Funding Source', '1', '1', '2017-04-26 11:19:16', '1057', '1', '');
INSERT INTO `center_project_funding_sources` VALUES ('2', '4', null, '', '1', '1', '2017-05-24 08:47:52', '1115', '1115', '');
INSERT INTO `center_project_funding_sources` VALUES ('3', '3', null, '', '2', '0', '2017-05-24 11:58:15', '1114', '1057', '');
INSERT INTO `center_project_funding_sources` VALUES ('4', '7', null, 'Cassava Varietal Development Agreement', '2', '1', '2017-05-10 08:35:30', '1114', '1114', '');
INSERT INTO `center_project_funding_sources` VALUES ('5', '10', null, '', '1', '1', '2017-05-17 08:45:58', '1115', '1115', '');
INSERT INTO `center_project_funding_sources` VALUES ('6', '11', null, '', '2', '0', '2017-06-08 11:02:09', '1114', '1115', '');
INSERT INTO `center_project_funding_sources` VALUES ('7', '13', '16', '', '3', '1', '2017-06-07 13:51:15', '1115', '1115', '');
INSERT INTO `center_project_funding_sources` VALUES ('8', '24', '11', 'CGIAR Research Program 6: Forests, Trees & Agroforestry', '1', '1', '2017-06-16 16:26:21', '1115', '1115', '');
INSERT INTO `center_project_funding_sources` VALUES ('9', '27', '11', '', '1', '1', '2017-06-20 07:51:27', '1115', '1115', '');

-- ----------------------------
-- Table structure for center_project_locations
-- ----------------------------
DROP TABLE IF EXISTS `center_project_locations`;
CREATE TABLE `center_project_locations` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_id` bigint(20) DEFAULT NULL,
  `loc_element_id` bigint(20) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL,
  `active_since` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `project_location_id_fk` (`project_id`),
  KEY `project_location_element_fk` (`loc_element_id`),
  KEY `project_location_created_fk` (`created_by`),
  KEY `project_location_modified_fk` (`modified_by`),
  CONSTRAINT `project_location_created_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `project_location_element_fk` FOREIGN KEY (`loc_element_id`) REFERENCES `loc_elements` (`id`),
  CONSTRAINT `project_location_id_fk` FOREIGN KEY (`project_id`) REFERENCES `center_projects` (`id`),
  CONSTRAINT `project_location_modified_fk` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of center_project_locations
-- ----------------------------
INSERT INTO `center_project_locations` VALUES ('1', '11', '4', '1', '2017-05-17 13:27:45', '1114', '1114', '');
INSERT INTO `center_project_locations` VALUES ('2', '11', '53', '1', '2017-05-17 13:27:45', '1114', '1114', '');
INSERT INTO `center_project_locations` VALUES ('3', '16', '53', '1', '2017-06-08 10:35:01', '1115', '1115', '');
INSERT INTO `center_project_locations` VALUES ('4', '17', '53', '1', '2017-06-08 10:50:25', '1115', '1115', '');
INSERT INTO `center_project_locations` VALUES ('5', '19', '159', '1', '2017-06-08 10:59:47', '1115', '1115', '');
INSERT INTO `center_project_locations` VALUES ('6', '19', '221', '1', '2017-06-08 10:59:47', '1115', '1115', '');
INSERT INTO `center_project_locations` VALUES ('7', '19', '222', '1', '2017-06-08 10:59:47', '1115', '1115', '');
INSERT INTO `center_project_locations` VALUES ('8', '19', '83', '1', '2017-06-08 10:59:47', '1115', '1115', '');
INSERT INTO `center_project_locations` VALUES ('9', '20', '53', '1', '2017-06-16 15:37:06', '1115', '1115', '');
INSERT INTO `center_project_locations` VALUES ('10', '20', '60', '1', '2017-06-16 15:37:06', '1115', '1115', '');
INSERT INTO `center_project_locations` VALUES ('11', '20', '169', '1', '2017-06-16 15:37:06', '1115', '1115', '');
INSERT INTO `center_project_locations` VALUES ('12', '21', '54', '1', '2017-06-16 15:59:25', '1115', '1115', '');
INSERT INTO `center_project_locations` VALUES ('13', '21', '95', '1', '2017-06-16 15:59:25', '1115', '1115', '');
INSERT INTO `center_project_locations` VALUES ('14', '21', '153', '1', '2017-06-16 15:59:25', '1115', '1115', '');
INSERT INTO `center_project_locations` VALUES ('15', '21', '168', '1', '2017-06-16 15:59:25', '1115', '1115', '');
INSERT INTO `center_project_locations` VALUES ('16', '21', '202', '1', '2017-06-16 15:59:25', '1115', '1115', '');
INSERT INTO `center_project_locations` VALUES ('17', '22', '53', '1', '2017-06-16 16:20:34', '1115', '1115', '');
INSERT INTO `center_project_locations` VALUES ('18', '23', '169', '1', '2017-06-16 16:23:19', '1115', '1115', '');
INSERT INTO `center_project_locations` VALUES ('19', '23', '230', '1', '2017-06-16 16:23:41', '1115', '1115', '');
INSERT INTO `center_project_locations` VALUES ('20', '24', '53', '1', '2017-06-16 16:26:02', '1115', '1115', '');
INSERT INTO `center_project_locations` VALUES ('21', '24', '168', '1', '2017-06-16 16:26:02', '1115', '1115', '');
INSERT INTO `center_project_locations` VALUES ('22', '24', '169', '1', '2017-06-16 16:26:02', '1115', '1115', '');
INSERT INTO `center_project_locations` VALUES ('23', '25', '53', '1', '2017-06-16 16:28:38', '1115', '1115', '');
INSERT INTO `center_project_locations` VALUES ('24', '27', '53', '1', '2017-06-20 07:51:05', '1115', '1115', '');
INSERT INTO `center_project_locations` VALUES ('25', '27', '168', '1', '2017-06-20 07:51:05', '1115', '1115', '');
INSERT INTO `center_project_locations` VALUES ('26', '27', '169', '1', '2017-06-20 07:51:05', '1115', '1115', '');
INSERT INTO `center_project_locations` VALUES ('27', '28', '53', '1', '2017-06-20 07:56:46', '1115', '1115', '');
INSERT INTO `center_project_locations` VALUES ('28', '28', '169', '1', '2017-06-20 07:56:46', '1115', '1115', '');
INSERT INTO `center_project_locations` VALUES ('29', '29', '47', '1', '2017-06-20 08:00:28', '1115', '1115', '');
INSERT INTO `center_project_locations` VALUES ('30', '29', '53', '1', '2017-06-20 08:00:28', '1115', '1115', '');
INSERT INTO `center_project_locations` VALUES ('31', '30', '97', '1', '2017-06-27 08:39:14', '1115', '1115', '');
INSERT INTO `center_project_locations` VALUES ('32', '31', '53', '1', '2017-06-27 08:57:05', '1115', '1115', '');
INSERT INTO `center_project_locations` VALUES ('33', '32', '53', '1', '2017-06-27 09:44:30', '1115', '1115', '');

-- ----------------------------
-- Table structure for center_project_outputs
-- ----------------------------
DROP TABLE IF EXISTS `center_project_outputs`;
CREATE TABLE `center_project_outputs` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `project_id` bigint(20) DEFAULT NULL,
  `output_id` int(11) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL,
  `modification_justification` text,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `active_since` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `po_project_fk` (`project_id`),
  KEY `po_output_fk` (`output_id`),
  KEY `po_created_fk` (`created_by`),
  KEY `po_modified_fk` (`modified_by`),
  CONSTRAINT `po_created_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `po_modified_fk` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`),
  CONSTRAINT `po_output_fk` FOREIGN KEY (`output_id`) REFERENCES `center_outputs` (`id`),
  CONSTRAINT `po_project_fk` FOREIGN KEY (`project_id`) REFERENCES `center_projects` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of center_project_outputs
-- ----------------------------
INSERT INTO `center_project_outputs` VALUES ('1', '1', '1', '0', '', '1057', '1057', '2017-06-27 10:28:52');
INSERT INTO `center_project_outputs` VALUES ('2', '1', '2', '0', '', '1057', '1057', '2017-06-27 10:28:52');
INSERT INTO `center_project_outputs` VALUES ('3', '4', '11', '0', '', '1115', '1115', '2017-05-02 11:01:51');
INSERT INTO `center_project_outputs` VALUES ('4', '4', '11', '1', '', '1114', '1114', '2017-05-03 10:45:15');
INSERT INTO `center_project_outputs` VALUES ('5', '3', '14', '0', '', '1115', '1115', '2017-05-03 11:10:33');
INSERT INTO `center_project_outputs` VALUES ('6', '7', '33', '1', '', '1114', '1114', '2017-05-08 15:34:29');
INSERT INTO `center_project_outputs` VALUES ('7', '7', '34', '1', '', '1114', '1114', '2017-05-10 08:35:30');
INSERT INTO `center_project_outputs` VALUES ('8', '10', '14', '1', '', '1115', '1115', '2017-05-17 08:45:39');
INSERT INTO `center_project_outputs` VALUES ('9', '11', '60', '0', '', '1114', '1114', '2017-06-27 10:28:52');
INSERT INTO `center_project_outputs` VALUES ('10', '11', '57', '0', '', '1114', '1114', '2017-06-27 10:28:52');
INSERT INTO `center_project_outputs` VALUES ('11', '4', '12', '0', '', '1115', '1115', '2017-05-24 08:47:35');
INSERT INTO `center_project_outputs` VALUES ('12', '3', '14', '1', '', '1107', '1107', '2017-06-07 10:46:25');
INSERT INTO `center_project_outputs` VALUES ('13', '13', '57', '0', '', '1115', '1115', '2017-06-27 10:28:52');
INSERT INTO `center_project_outputs` VALUES ('14', '13', '58', '0', '', '1115', '1115', '2017-06-27 10:28:52');
INSERT INTO `center_project_outputs` VALUES ('15', '17', '67', '0', '', '1115', '1115', '2017-06-27 10:28:52');
INSERT INTO `center_project_outputs` VALUES ('16', '18', '62', '0', '', '1115', '1115', '2017-06-27 10:28:52');
INSERT INTO `center_project_outputs` VALUES ('17', '19', '59', '0', '', '1115', '1115', '2017-06-27 10:28:52');
INSERT INTO `center_project_outputs` VALUES ('18', '11', '67', '0', '', '1115', '1115', '2017-06-27 10:28:52');
INSERT INTO `center_project_outputs` VALUES ('19', '20', '12', '1', '', '1115', '1115', '2017-06-16 15:37:06');
INSERT INTO `center_project_outputs` VALUES ('20', '21', '11', '1', '', '1115', '1115', '2017-06-16 15:59:25');
INSERT INTO `center_project_outputs` VALUES ('21', '22', '11', '1', '', '1115', '1115', '2017-06-16 16:20:34');
INSERT INTO `center_project_outputs` VALUES ('22', '23', '11', '1', '', '1115', '1115', '2017-06-16 16:23:19');
INSERT INTO `center_project_outputs` VALUES ('23', '24', '12', '1', '', '1115', '1115', '2017-06-16 16:26:02');
INSERT INTO `center_project_outputs` VALUES ('24', '25', '11', '1', '', '1115', '1115', '2017-06-16 16:28:38');
INSERT INTO `center_project_outputs` VALUES ('25', '27', '12', '1', '', '1115', '1115', '2017-06-20 07:51:05');
INSERT INTO `center_project_outputs` VALUES ('26', '29', '12', '1', '', '1115', '1115', '2017-06-20 08:00:28');
INSERT INTO `center_project_outputs` VALUES ('27', '30', '41', '1', '', '1115', '1115', '2017-06-27 08:39:14');
INSERT INTO `center_project_outputs` VALUES ('28', '31', '29', '1', '', '1115', '1115', '2017-06-27 08:57:05');
INSERT INTO `center_project_outputs` VALUES ('29', '32', '41', '1', '', '1115', '1115', '2017-06-27 09:44:30');

-- ----------------------------
-- Table structure for center_project_partners
-- ----------------------------
DROP TABLE IF EXISTS `center_project_partners`;
CREATE TABLE `center_project_partners` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_id` bigint(20) NOT NULL,
  `institution_id` bigint(20) NOT NULL,
  `is_internal` tinyint(1) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL,
  `active_since` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `project_partners_project_id_fk` (`project_id`),
  KEY `project_partners_institution_id_fk` (`institution_id`),
  KEY `project_partners_created_fk` (`created_by`),
  KEY `project_partners_modified_fk` (`modified_by`),
  CONSTRAINT `project_partners_created_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `project_partners_institution_id_fk` FOREIGN KEY (`institution_id`) REFERENCES `institutions` (`id`),
  CONSTRAINT `project_partners_modified_fk` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`),
  CONSTRAINT `project_partners_project_id_fk` FOREIGN KEY (`project_id`) REFERENCES `center_projects` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of center_project_partners
-- ----------------------------
INSERT INTO `center_project_partners` VALUES ('1', '1', '5', '0', '1', '2017-04-03 11:37:20', '1057', '1057', '');
INSERT INTO `center_project_partners` VALUES ('2', '1', '66', '0', '1', '2017-04-26 11:20:09', '1', '1', '');
INSERT INTO `center_project_partners` VALUES ('3', '4', '164', '0', '1', '2017-05-02 11:03:05', '1115', '1115', '');
INSERT INTO `center_project_partners` VALUES ('4', '4', '5', '0', '0', '2017-05-02 13:42:00', '1115', '1115', '');
INSERT INTO `center_project_partners` VALUES ('5', '3', '133', '0', '1', '2017-05-03 11:12:06', '1115', '1115', '');
INSERT INTO `center_project_partners` VALUES ('6', '3', '3', '0', '0', '2017-05-05 12:02:37', '1115', '1115', '');
INSERT INTO `center_project_partners` VALUES ('7', '3', '2', '0', '0', '2017-05-05 12:03:13', '1115', '1115', '');
INSERT INTO `center_project_partners` VALUES ('8', '3', '2', '0', '0', '2017-05-17 08:41:34', '1115', '1115', '');

-- ----------------------------
-- Table structure for center_project_partner_persons
-- ----------------------------
DROP TABLE IF EXISTS `center_project_partner_persons`;
CREATE TABLE `center_project_partner_persons` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_partner_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL,
  `active_since` timestamp NULL DEFAULT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `project_partner_person_partner_id_fk` (`project_partner_id`),
  KEY `project_partner_person_user_fk` (`user_id`),
  KEY `project_partner_created_fk` (`created_by`),
  KEY `project_partner_modified_fk` (`modified_by`),
  CONSTRAINT `project_partner_created_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `project_partner_modified_fk` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`),
  CONSTRAINT `project_partner_person_partner_id_fk` FOREIGN KEY (`project_partner_id`) REFERENCES `center_project_partners` (`id`),
  CONSTRAINT `project_partner_person_user_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of center_project_partner_persons
-- ----------------------------
INSERT INTO `center_project_partner_persons` VALUES ('1', '1', '1057', '1', '2017-04-03 11:37:20', '1057', '1057', '');
INSERT INTO `center_project_partner_persons` VALUES ('2', '1', '1', '1', '2017-04-03 11:37:20', '1057', '1057', '');
INSERT INTO `center_project_partner_persons` VALUES ('3', '3', '1109', '1', '2017-05-02 13:42:25', '1115', '1115', '');
INSERT INTO `center_project_partner_persons` VALUES ('4', '5', '66', '1', '2017-05-03 11:12:21', '1115', '1115', '');
INSERT INTO `center_project_partner_persons` VALUES ('5', '6', '1114', '0', '2017-05-05 12:02:50', '1115', '1115', '');
INSERT INTO `center_project_partner_persons` VALUES ('6', '8', '456', '0', '2017-05-17 08:41:45', '1115', '1115', '');

-- ----------------------------
-- Table structure for center_project_status
-- ----------------------------
DROP TABLE IF EXISTS `center_project_status`;
CREATE TABLE `center_project_status` (
  `id` int(11) NOT NULL,
  `name` text,
  `is_active` tinyint(4) NOT NULL,
  `active_since` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `project_status_created_fk` (`created_by`),
  KEY `project_status_modified_fk` (`modified_by`),
  CONSTRAINT `project_status_created_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `project_status_modified_fk` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of center_project_status
-- ----------------------------
INSERT INTO `center_project_status` VALUES ('2', 'On Going', '1', '2017-02-07 10:06:40', '3', '3', ' ');
INSERT INTO `center_project_status` VALUES ('3', 'Complete', '1', '2017-02-07 10:07:05', '3', '3', ' ');
INSERT INTO `center_project_status` VALUES ('4', 'Extended', '1', '2017-02-07 10:07:17', '3', '3', ' ');
INSERT INTO `center_project_status` VALUES ('5', 'Cancelled', '1', '2017-02-07 10:07:31', '3', '3', ' ');

-- ----------------------------
-- Table structure for center_project_types
-- ----------------------------
DROP TABLE IF EXISTS `center_project_types`;
CREATE TABLE `center_project_types` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` text,
  `is_active` tinyint(1) NOT NULL,
  `active_sice` timestamp NULL DEFAULT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `project_type_created_fk` (`created_by`),
  KEY `project_type_modified_fk` (`modified_by`),
  CONSTRAINT `project_type_created_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `project_type_modified_fk` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of center_project_types
-- ----------------------------
INSERT INTO `center_project_types` VALUES ('1', 'W1/W2', '0', '2017-02-28 11:08:50', '3', '3', ' ');
INSERT INTO `center_project_types` VALUES ('2', 'Bilateral', '1', '2017-02-28 11:09:10', '3', '3', ' ');
INSERT INTO `center_project_types` VALUES ('3', 'W3', '1', '2017-05-15 09:38:28', '3', '3', ' ');

-- ----------------------------
-- Table structure for center_regions
-- ----------------------------
DROP TABLE IF EXISTS `center_regions`;
CREATE TABLE `center_regions` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` text,
  `is_active` tinyint(1) NOT NULL,
  `active_since` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `regions_created_by_fk` (`created_by`) USING BTREE,
  KEY `regions_modified_by_fk` (`modified_by`) USING BTREE,
  CONSTRAINT `center_regions_ibfk_1` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `center_regions_ibfk_2` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of center_regions
-- ----------------------------
INSERT INTO `center_regions` VALUES ('1', 'Non specific', '1', '2016-12-07 11:13:41', '3', '3', ' ');
INSERT INTO `center_regions` VALUES ('2', 'Latin America and the Caribbean', '1', '2016-12-07 11:13:39', '3', '3', ' ');
INSERT INTO `center_regions` VALUES ('3', 'East Africa', '1', '2016-12-07 11:13:39', '3', '3', ' ');
INSERT INTO `center_regions` VALUES ('4', 'West Africa', '1', '2016-12-07 11:13:38', '3', '3', ' ');
INSERT INTO `center_regions` VALUES ('5', 'Sub- saharan Africa', '1', '2016-12-07 11:13:37', '3', '3', ' ');
INSERT INTO `center_regions` VALUES ('6', 'Southeast Asia', '1', '2016-12-07 11:13:36', '3', '3', ' ');
INSERT INTO `center_regions` VALUES ('7', 'Central Asia', '1', '2016-12-07 11:13:35', '3', '3', ' ');
INSERT INTO `center_regions` VALUES ('8', 'East Asia', '1', '2016-12-07 11:13:34', '3', '3', ' ');
INSERT INTO `center_regions` VALUES ('9', 'Other', '1', '2016-12-07 11:13:33', '3', '3', ' ');
INSERT INTO `center_regions` VALUES ('10', 'Global', '1', '2016-12-07 11:13:31', '3', '3', ' ');
INSERT INTO `center_regions` VALUES ('11', 'South Asia', '1', '2017-05-03 14:44:10', '3', '3', ' ');

-- ----------------------------
-- Table structure for center_roles
-- ----------------------------
DROP TABLE IF EXISTS `center_roles`;
CREATE TABLE `center_roles` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` text NOT NULL,
  `acronym` varchar(50) NOT NULL,
  `center_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `crp_id` (`center_id`) USING BTREE,
  KEY `fk_roles_center` (`center_id`) USING BTREE,
  CONSTRAINT `center_roles_ibfk_1` FOREIGN KEY (`center_id`) REFERENCES `centers` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of center_roles
-- ----------------------------
INSERT INTO `center_roles` VALUES ('1', 'Administrator', 'Admin', '1');
INSERT INTO `center_roles` VALUES ('2', 'CIAT Program Coordinator', 'Coord', '1');
INSERT INTO `center_roles` VALUES ('3', 'Research Area Director', 'RAD', '1');
INSERT INTO `center_roles` VALUES ('4', 'Research Program Leader', 'RPL', '1');
INSERT INTO `center_roles` VALUES ('5', 'Guest', 'G', '1');
INSERT INTO `center_roles` VALUES ('6', 'Super Administrator', 'Superadmin', '1');

-- ----------------------------
-- Table structure for center_role_permissions
-- ----------------------------
DROP TABLE IF EXISTS `center_role_permissions`;
CREATE TABLE `center_role_permissions` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `role_id` bigint(20) NOT NULL,
  `permission_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `roles_permission_roles_idx` (`role_id`) USING BTREE,
  KEY `roles_permission_user_permission_idx` (`permission_id`) USING BTREE,
  CONSTRAINT `center_role_permissions_ibfk_1` FOREIGN KEY (`permission_id`) REFERENCES `permissions` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `center_role_permissions_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `center_roles` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for center_section_statuses
-- ----------------------------
DROP TABLE IF EXISTS `center_section_statuses`;
CREATE TABLE `center_section_statuses` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `research_program_id` int(11) DEFAULT NULL,
  `project_id` bigint(20) DEFAULT NULL,
  `research_outcome_id` int(11) DEFAULT NULL,
  `research_output_id` int(11) DEFAULT NULL,
  `deliverable_id` bigint(20) DEFAULT NULL,
  `section_name` varchar(255) NOT NULL COMMENT 'Some section name (would be the action name)',
  `missing_fields` mediumtext COMMENT 'The list of missing fields per section',
  `cycle` varchar(100) DEFAULT NULL,
  `year` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `section_research_program_fk` (`research_program_id`) USING BTREE,
  KEY `section_research_outcome_fk` (`research_outcome_id`) USING BTREE,
  KEY `section_research_output_fk` (`research_output_id`) USING BTREE,
  KEY `section_statuses_project_id_fk` (`project_id`),
  KEY `section_statuses_deliverable_id_fk` (`deliverable_id`),
  CONSTRAINT `center_section_statuses_ibfk_1` FOREIGN KEY (`research_outcome_id`) REFERENCES `center_outcomes` (`id`),
  CONSTRAINT `center_section_statuses_ibfk_2` FOREIGN KEY (`research_output_id`) REFERENCES `center_outputs` (`id`),
  CONSTRAINT `center_section_statuses_ibfk_3` FOREIGN KEY (`research_program_id`) REFERENCES `center_programs` (`id`),
  CONSTRAINT `section_statuses_deliverable_id_fk` FOREIGN KEY (`deliverable_id`) REFERENCES `center_deliverables` (`id`),
  CONSTRAINT `section_statuses_project_id_fk` FOREIGN KEY (`project_id`) REFERENCES `center_projects` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=131 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of center_section_statuses
-- ----------------------------
INSERT INTO `center_section_statuses` VALUES ('1', null, '3', null, null, null, 'projectDescription', 'programImpact.action.draft;OCS Code;Project Description;Original Donor;Global Select;Region Select;There are not funding source(s) added yet.', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('2', null, '3', null, null, '6', 'deliverableList', 'Deliverable Type;There are not output(s) added yet.;There are not Document(s) added yet.', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('3', null, '3', null, null, '7', 'deliverableList', 'Deliverable Type;There are not output(s) added yet.;There are not Document(s) added yet.;Name;End year;Deliverable Type;There are not output(s) added yet.;There are not Document(s) added yet.', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('4', null, '3', null, null, '8', 'deliverableList', 'Deliverable Type;There are not output(s) added yet.;There are not Document(s) added yet.;Name;End year;Deliverable Type;There are not output(s) added yet.;There are not Document(s) added yet.;Name;End year;Deliverable Type;There are not output(s) added yet.;There are not Document(s) added yet.', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('5', '4', null, null, null, null, 'programimpacts', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('6', '4', null, '15', null, null, 'outcomesList', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('7', '4', null, '16', null, null, 'outcomesList', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('8', '4', null, '17', null, null, 'outcomesList', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('9', '4', null, '13', null, null, 'outcomesList', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('10', '4', null, '14', null, null, 'outcomesList', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('11', '4', null, '12', null, null, 'outcomesList', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('12', '6', null, '28', null, null, 'outcomesList', 'Milestones', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('13', '6', null, '29', null, null, 'outcomesList', 'Milestones', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('14', '6', null, '30', null, null, 'outcomesList', 'Milestones', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('15', '6', null, '32', null, null, 'outcomesList', 'Milestones', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('16', '6', null, '33', null, null, 'outcomesList', 'Milestones', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('17', '6', null, '34', null, null, 'outcomesList', 'Milestones', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('18', '6', null, '35', null, null, 'outcomesList', 'Milestones', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('19', '6', null, null, null, null, 'researchTopics', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('20', '7', null, null, null, null, 'researchTopics', 'researchTopic.action.draft', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('21', '6', null, null, null, null, 'programimpacts', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('22', '1', null, '11', null, null, 'outcomesList', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('24', '1', null, '27', null, null, 'outcomesList', 'Select an Research Impact;Outcome Statement;Estimated Target year;Target unit;Milestones', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('25', '1', null, '36', null, null, 'outcomesList', 'Select an Research Impact;Outcome Statement;Estimated Target year;Target unit;Milestones;Select an Research Impact;Outcome Statement;Estimated Target year;Target unit;Milestones', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('26', '1', null, '38', null, null, 'outcomesList', 'Select an Research Impact;Outcome Statement;Estimated Target year;Target unit;Milestones;Select an Research Impact;Outcome Statement;Estimated Target year;Target unit;Milestones;Select an Research Impact;Outcome Statement;Estimated Target year;Target unit;Milestones', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('27', '1', null, null, null, null, 'programimpacts', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('28', null, '4', null, null, '4', 'deliverableList', 'Name;End year;Deliverable Type;There are not output(s) added yet.;There are not Document(s) added yet.', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('29', null, '14', null, null, null, 'projectDescription', 'Global Select;Region Select;There are not funding source(s) added yet.;There are not output(s) added yet.', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('30', null, '13', null, null, null, 'projectDescription', 'Global Select;Region Select', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('31', null, '16', null, null, null, 'projectDescription', 'programImpact.action.draft;Global Select;Region Select;There are not funding source(s) added yet.;There are not output(s) added yet.', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('32', null, '17', null, null, null, 'projectDescription', 'Project CIAT Short name;Global Select;Region Select;There are not funding source(s) added yet.', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('33', null, '18', null, null, null, 'projectDescription', 'Global Select;Region Select;There are not funding source(s) added yet.', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('34', null, '19', null, null, null, 'projectDescription', 'Global Select;Region Select;There are not funding source(s) added yet.', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('35', null, '11', null, null, null, 'projectDescription', 'Global Select;Region Select;There are not funding source(s) added yet.', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('46', '6', null, null, '68', null, 'outputsList', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('47', '6', null, null, '69', null, 'outputsList', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('48', '6', null, '39', null, null, 'outcomesList', 'Milestones', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('49', '6', null, null, '71', null, 'outputsList', 'Output name;nextUsers;Output Next Users required', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('50', '6', null, null, '70', null, 'outputsList', 'Output name;nextUsers;Output Next Users required', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('51', '6', null, '40', null, null, 'outcomesList', 'Milestones', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('52', '6', null, null, '72', null, 'outputsList', 'Output name;nextUsers;Output Next Users required', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('53', '6', null, null, '73', null, 'outputsList', 'Output name;nextUsers;Output Next Users required', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('54', '6', null, '41', null, null, 'outcomesList', 'Milestones', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('55', '6', null, '42', null, null, 'outcomesList', 'Milestones;Estimated Target year', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('56', '6', null, null, '74', null, 'outputsList', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('57', '6', null, null, '75', null, 'outputsList', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('58', '6', null, null, '76', null, 'outputsList', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('59', '6', null, null, '77', null, 'outputsList', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('62', '6', null, null, '80', null, 'outputsList', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('63', '6', null, null, '81', null, 'outputsList', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('64', '6', null, null, '82', null, 'outputsList', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('65', '6', null, null, '83', null, 'outputsList', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('66', '6', null, null, '84', null, 'outputsList', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('67', '6', null, null, '85', null, 'outputsList', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('68', '6', null, null, '86', null, 'outputsList', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('69', '6', null, null, '87', null, 'outputsList', 'Output name;nextUsers;Output Next Users required', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('70', '6', null, null, '88', null, 'outputsList', 'Output name;nextUsers;Output Next Users required', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('71', '6', null, null, '89', null, 'outputsList', 'Output name;nextUsers;Output Next Users required', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('72', '6', null, null, '90', null, 'outputsList', 'Output name;nextUsers;Output Next Users required', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('73', '6', null, null, '92', null, 'outputsList', 'Output name;nextUsers;Output Next Users required', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('74', '6', null, null, '93', null, 'outputsList', 'Output name;nextUsers;Output Next Users required', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('75', '1', null, '43', null, null, 'outcomesList', 'Select an Research Impact;Outcome Statement;Estimated Target year;Target unit;Milestones;Select an Research Impact;Outcome Statement;Estimated Target year;Target unit;Milestones;Select an Research Impact;Outcome Statement;Estimated Target year;Target unit;Milestones;Select an Research Impact;Outcome Statement;Estimated Target year;Target unit;Milestones', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('77', '4', null, null, null, null, 'researchTopics', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('78', '4', null, null, '33', null, 'outputsList', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('79', '4', null, null, '34', null, 'outputsList', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('80', '4', null, null, '35', null, 'outputsList', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('81', '4', null, null, '36', null, 'outputsList', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('82', '4', null, null, '37', null, 'outputsList', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('83', '4', null, null, '38', null, 'outputsList', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('84', '4', null, null, '39', null, 'outputsList', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('85', '4', null, null, '40', null, 'outputsList', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('86', '4', null, null, '41', null, 'outputsList', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('87', '4', null, null, '42', null, 'outputsList', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('88', '4', null, null, '43', null, 'outputsList', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('89', '4', null, null, '44', null, 'outputsList', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('90', '4', null, null, '45', null, 'outputsList', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('91', '4', null, null, '46', null, 'outputsList', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('92', '4', null, null, '47', null, 'outputsList', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('93', '4', null, null, '49', null, 'outputsList', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('94', '4', null, null, '50', null, 'outputsList', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('95', '4', null, null, '51', null, 'outputsList', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('96', '4', null, null, '52', null, 'outputsList', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('97', '4', null, null, '53', null, 'outputsList', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('98', '4', null, null, '54', null, 'outputsList', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('99', '4', null, null, '55', null, 'outputsList', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('100', '4', null, null, '48', null, 'outputsList', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('101', '4', null, null, '19', null, 'outputsList', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('102', '4', null, null, '20', null, 'outputsList', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('103', '4', null, null, '21', null, 'outputsList', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('104', '4', null, null, '22', null, 'outputsList', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('105', '4', null, null, '23', null, 'outputsList', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('106', '4', null, null, '24', null, 'outputsList', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('107', '4', null, null, '25', null, 'outputsList', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('108', '4', null, null, '26', null, 'outputsList', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('109', '4', null, null, '27', null, 'outputsList', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('110', '4', null, null, '28', null, 'outputsList', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('111', '4', null, null, '29', null, 'outputsList', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('112', '4', null, null, '30', null, 'outputsList', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('113', '4', null, null, '31', null, 'outputsList', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('114', '4', null, null, '32', null, 'outputsList', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('115', '4', null, null, '17', null, 'outputsList', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('116', '4', null, null, '18', null, 'outputsList', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('117', '4', null, null, '15', null, 'outputsList', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('118', '4', null, null, '16', null, 'outputsList', '', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('119', null, '20', null, null, null, 'projectDescription', 'Global Select;Region Select;There are not funding source(s) added yet.', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('120', null, '21', null, null, null, 'projectDescription', 'Global Select;Region Select;There are not funding source(s) added yet.', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('121', null, '22', null, null, null, 'projectDescription', 'Global Select;Region Select;There are not funding source(s) added yet.', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('122', null, '23', null, null, null, 'projectDescription', 'Global Select;Region Select;Project Leader;There are not funding source(s) added yet.', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('123', null, '24', null, null, null, 'projectDescription', 'Global Select;Region Select', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('124', null, '25', null, null, null, 'projectDescription', 'Global Select;Region Select;There are not funding source(s) added yet.', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('125', null, '27', null, null, null, 'projectDescription', 'Global Select;Region Select', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('126', null, '28', null, null, null, 'projectDescription', 'Global Select;Region Select;There are not funding source(s) added yet.;There are not output(s) added yet.', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('127', null, '29', null, null, null, 'projectDescription', 'Project Description;Global Select;Region Select;There are not funding source(s) added yet.', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('128', null, '30', null, null, null, 'projectDescription', 'Global Select;Region Select;There are not funding source(s) added yet.', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('129', null, '31', null, null, null, 'projectDescription', 'Global Select;Region Select;There are not funding source(s) added yet.', null, '2017');
INSERT INTO `center_section_statuses` VALUES ('130', null, '32', null, null, null, 'projectDescription', 'Global Select;Region Select;There are not funding source(s) added yet.', null, '2017');

-- ----------------------------
-- Table structure for center_submissions
-- ----------------------------
DROP TABLE IF EXISTS `center_submissions`;
CREATE TABLE `center_submissions` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `program_id` int(11) DEFAULT NULL,
  `project_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) NOT NULL,
  `cycle_id` bigint(20) DEFAULT NULL COMMENT 'Cycling period type.',
  `date_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'The date time when the report was made.',
  `modification_justification` mediumtext,
  `year` smallint(6) DEFAULT NULL COMMENT 'Year to which the report is happening.',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`) USING BTREE,
  KEY `program_id` (`program_id`),
  KEY `submissions_cycle_fk` (`cycle_id`),
  KEY `submissions_project_fk` (`project_id`),
  CONSTRAINT `submissions_cycle_fk` FOREIGN KEY (`cycle_id`) REFERENCES `center_cycles` (`id`),
  CONSTRAINT `submissions_program_fk` FOREIGN KEY (`program_id`) REFERENCES `center_programs` (`id`),
  CONSTRAINT `submissions_project_fk` FOREIGN KEY (`project_id`) REFERENCES `center_projects` (`id`),
  CONSTRAINT `submissions_users_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of center_submissions
-- ----------------------------

-- ----------------------------
-- Table structure for center_target_units
-- ----------------------------
DROP TABLE IF EXISTS `center_target_units`;
CREATE TABLE `center_target_units` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` text NOT NULL,
  `is_active` tinyint(1) NOT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `active_since` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified_by` bigint(20) NOT NULL,
  `modification_justification` text NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_target_units_created_by_users_id` (`created_by`) USING BTREE,
  KEY `fk_target_units_modified_by_users_id` (`modified_by`) USING BTREE,
  CONSTRAINT `center_target_units_ibfk_1` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `center_target_units_ibfk_2` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of center_target_units
-- ----------------------------
INSERT INTO `center_target_units` VALUES ('-1', 'Not Applicable', '0', '3', '2016-09-19 16:35:08', '3', '');
INSERT INTO `center_target_units` VALUES ('1', 'Advanced Research Institutes', '1', '3', '2016-06-29 08:22:36', '843', '');
INSERT INTO `center_target_units` VALUES ('2', 'Agricultural Development Initiatives', '1', '3', '2016-07-05 17:48:58', '843', '');
INSERT INTO `center_target_units` VALUES ('3', 'Agricultural Research Initiatives', '1', '3', '2016-07-12 03:15:20', '843', '');
INSERT INTO `center_target_units` VALUES ('4', 'CGIAR Centers', '1', '3', '2016-07-18 12:41:42', '843', '');
INSERT INTO `center_target_units` VALUES ('5', 'Civil Society Organizations (CSO) ', '1', '3', '2016-07-24 22:08:04', '843', '');
INSERT INTO `center_target_units` VALUES ('6', 'Countries/ States', '1', '3', '2016-07-31 07:34:26', '843', '');
INSERT INTO `center_target_units` VALUES ('7', 'CRPs ', '1', '3', '2016-08-06 17:00:48', '843', '');
INSERT INTO `center_target_units` VALUES ('8', 'Decision Makers', '1', '3', '2016-08-13 02:27:10', '843', '');
INSERT INTO `center_target_units` VALUES ('9', 'Development Organizations', '1', '3', '2016-08-19 11:53:32', '843', '');
INSERT INTO `center_target_units` VALUES ('10', 'Farmer´s Groups', '0', '3', '2016-08-25 21:19:54', '3', 'a');
INSERT INTO `center_target_units` VALUES ('11', 'Farmers ', '1', '3', '2016-09-01 06:46:16', '843', '');
INSERT INTO `center_target_units` VALUES ('12', 'Farming households', '1', '3', '2016-09-07 16:12:38', '843', '');
INSERT INTO `center_target_units` VALUES ('13', 'Fisheries', '1', '3', '2016-09-14 01:39:00', '843', '');
INSERT INTO `center_target_units` VALUES ('14', 'Goverment Agencies', '1', '3', '2016-09-20 11:05:22', '843', '');
INSERT INTO `center_target_units` VALUES ('15', 'Hectares', '1', '3', '2016-09-26 20:31:44', '843', '');
INSERT INTO `center_target_units` VALUES ('16', 'Institutions', '1', '3', '2016-10-03 05:58:06', '843', '');
INSERT INTO `center_target_units` VALUES ('17', 'International Research Organizations', '1', '3', '2016-10-09 15:24:28', '843', '');
INSERT INTO `center_target_units` VALUES ('19', 'Livestock Dependent Communities', '1', '988', '2016-07-19 15:11:33', '843', '');
INSERT INTO `center_target_units` VALUES ('20', 'Market Buyers', '1', '988', '2016-07-22 09:21:54', '843', '');
INSERT INTO `center_target_units` VALUES ('21', 'Market Sellers', '1', '988', '2016-07-22 09:25:31', '843', '');
INSERT INTO `center_target_units` VALUES ('22', 'Markets', '1', '988', '2016-07-22 09:28:30', '843', '');
INSERT INTO `center_target_units` VALUES ('23', 'National Research and Extension Institutes (NAREs)', '1', '988', '2016-07-22 11:23:07', '843', '');
INSERT INTO `center_target_units` VALUES ('24', 'National Research Institutes (NARs)', '1', '988', '2016-07-22 11:23:36', '843', '');
INSERT INTO `center_target_units` VALUES ('25', 'National/State Organizations', '1', '988', '2016-07-22 11:58:38', '843', '');
INSERT INTO `center_target_units` VALUES ('26', 'Non- governamental Organizations (NGOs) ', '1', '988', '2016-07-22 12:03:40', '843', '');
INSERT INTO `center_target_units` VALUES ('27', 'Organizations and Institutions', '1', '988', '2016-07-22 12:04:27', '843', '');
INSERT INTO `center_target_units` VALUES ('28', 'Partners', '1', '988', '2016-07-22 12:21:28', '843', '');
INSERT INTO `center_target_units` VALUES ('29', 'Partnerships ', '1', '988', '2016-07-22 12:22:35', '843', '');
INSERT INTO `center_target_units` VALUES ('30', 'Persons', '1', '988', '2016-07-22 12:23:22', '843', '');
INSERT INTO `center_target_units` VALUES ('31', 'Policy Decisions', '1', '988', '2016-07-25 09:22:41', '843', '');
INSERT INTO `center_target_units` VALUES ('32', 'Policy Makers', '1', '988', '2016-07-25 09:24:10', '843', '');
INSERT INTO `center_target_units` VALUES ('33', 'Private Sector Organizations', '1', '988', '2016-07-25 09:26:31', '843', '');
INSERT INTO `center_target_units` VALUES ('35', 'Purchasing Companies', '1', '13', '2016-08-01 11:27:57', '843', '');
INSERT INTO `center_target_units` VALUES ('37', 'Recommendations Implemented', '1', '1061', '2016-08-03 12:32:23', '843', '');
INSERT INTO `center_target_units` VALUES ('38', 'Regional Bodies', '1', '1061', '2016-08-03 12:35:27', '843', '');
INSERT INTO `center_target_units` VALUES ('39', 'Researchers', '1', '843', '2016-08-25 07:52:47', '843', '');
INSERT INTO `center_target_units` VALUES ('40', 'Scientific Community', '1', '843', '2016-08-25 08:21:33', '843', '');
INSERT INTO `center_target_units` VALUES ('41', 'Small and Medium Enterprises (SMEs) ', '1', '843', '2016-08-25 08:23:54', '843', '');
INSERT INTO `center_target_units` VALUES ('42', 'Stakeholders', '1', '3', '2016-11-24 14:43:53', '3', '');
INSERT INTO `center_target_units` VALUES ('44', 'Subnational Public/Private Initiatives', '1', '3', '2016-11-25 13:30:44', '3', '');
INSERT INTO `center_target_units` VALUES ('45', 'Technology Developers', '1', '3', '2016-11-25 13:32:29', '3', '');
INSERT INTO `center_target_units` VALUES ('46', 'USD mio. new investment', '1', '3', '2017-05-03 14:41:13', '3', ' ');
INSERT INTO `center_target_units` VALUES ('47', 'Value Chain Actors', '1', '3', '2017-05-03 14:41:30', '3', ' ');
INSERT INTO `center_target_units` VALUES ('48', 'Wholesale companies', '1', '3', '2017-05-03 14:41:39', '3', ' ');
INSERT INTO `center_target_units` VALUES ('49', 'Women Farmer´s Groups', '1', '3', '2017-05-03 14:41:51', '3', ' ');
INSERT INTO `center_target_units` VALUES ('50', 'Other', '1', '3', '2017-05-03 14:41:58', '3', ' ');

-- ----------------------------
-- Table structure for center_topics
-- ----------------------------
DROP TABLE IF EXISTS `center_topics`;
CREATE TABLE `center_topics` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `research_topic` text NOT NULL,
  `short_name` text,
  `research_program_id` int(11) NOT NULL,
  `color` varchar(8) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL,
  `active_since` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `fk_rtopic_rprogram` (`research_program_id`) USING BTREE,
  KEY `fk_rtropic_created_by` (`created_by`) USING BTREE,
  KEY `fk_rtropic_modified_by` (`modified_by`) USING BTREE,
  CONSTRAINT `center_topics_ibfk_1` FOREIGN KEY (`research_program_id`) REFERENCES `center_programs` (`id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT `center_topics_ibfk_2` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `center_topics_ibfk_3` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of center_topics
-- ----------------------------
INSERT INTO `center_topics` VALUES ('1', 'Conservation of genetic resources: beans, cassava and tropical-forages', '', '7', '#ecf0f1', '1', '2017-03-23 13:48:36', '1114', '1057', 'Modified on Thu Apr 20 15:31:14 COT 2017');
INSERT INTO `center_topics` VALUES ('2', 'Genetic Intensification', null, '8', '#ecf0f1', '1', '2017-04-07 11:47:57', '1115', '1115', null);
INSERT INTO `center_topics` VALUES ('3', 'Characterization and evaluation of genetic resources', '', '7', '#ecf0f1', '1', '2017-04-07 13:36:35', '1115', '1057', 'Modified on Thu Apr 20 15:31:09 COT 2017');
INSERT INTO `center_topics` VALUES ('4', 'Contribution to an effective Global System for the conservation and use of plant genetic resources', '', '7', '#ecf0f1', '1', '2017-04-07 13:36:35', '1115', '1057', 'Modified on Thu Apr 20 15:31:09 COT 2017');
INSERT INTO `center_topics` VALUES ('5', 'Social and economic benefits of Ecosystem Services', 'Social and Economic Benefits', '3', '#ecf0f1', '1', '2017-04-17 15:12:13', '1109', '1115', 'Modified on Thu Apr 20 16:27:01 COT 2017');
INSERT INTO `center_topics` VALUES ('6', '', '', '1', '#ecf0f1', '0', '2017-04-20 15:40:02', '1057', '1057', 'Modified on Thu Apr 20 15:42:49 COT 2017');
INSERT INTO `center_topics` VALUES ('7', 'Priorities and Policies for Climate Smart Agriculture', 'Priorities & Policies for CSA', '2', '#ecf0f1', '1', '2017-05-02 10:31:08', '1114', '1115', 'Modified on Fri May 19 11:46:20 COT 2017');
INSERT INTO `center_topics` VALUES ('8', 'Climate Smart Technologies and Practices', 'CS Technologies and Practices', '2', '#ecf0f1', '1', '2017-05-02 14:34:59', '1114', '1114', null);
INSERT INTO `center_topics` VALUES ('9', 'Low Emissions Development', 'Low Emissions', '2', '#ecf0f1', '1', '2017-05-02 14:34:59', '1114', '1114', null);
INSERT INTO `center_topics` VALUES ('10', 'Climate services and safety nets', 'Climate services and safety', '2', '#ecf0f1', '1', '2017-05-02 14:34:59', '1114', '1114', null);
INSERT INTO `center_topics` VALUES ('11', 'Inclusive Value Chains', 'IVC', '1', '#ecf0f1', '1', '2017-05-03 11:07:25', '1115', '1115', null);
INSERT INTO `center_topics` VALUES ('12', 'Enhancement of Genetic Resources', 'Genetic Resources', '4', '#ecf0f1', '1', '2017-05-05 10:12:45', '1115', '1115', 'Modified on Tue May 16 10:15:35 COT 2017');
INSERT INTO `center_topics` VALUES ('13', 'Agronomy and Soil Manangement', 'Ag and Soil Manangement', '4', '#ecf0f1', '1', '2017-05-05 10:13:13', '1115', '1115', null);
INSERT INTO `center_topics` VALUES ('14', 'Crop Protection', 'Crop Protection', '4', '#ecf0f1', '1', '2017-05-05 10:17:32', '1115', '1115', null);
INSERT INTO `center_topics` VALUES ('15', 'Seed Systems and Harvesting', 'Seed Systems/Harvesting', '4', '#ecf0f1', '1', '2017-05-05 10:17:32', '1115', '1115', null);
INSERT INTO `center_topics` VALUES ('16', 'Post Harvest & Enhanced Nutrition', 'Nutrition', '4', '#ecf0f1', '1', '2017-05-05 10:17:32', '1115', '1115', null);
INSERT INTO `center_topics` VALUES ('17', 'Value Chain and Policy', 'Value Chain', '4', '#ecf0f1', '1', '2017-05-05 10:17:32', '1115', '1115', null);
INSERT INTO `center_topics` VALUES ('18', 'aaa', 'aaa', '5', '#ecf0f1', '1', '2017-05-16 09:40:47', '1115', '1115', null);
INSERT INTO `center_topics` VALUES ('19', 'Accelerating Impact and Equity', 'Impact and Equity', '6', '#ecf0f1', '1', '2017-05-17 10:24:35', '1114', '1114', null);
INSERT INTO `center_topics` VALUES ('20', 'Global rice array  through  a worldwide field laboratory', 'worldwide field laboratory', '6', '#ecf0f1', '1', '2017-05-17 10:24:35', '1114', '1114', 'Modified on Wed May 17 15:31:05 COT 2017');
INSERT INTO `center_topics` VALUES ('21', 'Global rice array -global phenotyping tools', 'phenotyping tools', '6', '#ecf0f1', '1', '2017-05-17 10:24:35', '1114', '1114', 'Modified on Wed May 17 15:31:05 COT 2017');
INSERT INTO `center_topics` VALUES ('22', 'Global rice array Genetics of rice plant interacion w/biotic environment', 'Genetics of rice plant interac', '6', '#ecf0f1', '1', '2017-05-17 15:31:05', '1114', '1114', 'Modified on Tue May 30 10:54:49 COT 2017');
INSERT INTO `center_topics` VALUES ('23', 'Global rice array-Discovery of genomic associations', 'Genomic associations', '6', '#ecf0f1', '1', '2017-05-17 15:31:05', '1114', '1114', null);
INSERT INTO `center_topics` VALUES ('24', 'Global rice array-Big Data integration platform', 'Big data integration platform', '6', '#ecf0f1', '1', '2017-05-17 15:31:05', '1114', '1114', null);
INSERT INTO `center_topics` VALUES ('25', 'New Rice Varieties-Harnessing rice diversity', 'Harnessing rice diversity', '6', '#ecf0f1', '1', '2017-05-18 16:31:50', '1114', '1114', null);
INSERT INTO `center_topics` VALUES ('26', 'New Rice Varieties -Precision breeding (upstream)', 'Precision breeding (upstream)', '6', '#ecf0f1', '1', '2017-05-18 16:31:50', '1114', '1114', null);
INSERT INTO `center_topics` VALUES ('27', 'New Rice Varieties- Intensive systems', 'Intensive systems', '6', '#ecf0f1', '1', '2017-05-18 16:31:50', '1114', '1114', null);
INSERT INTO `center_topics` VALUES ('28', 'New Rice Varieties-Unfavorable ecosystems', 'Unfavorable ecosystems', '6', '#ecf0f1', '1', '2017-05-18 16:31:50', '1114', '1114', null);
INSERT INTO `center_topics` VALUES ('29', 'New Rice Varieties-Rice grain quality and nutrition', 'Rice grain quality and nutriti', '6', '#ecf0f1', '1', '2017-05-18 16:31:50', '1114', '1114', null);

-- ----------------------------
-- Table structure for center_users
-- ----------------------------
DROP TABLE IF EXISTS `center_users`;
CREATE TABLE `center_users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `center_id` int(11) NOT NULL,
  `is_active` tinyint(1) NOT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `active_since` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified_by` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_center_id` (`center_id`) USING BTREE,
  KEY `fk_created_by` (`created_by`) USING BTREE,
  KEY `fk_modified_by` (`modified_by`) USING BTREE,
  KEY `fk_user_id` (`user_id`) USING BTREE,
  CONSTRAINT `center_users_ibfk_1` FOREIGN KEY (`center_id`) REFERENCES `centers` (`id`),
  CONSTRAINT `center_users_ibfk_2` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `center_users_ibfk_3` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`),
  CONSTRAINT `center_users_ibfk_4` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for center_user_roles
-- ----------------------------
DROP TABLE IF EXISTS `center_user_roles`;
CREATE TABLE `center_user_roles` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `role_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`) USING BTREE,
  KEY `role_id` (`role_id`) USING BTREE,
  CONSTRAINT `center_user_roles_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `center_user_roles_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `center_roles` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8;


