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
  `timeline` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `deliverable_types_parent_id_idx` (`parent_id`) USING BTREE,
  CONSTRAINT `deliverable_types_ibfk_1` FOREIGN KEY (`parent_id`) REFERENCES `deliverable_types` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of deliverable_types
-- ----------------------------
INSERT INTO `deliverable_types` VALUES ('1', 'Data and information outputs, including datasets, databases and models', null, null, null);
INSERT INTO `deliverable_types` VALUES ('2', 'Reports, Reference Materials and Other Papers', null, null, null);
INSERT INTO `deliverable_types` VALUES ('3', 'Peer reviewed Publications', null, null, null);
INSERT INTO `deliverable_types` VALUES ('4', 'Communication Products and Multimedia', null, null, null);
INSERT INTO `deliverable_types` VALUES ('7', 'Tools and Computer Software', null, null, null);
INSERT INTO `deliverable_types` VALUES ('8', 'Workshops', null, null, null);
INSERT INTO `deliverable_types` VALUES ('9', 'Capacity', null, null, '-1');
INSERT INTO `deliverable_types` VALUES ('10', 'Data', '1', 'Primary data / raw data (unprocessed data).', '12');
INSERT INTO `deliverable_types` VALUES ('11', 'Datasets', '1', 'Dataset is a collection of data.', '12');
INSERT INTO `deliverable_types` VALUES ('12', 'Databases', '1', 'Database is an organized collection of data.', '12');
INSERT INTO `deliverable_types` VALUES ('13', 'Information outputs', '1', null, '6');
INSERT INTO `deliverable_types` VALUES ('14', 'Research report', '2', 'Workshop report, consultant’s report, project reports, student thesis, portfolios, etc.', '3');
INSERT INTO `deliverable_types` VALUES ('15', 'Policy briefs - Briefing paper', '2', 'Policy brief, info note, etc.', '3');
INSERT INTO `deliverable_types` VALUES ('16', 'Working paper', '2', 'Working paper.', '3');
INSERT INTO `deliverable_types` VALUES ('17', 'Conference proceedings/papere', '2', 'Conference proceeding, conference paper.', '3');
INSERT INTO `deliverable_types` VALUES ('18', 'Seminar paper', '2', 'Seminar paper', '3');
INSERT INTO `deliverable_types` VALUES ('19', 'Discussion paper', '2', 'Discussion paper.', '3');
INSERT INTO `deliverable_types` VALUES ('20', 'Reference material', '2', 'Booklets and training manuals for extension agents, etc.', '3');
INSERT INTO `deliverable_types` VALUES ('21', 'Peer-reviewed journal articles', '3', 'Peer-reviewed journal article from scientific journal.', '6');
INSERT INTO `deliverable_types` VALUES ('22', 'Other non-peer reviewed articles', '2', 'Article in a magazin.', '3');
INSERT INTO `deliverable_types` VALUES ('23', 'Books', '3', 'Peer-reviewed book.', '6');
INSERT INTO `deliverable_types` VALUES ('24', 'Book chapters', '3', 'Peer-reviewed book chapter.', '6');
INSERT INTO `deliverable_types` VALUES ('26', 'Articles for media or news', '4', 'Radio, TV, newspapers, newsletters, etc.', '0');
INSERT INTO `deliverable_types` VALUES ('27', 'Social media outputs', '4', 'Web site, blog, wiki, linkedin group, facebook, yammer, etc.', '0');
INSERT INTO `deliverable_types` VALUES ('28', 'Poster', '4', 'Poster.', '0');
INSERT INTO `deliverable_types` VALUES ('29', 'Presentations', '4', 'Presentation.', '0');
INSERT INTO `deliverable_types` VALUES ('30', 'Case Study', '2', 'Case study, outcome case, etc.', '0');
INSERT INTO `deliverable_types` VALUES ('31', 'Video', '4', 'Audio.', '3');
INSERT INTO `deliverable_types` VALUES ('32', 'Audio', '4', 'Video.', '3');
INSERT INTO `deliverable_types` VALUES ('33', 'Images', '4', 'Photo, Picture, etc.', '3');
INSERT INTO `deliverable_types` VALUES ('34', 'Platforms', '7', 'Data Portals for dissemination.', '0');
INSERT INTO `deliverable_types` VALUES ('35', 'Maps', '7', 'CCAFS Sites Atlas, cropland, etc.', '0');
INSERT INTO `deliverable_types` VALUES ('36', 'Tools', '7', 'Search engine, game, etc.', '0');
INSERT INTO `deliverable_types` VALUES ('37', 'Models', '1', 'i.e. Crop models', '12');
INSERT INTO `deliverable_types` VALUES ('38', 'Other', '7', 'Algorithms', '0');
INSERT INTO `deliverable_types` VALUES ('39', 'Workshop', '8', null, null);
INSERT INTO `deliverable_types` VALUES ('40', 'Capacity', '9', null, null);
INSERT INTO `deliverable_types` VALUES ('41', 'Website', '7', 'Website, blog, etc.', '0');
