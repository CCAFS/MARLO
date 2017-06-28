SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for deliverable_types
-- ----------------------------
DROP TABLE IF EXISTS `deliverable_types`;
CREATE TABLE `deliverable_types` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` text NOT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL,
  `active_since` timestamp NULL DEFAULT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `deliverable_type_created_fk` (`created_by`),
  KEY `deliverable_type_modified_fk` (`modified_by`),
  KEY `deliverable_parent_fk` (`parent_id`),
  CONSTRAINT `deliverable_parent_fk` FOREIGN KEY (`parent_id`) REFERENCES `deliverable_types` (`id`),
  CONSTRAINT `deliverable_type_created_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `deliverable_type_modified_fk` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=63 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of deliverable_types
-- ----------------------------
INSERT INTO `deliverable_types` VALUES ('1', 'Agricultural inputs/ Cultivars/ Lines', null, '1', '2017-03-13 11:29:49', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('2', 'GR Products', null, '1', '2017-03-13 11:30:05', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('3', 'Practices/ procedures', null, '1', '2017-05-04 07:41:58', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('4', 'Methods/ Scientific methodology or method', null, '1', '2017-05-04 07:42:12', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('5', 'Data and information outputs, including datasets, databases and models', null, '1', '2017-05-04 07:43:18', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('6', 'Tools and Computer Software', null, '1', '2017-05-04 07:43:54', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('7', 'Policy recommendations', null, '1', '2017-05-04 07:44:13', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('8', 'Situational and Implications analyses and quantitative and qualitative impact assessments', null, '1', '2017-05-04 07:45:18', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('9', 'Project designs- new researchinitiatives', null, '1', '2017-05-04 07:45:34', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('10', 'Reports, Reference Materials and Other Papers', null, '1', '2017-05-04 07:45:50', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('11', 'Peer reviewed Publications', null, '1', '2017-05-04 07:46:03', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('12', 'Communication Products and Multimedia', null, '1', '2017-05-04 07:46:57', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('13', 'Capacity building/ Capacity strengthening (external, not strategy associated to another product)', null, '1', '2017-05-04 07:47:08', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('14', 'Traits (?) / Molecular markers Strains', '1', '1', '2017-05-22 08:28:47', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('15', 'Cultivar generated and released', '1', '1', '2017-05-22 08:28:45', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('16', 'Cultivar tested and recommended', '1', '1', '2017-05-22 08:28:57', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('17', 'Improved lines', '1', '1', '2017-05-22 08:29:08', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('18', 'Varieties/ germplasm /Elite event', '1', '1', '2017-05-22 08:29:25', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('19', 'GR Inventories', '2', '1', '2017-05-22 08:29:39', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('20', 'GR Protocols for identification', '2', '1', '2017-05-22 08:29:52', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('21', 'GR Characterization', '2', '1', '2017-05-22 08:30:38', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('22', 'Creation of collections of biological material', '2', '1', '2017-05-22 08:30:52', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('23', 'Preserved / continued existence of genetic Material', '2', '1', '2017-05-22 08:31:03', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('24', 'Agricultural practices or processes', '3', '1', '2017-05-22 08:31:14', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('25', 'Agro-industrial processes', '3', '1', '2017-05-22 08:31:28', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('26', 'Methodological approaches and tools', '4', '1', '2017-05-22 08:31:40', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('27', 'Guidelines', '4', '1', '2017-05-22 08:32:03', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('28', 'Data (unprocessed, raw data)', '5', '1', '2017-05-22 08:32:18', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('29', 'Datasets (collection of data)', '5', '1', '2017-05-22 08:32:32', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('30', 'Databases (organized collection of data)', '5', '1', '2017-05-22 08:32:46', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('31', 'Models', '5', '1', '2017-05-22 08:32:57', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('32', 'Platforms - Data Portals for dissemination', '6', '1', '2017-05-22 08:33:14', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('33', 'Maps', '6', '1', '2017-05-22 08:33:25', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('34', 'Tools', '6', '1', '2017-05-22 08:33:35', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('35', 'Website', '6', '1', '2017-05-22 08:33:45', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('36', 'Software', '6', '1', '2017-05-22 08:33:55', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('37', 'Biophysical, socio-economical, environmental, cultural, market related constraints and barriers and trade-offs, including gender perspectives, analyses to inform the development of CC adaptation and mitigation plans', '7', '1', '2017-05-22 08:34:17', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('38', 'Recommendations about the design of PES-type schemes', '7', '1', '2017-05-22 08:34:30', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('39', 'Portfolio (system/site specific-packages) of climate smart adaptation options (practices and technologies)', '7', '1', '2017-05-22 08:34:40', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('40', 'Situational and Implications analyses and quantitative and qualitative impact assessments', '8', '1', '2017-05-22 08:35:00', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('41', 'Specific projects or programs co-designed and implemented', '9', '1', '2017-05-22 08:35:12', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('42', 'Research report', '10', '1', '2017-05-22 08:35:25', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('43', 'Policy briefs-Briefing paper', '10', '1', '2017-05-22 08:35:40', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('44', 'Working paper', '10', '1', '2017-05-22 08:35:51', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('45', 'Conference proceedings/paper', '10', '1', '2017-05-22 08:36:01', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('46', 'Seminar paper', '10', '1', '2017-05-22 08:36:13', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('47', 'Discussion paper', '10', '1', '2017-05-22 08:36:23', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('48', 'Reference material', '10', '1', '2017-05-22 08:36:34', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('49', 'Non-peer reviewed articles', '10', '1', '2017-05-22 08:37:09', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('50', 'Case Study', '10', '1', '2017-05-22 08:37:19', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('51', 'Peer-reviewed journal articles', '11', '1', '2017-05-22 08:37:32', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('52', 'Books', '11', '1', '2017-05-22 08:37:45', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('53', 'Book chapters', '11', '1', '2017-05-22 08:38:00', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('54', 'Articles for media or news', '12', '1', '2017-05-22 08:38:17', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('55', 'Social media outputs', '12', '1', '2017-05-22 08:38:28', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('56', 'Poster', '12', '1', '2017-05-22 08:38:37', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('57', 'Presentations', '12', '1', '2017-05-22 08:38:47', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('58', 'Video', '12', '1', '2017-05-22 08:38:56', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('59', 'Audio', '12', '1', '2017-05-22 08:39:12', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('60', 'Images', '12', '1', '2017-05-22 08:39:24', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('61', 'Workshops', '13', '1', '2017-05-22 08:39:42', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('62', 'Meetings, Fairs, Events', '13', '1', '2017-05-22 08:39:52', '3', '3', ' ');
