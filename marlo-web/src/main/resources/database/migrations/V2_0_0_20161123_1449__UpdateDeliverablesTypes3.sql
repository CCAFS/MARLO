/*
Navicat MySQL Data Transfer

Source Server         : local
Source Server Version : 50617
Source Host           : localhost:3306
Source Database       : ccafs_marlo

Target Server Type    : MYSQL
Target Server Version : 50617
File Encoding         : 65001

Date: 2016-11-23 14:49:02
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `deliverable_types`
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
) ENGINE=InnoDB AUTO_INCREMENT=84 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of deliverable_types
-- ----------------------------
INSERT INTO `deliverable_types` VALUES ('42', 'Data, models and tools', null, null, null);
INSERT INTO `deliverable_types` VALUES ('43', 'Reports', null, null, null);
INSERT INTO `deliverable_types` VALUES ('44', 'Outreach products', null, null, null);
INSERT INTO `deliverable_types` VALUES ('46', 'Training materials', null, null, null);
INSERT INTO `deliverable_types` VALUES ('49', 'Articles and Books', null, null, null);
INSERT INTO `deliverable_types` VALUES ('51', 'Database/Dataset/Data documentation', '42', 'Dataset is a collection of data. Database is an organized collection of data. Data paper, dataset, database, repository', null);
INSERT INTO `deliverable_types` VALUES ('52', 'Data portal/Tool/Model code/Computer software', '42', 'Data Portals for dissemination, tools, model codes, computer software. search engine, games, algorithms.', null);
INSERT INTO `deliverable_types` VALUES ('53', 'Thesis', '43', 'Student thesis', null);
INSERT INTO `deliverable_types` VALUES ('54', 'Research workshop report', '43', 'Research workshop report, proceedings, workshop summary paper.', null);
INSERT INTO `deliverable_types` VALUES ('55', 'Policy brief/policy note/briefing paper', '44', 'Policy brief, policy note, briefing paper, 2020 synthesis, 2020 Vision focus, brief, policy paper, policy report, policy review, policy statement', null);
INSERT INTO `deliverable_types` VALUES ('56', 'Discussion paper/Working paper/White paper', '43', 'Discussion paper, policy working paper, progress report, research paper, research report, technical note, technical report, unpublished paper, white paper, working paper', null);
INSERT INTO `deliverable_types` VALUES ('57', 'Conference paper / Seminar paper', '43', 'Conference paper, seminar paper', null);
INSERT INTO `deliverable_types` VALUES ('58', 'Lecture/Training Course Material', '46', 'Lecture, training course material', null);
INSERT INTO `deliverable_types` VALUES ('59', 'Guidebook/Handbook/Good Practice Note', '46', 'Guidebook, handbook, good practice note', null);
INSERT INTO `deliverable_types` VALUES ('60', 'User manual/Technical Guide', '46', 'User manual, technical guide', null);
INSERT INTO `deliverable_types` VALUES ('61', 'Article for media/Magazine/Other (not peer-reviewed)', '46', 'Radio, TV, newspapers, newsletters, mazagines, etc', null);
INSERT INTO `deliverable_types` VALUES ('62', 'Outcome case study', '43', 'Outcome case study', null);
INSERT INTO `deliverable_types` VALUES ('63', 'Journal Article (peer reviewed)', '49', 'Peer-reviewed journal article from scientific journal', null);
INSERT INTO `deliverable_types` VALUES ('64', 'Book (peer reviewed)', '49', 'Peer-reviewed books', null);
INSERT INTO `deliverable_types` VALUES ('65', 'Book (non-peer reviewed)', '49', 'Non-peer reviewed books', null);
INSERT INTO `deliverable_types` VALUES ('66', 'Book chapter (peer reviewed)', '49', 'Peer-reviewed book chapters', null);
INSERT INTO `deliverable_types` VALUES ('67', 'Book chapter (non-peer reviewed)', '49', 'Non-peer-reviewed book chapters', null);
INSERT INTO `deliverable_types` VALUES ('68', 'Newsletter', '44', 'Newsletter', null);
INSERT INTO `deliverable_types` VALUES ('69', 'Social Media Output', '44', 'Wiki, linkedin group, facebook, yammer, etc.', null);
INSERT INTO `deliverable_types` VALUES ('70', 'Blog', '44', 'Blog (collection of posts)', null);
INSERT INTO `deliverable_types` VALUES ('71', 'Website', '44', 'Website', null);
INSERT INTO `deliverable_types` VALUES ('72', 'Presentation/Poster', '44', 'Presentation/Poster', null);
INSERT INTO `deliverable_types` VALUES ('73', 'Multimedia', '44', 'Video, audio, images', null);
INSERT INTO `deliverable_types` VALUES ('74', 'Maps/Geospatial data', '42', 'Geospatial data - geographic positioning information, CCAFS Sites Atlas, cropland, etc.', null);
INSERT INTO `deliverable_types` VALUES ('75', 'Brochure', '44', 'Brochure, Booklet', null);
INSERT INTO `deliverable_types` VALUES ('76', 'Outcome note', '44', 'Outcome note', null);
INSERT INTO `deliverable_types` VALUES ('77', 'Factsheet, Project Note', '44', 'Factsheet, project note, note', null);
INSERT INTO `deliverable_types` VALUES ('78', 'Infographic', '44', 'Infographic', null);
INSERT INTO `deliverable_types` VALUES ('79', 'Journal Article (non-peer reviewed)', '49', 'Non-peer reviewed journal article', null);
INSERT INTO `deliverable_types` VALUES ('80', 'Special issue', '49', 'Special issue', null);
INSERT INTO `deliverable_types` VALUES ('81', 'Policy workshop/Dialogue report', '43', 'Policy workshop report, dialogue report', null);
INSERT INTO `deliverable_types` VALUES ('82', 'Donor report', '43', 'Donor report, annual report, project paper, project report', null);
INSERT INTO `deliverable_types` VALUES ('83', 'Concept note', '43', 'Concept note', null);
