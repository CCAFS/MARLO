SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for deliverable_types
-- ----------------------------
DROP TABLE IF EXISTS `deliverable_types`;
CREATE TABLE `deliverable_types` (
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
  CONSTRAINT `deliverable_type_created` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `deliverable_type_modified` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`),
  CONSTRAINT `deliverable_types_ibfk_1` FOREIGN KEY (`parent_id`) REFERENCES `deliverable_types` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=112 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of deliverable_types
-- ----------------------------
INSERT INTO `deliverable_types` VALUES ('42', 'Data, models and tools', null, null, '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('43', 'Reports and other publications ', null, null, '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('44', 'Outreach products', null, null, '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('46', 'Training materials', null, null, '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('49', 'Articles and Books', null, null, '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('51', 'Database/Dataset/Data documentation', '42', 'Dataset is a collection of data. Database is an organized collection of data. Data paper, dataset, database, repository', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('52', 'Data portal/Tool/Model code/Computer software', '42', 'Data Portals for dissemination, tools, model codes, computer software. search engine, games, algorithms.', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('53', 'Thesis', '43', 'Student thesis', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('54', 'Research workshop report', '43', 'Research workshop report, proceedings, workshop summary paper.', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('55', 'Policy brief/policy note/briefing paper', '43', 'Policy brief, policy note, briefing paper, 2020 synthesis, 2020 Vision focus, brief, policy paper, policy report, policy review, policy statement', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('56', 'Discussion paper/Working paper/White paper', '43', 'Discussion paper, policy working paper, progress report, research paper, research report, technical note, technical report, unpublished paper, white paper, working paper', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('57', 'Conference paper / Seminar paper', '43', 'Conference paper, seminar paper', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('58', 'Lecture/Training Course Material', '46', 'Lecture, training course material', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('59', 'Guidebook/Handbook/Good Practice Note', '46', 'Guidebook, handbook, good practice note', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('60', 'User manual/Technical Guide', '46', 'User manual, technical guide', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('61', 'Article for media/Magazine/Other (not peer-reviewed)', '44', 'Radio, TV, newspapers, newsletters, mazagines, etc', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('62', 'Outcome case study', '43', 'Outcome case study', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('63', 'Journal Article (peer reviewed)', '49', 'Peer-reviewed journal article from scientific journal', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('64', 'Book (peer reviewed)', '49', 'Peer-reviewed books', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('65', 'Book (non-peer reviewed)', '49', 'Non-peer reviewed books', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('66', 'Book chapter (peer reviewed)', '49', 'Peer-reviewed book chapters', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('67', 'Book chapter (non-peer reviewed)', '49', 'Non-peer-reviewed book chapters', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('68', 'Newsletter', '44', 'Newsletter', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('69', 'Social Media Output', '44', 'Wiki, linkedin group, facebook, yammer, etc.', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('70', 'Blog', '44', 'Blog (collection of posts)', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('71', 'Website', '44', 'Website', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('72', 'Presentation/Poster', '44', 'Presentation/Poster', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('73', 'Multimedia', '44', 'Video, audio, images', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('74', 'Maps/Geospatial data', '42', 'Geospatial data - geographic positioning information, CCAFS Sites Atlas, cropland, etc.', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('75', 'Brochure', '44', 'Brochure, Booklet', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('76', 'Outcome note', '43', 'Outcome note', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('77', 'Factsheet, Project Note', '44', 'Factsheet, project note, note', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('78', 'Infographic', '44', 'Infographic', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('79', 'Journal Article (non-peer reviewed)', '49', 'Non-peer reviewed journal article', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('80', 'Special issue', '49', 'Special issue', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('81', 'Policy workshop/Dialogue report', '43', 'Policy workshop report, dialogue report', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('82', 'Donor report', '43', 'Donor report, annual report, project paper, project report', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('83', 'Concept note', '43', 'Concept note', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('84', 'Governance, Administration & Management', null, null, '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('85', 'Management Meetings', '84', 'Management Meetings', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('86', 'Events', '84', 'Events', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('87', 'Governance report', '84', 'Authorizing plans, commitments and evaluation of the organization’s performance', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('88', 'Administration report', '84', 'Formulation of plans, framing policies and objectives. Finance reports.', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('89', 'Management report', '84', 'Putting plans and policies into actions.', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('90', 'Other Capacity', '46', 'Other Capacity', '1', '2017-06-07 14:53:49', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('91', 'Partnerships', null, null, '1', '2017-06-07 14:54:13', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('92', 'Partnership Agreements', '91', 'Formal agreements/research plan developed with MOA and/or firm resource agreements, between two entities, agreed upon mutual outputs and/or information products', '1', '2017-06-07 14:56:11', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('93', 'Reseach Plan Developed', '91', 'Research plan developed with partners involving firm resource commitments', '1', '2017-06-07 14:56:13', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('94', 'Agrobiodiversity and Genebank (Agricultural technologies)', null, null, '1', '2017-06-07 14:56:28', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('95', 'Varieties/Improved Lines/cultivars', '94', null, '1', '2017-06-07 14:56:47', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('96', 'Reference genomes', '94', null, '1', '2017-06-08 09:43:18', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('97', 'Traits/molecular markers strains', '94', null, '1', '2017-06-08 09:43:25', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('98', 'Tool Kit', '94', null, '1', '2017-06-08 09:43:35', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('99', 'Strategies', '94', null, '1', '2017-06-08 09:43:39', '3', '3', ' ');
INSERT INTO `deliverable_types` VALUES ('100', 'Agronomic Practices', '94', null, '1', '2017-06-08 09:43:44', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('101', 'Agro-industrial processes', '94', null, '1', '2017-06-08 09:43:53', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('102', 'Methods/Methodology/approaches/guidelines', '94', null, '1', '2017-06-08 09:43:59', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('103', 'Protocols', '94', null, '1', '2017-06-08 09:44:03', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('104', 'Agricultural Technologies', '94', null, '1', '2017-06-08 09:44:08', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('105', 'Enhanced Genetic Materials/Germplasm', '94', null, '1', '2017-06-08 09:44:17', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('106', 'New accessions', '94', null, '1', '2017-06-08 09:44:23', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('107', 'Genetic Resources inventories', '94', null, '1', '2017-06-07 15:00:36', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('108', 'Genetic Resources characterization ', '94', null, '1', '2017-06-07 15:00:51', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('109', 'Creation of collections', '94', null, '1', '2017-06-07 15:01:04', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('110', 'Preserved/ continued existence of genetic material', '94', null, '1', '2017-06-07 15:01:15', '3', '3', null);
INSERT INTO `deliverable_types` VALUES ('111', 'Genebank Management Procedures', '94', 'New management procedures that add value to the geneabank collection and its use', '1', '2017-06-07 15:02:30', '3', '3', null);
