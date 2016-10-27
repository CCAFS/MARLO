/*
Navicat MySQL Data Transfer

Source Server         : local
Source Server Version : 50617
Source Host           : localhost:3306
Source Database       : ccafs_marlo

Target Server Type    : MYSQL
Target Server Version : 50617
File Encoding         : 65001

Date: 2016-10-27 16:40:47
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `funding_source_budgets`
-- ----------------------------
DROP TABLE IF EXISTS `funding_source_budgets`;
CREATE TABLE `funding_source_budgets` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `funding_source_id` bigint(20) DEFAULT NULL,
  `budget` double(20,0) DEFAULT NULL,
  `year` int(11) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `active_since` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified_by` bigint(20) NOT NULL,
  `modification_justification` text NOT NULL,
  PRIMARY KEY (`id`),
  KEY `funding_source_id` (`funding_source_id`),
  KEY `created_by` (`created_by`),
  KEY `modified_by` (`modified_by`),
  CONSTRAINT `funding_source_budgets_ibfk_1` FOREIGN KEY (`funding_source_id`) REFERENCES `funding_sources` (`id`),
  CONSTRAINT `funding_source_budgets_ibfk_2` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `funding_source_budgets_ibfk_3` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=173 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of funding_source_budgets
-- ----------------------------
INSERT INTO `funding_source_budgets` VALUES ('1', '248', '0', '2011', '1', '3', '2016-10-27 15:41:56', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('2', '248', '229663', '2012', '1', '3', '2016-10-27 15:41:56', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('3', '248', '209516', '2013', '1', '3', '2016-10-27 15:41:56', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('4', '248', '3925956', '2014', '1', '3', '2016-10-27 15:41:56', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('5', '248', '45782694', '2015', '1', '3', '2016-10-27 15:41:56', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('6', '248', '27459455', '2016', '1', '3', '2016-10-27 15:41:56', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('7', '248', '17624273', '2017', '1', '3', '2016-10-27 15:41:56', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('8', '248', '14272635', '2018', '1', '3', '2016-10-27 15:41:56', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('9', '248', '0', '2019', '1', '3', '2016-10-27 15:41:56', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('46', '105', '1500000', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('47', '105', '1500050', '2017', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('48', '105', '1500000', '2018', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('49', '131', '620863', '2015', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('50', '131', '1028455', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('51', '134', '376542', '2015', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('52', '134', '259539', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('53', '134', '363919', '2017', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('54', '136', '557389', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('55', '136', '727632', '2017', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('56', '137', '365000', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('57', '137', '415000', '2017', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('58', '138', '91768', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('59', '140', '300000', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('60', '141', '300000', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('61', '142', '250000', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('62', '145', '502000', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('63', '146', '625000', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('64', '147', '50000', '2015', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('65', '147', '50000', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('66', '147', '50000', '2017', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('67', '147', '50000', '2018', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('68', '148', '60000', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('69', '149', '200000', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('70', '151', '270000', '2015', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('71', '151', '410000', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('72', '151', '410000', '2017', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('73', '152', '250000', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('74', '152', '250000', '2018', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('75', '154', '187000', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('76', '156', '228308', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('77', '157', '550000', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('78', '158', '50000', '2015', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('79', '158', '140000', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('80', '159', '25000', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('81', '159', '25000', '2017', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('82', '161', '550', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('83', '162', '75000', '2014', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('84', '162', '25000', '2015', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('85', '164', '61798', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('86', '166', '539278', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('87', '166', '539278', '2017', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('88', '167', '800000', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('89', '168', '120000', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('90', '170', '493000', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('91', '172', '80000', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('92', '174', '200000', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('93', '175', '620301', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('94', '176', '60000', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('95', '176', '15000', '2017', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('96', '177', '159102', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('97', '177', '120590', '2017', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('98', '178', '15000', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('99', '180', '900000', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('100', '180', '900000', '2017', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('101', '181', '6800', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('102', '182', '50000', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('103', '184', '188400', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('104', '185', '200000', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('105', '185', '400000', '2017', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('106', '185', '400000', '2018', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('107', '187', '24835', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('108', '188', '150000', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('109', '189', '143148', '2015', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('110', '189', '367916', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('111', '189', '288762', '2017', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('112', '189', '284677', '2018', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('113', '190', '1000000', '2015', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('114', '190', '1500000', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('115', '190', '500000', '2017', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('116', '191', '475206', '2015', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('117', '191', '499777', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('118', '191', '524771', '2017', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('119', '192', '60000', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('120', '194', '69127', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('121', '195', '21774', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('122', '198', '40000', '2015', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('123', '198', '175000', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('124', '198', '150000', '2017', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('125', '199', '70000', '2015', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('126', '199', '120000', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('127', '199', '45000', '2017', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('128', '201', '517477', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('129', '201', '419427', '2017', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('130', '202', '550000', '2015', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('131', '202', '480000', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('132', '202', '380000', '2017', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('133', '203', '255000', '2015', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('134', '204', '773000', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('135', '204', '604000', '2017', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('136', '205', '200000', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('137', '205', '200000', '2017', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('138', '206', '101804', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('139', '207', '174000', '2015', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('140', '207', '650000', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('141', '211', '272985', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('142', '212', '220000', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('143', '213', '43000', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('144', '217', '360000', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('145', '220', '10000', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('146', '221', '280207', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('147', '222', '190000', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('148', '223', '251000', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('149', '224', '19213', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('150', '225', '500000', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('151', '226', '1000000', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('152', '227', '250000', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('153', '228', '159186', '2015', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('154', '228', '140810', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('155', '229', '336089', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('156', '232', '400000', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('157', '233', '1728000', '2015', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('158', '233', '1728000', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('159', '234', '400000', '2015', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('160', '234', '400000', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('161', '235', '150000', '2015', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('162', '235', '150000', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('163', '237', '61000', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('164', '238', '138000', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('165', '239', '200000', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('166', '240', '253813', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('167', '241', '91000', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('168', '242', '226291', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('169', '244', '1000000', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('170', '245', '300000', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('171', '246', '230000', '2016', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');
INSERT INTO `funding_source_budgets` VALUES ('172', '247', '490000', '2015', '1', '3', '2016-10-27 16:40:13', '3', 'Creating Funding Sources');

-- ----------------------------
-- Table structure for `funding_sources`
-- ----------------------------
DROP TABLE IF EXISTS `funding_sources`;
CREATE TABLE `funding_sources` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` text,
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `finance_code` text,
  `contact_person_name` text,
  `contact_person_email` text,
  `donor` bigint(20) DEFAULT NULL,
  `center_type` int(11) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `active_since` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified_by` bigint(20) NOT NULL,
  `modification_justification` text NOT NULL,
  PRIMARY KEY (`id`),
  KEY `donor` (`donor`),
  KEY `created_by` (`created_by`),
  KEY `modified_by` (`modified_by`),
  CONSTRAINT `funding_sources_ibfk_4` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`),
  CONSTRAINT `funding_sources_ibfk_2` FOREIGN KEY (`donor`) REFERENCES `institutions` (`id`),
  CONSTRAINT `funding_sources_ibfk_3` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=316 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of funding_sources
-- ----------------------------
INSERT INTO `funding_sources` VALUES ('105', 'Climate Services for Agriculture: Empowering Farmers to Manage Risk and Adapt to a Changing Climate in Rwanda', '2015-01-01', '2018-12-31', null, null, null, null, '0', '2', '1', '3', '2016-10-20 09:05:50', '844', 'Lessons.');
INSERT INTO `funding_sources` VALUES ('106', 'Climate Services for Africa', '2015-01-01', '2016-12-31', null, null, null, null, '0', '2', '1', '3', '2014-09-19 20:08:10', '29', 'Added regions');
INSERT INTO `funding_sources` VALUES ('107', 'Global Framework for Climate Services: Tanzania and Malawi', '2014-01-01', '2016-12-31', null, null, null, null, '0', '2', '1', '3', '2014-09-19 20:11:05', '29', 'Uploaded CCAFS annual report to WMO.');
INSERT INTO `funding_sources` VALUES ('128', 'Small farmer adaptation and mitigation to climate change in Africa:  Enhancing small farmer incomes and productivity/implementation phase.', '2012-01-01', '2015-06-30', null, null, null, null, '0', '2', '1', '3', '2014-10-07 14:41:04', '241', 'no edits made');
INSERT INTO `funding_sources` VALUES ('131', 'UVM CIAT Reducing and Accounting for Agriculture-Driven GHG Emissions in USAID\'s Agriculture Related Work', '2015-01-01', '2016-12-31', null, null, null, null, '0', '3', '1', '3', '2014-11-11 15:01:47', '31', 'minor edit');
INSERT INTO `funding_sources` VALUES ('134', 'Crowdsourcing Crop Improvement: Evidence base and outscaling model', '2015-03-01', '2018-02-28', null, null, null, null, '0', '3', '1', '3', '2014-09-12 02:32:24', '844', 'Change file');
INSERT INTO `funding_sources` VALUES ('136', 'Mainstreaming agrobiodiversity conservation and utilization in agricultural sector to ensure \r\n ecosystem services and reduce vulnerability', '2015-07-01', '2018-12-31', null, null, null, null, '0', '3', '1', '3', '2014-09-28 01:58:51', '844', 'Change file');
INSERT INTO `funding_sources` VALUES ('137', 'Linking crop index insurance schemes to bilateral research projects that are developing and generating climate-adapted maize germplasm in Sub-Saharan Africa', '2015-01-01', '2018-01-31', null, null, null, null, '0', '3', '1', '3', '2014-09-28 18:25:48', '100', 'Added documents linking index insurance to climate-adapted maize germplasm developed by DTMA project');
INSERT INTO `funding_sources` VALUES ('138', 'Linking agrobiodiversity value chains, climate adaptation and nutrition: empowering the poor to manage risk', '2015-01-01', '2018-12-31', null, null, null, null, '0', '2', '1', '3', '2014-09-29 04:08:35', '206', 'Details of villages will be reported next year');
INSERT INTO `funding_sources` VALUES ('140', 'Integrated Agricultural Production and Food Security Forecasting System for East Africa', '2015-01-01', '2018-01-31', null, null, null, null, '0', '3', '1', '3', '2014-10-04 11:26:34', '844', 'Change');
INSERT INTO `funding_sources` VALUES ('141', 'Policy Action for Sustainable Intensification of Ugandan Cropping Systems', '2014-03-01', '2017-12-31', null, null, null, null, '0', '3', '1', '3', '2014-10-06 05:30:27', '271', 'Added new info');
INSERT INTO `funding_sources` VALUES ('142', 'Trade-offs and synergies in climate change adaptation and mitigation in coffee and cocoa systems', '2013-06-01', '2016-04-01', null, null, null, null, '0', '3', '1', '3', '2014-10-06 06:17:17', '271', 'Added info');
INSERT INTO `funding_sources` VALUES ('145', 'Enhancement of modeling tools (IMPACT), to handle variability and land-use, for improved analysis of climate change impacts.', '2015-01-01', '2016-12-31', null, null, null, null, '0', '3', '1', '3', '2014-10-07 21:34:50', '52', '.');
INSERT INTO `funding_sources` VALUES ('146', 'USAID-Climate Smart Agriculture (CSA) Strategic Support for Feed the Future Stakeholders and National Institutions', '2015-01-01', '2016-12-31', null, null, null, null, '0', '3', '1', '3', '2014-10-08 19:56:10', '69', 'Edited');
INSERT INTO `funding_sources` VALUES ('147', 'Assessing the implications of private commitments in land use change trajectories and ranching intensification', '2015-01-01', '2018-12-31', null, null, null, null, '0', '3', '1', '3', '2014-10-08 20:42:31', '62', 'Project document has been updated');
INSERT INTO `funding_sources` VALUES ('148', 'Convenio MADR - Project with the Colombian Ministry of Agriculture on Climate Change and Agricultre', '2014-03-01', '2016-12-31', null, null, null, null, '0', '3', '1', '3', '2014-10-09 06:45:15', '69', 'contract added');
INSERT INTO `funding_sources` VALUES ('149', 'Remote Sensing as a Monitoring Tool for Smallholder\'s Cropping Area Determination', '2014-03-01', '2016-07-31', null, null, null, null, '0', '3', '1', '3', '2014-10-09 07:03:27', '1', 'Adjusting end date to July 31th, 2016 as requested by Roberto Quiroz.');
INSERT INTO `funding_sources` VALUES ('151', 'Scaling of climate smart practices that reduce GHG emissions.', '2015-01-01', '2018-01-31', null, null, null, null, '0', '3', '1', '3', '2014-10-09 11:56:19', '844', '');
INSERT INTO `funding_sources` VALUES ('152', 'Objective 4. SIMLESA II Project:  To support the development of local and regional innovations systems and scaling out modalities', '2015-01-01', '2018-06-30', null, null, null, null, '0', '3', '1', '3', '2014-10-09 15:49:47', '95', 'Sites added');
INSERT INTO `funding_sources` VALUES ('154', 'Sustainable development options to enhance climate change mitigation and adaptation capacities in Colombian and the Peruvian Amazon.', '2015-01-01', '2017-12-31', null, null, null, null, '0', '3', '1', '3', '2014-10-10 04:30:15', '69', 'Added Proposal');
INSERT INTO `funding_sources` VALUES ('156', 'Enhancing climate-resilience of Agricultural Livelihoods', '2014-03-01', '2017-10-31', null, null, null, null, '0', '3', '1', '3', '2014-10-10 13:06:59', '271', 'Attached file');
INSERT INTO `funding_sources` VALUES ('157', 'Increasing food security and farming system resilience in East Africa through wide-scale adoption of climate-smart agricultural practices', '2014-01-01', '2016-12-31', null, null, null, null, '0', '3', '1', '3', '2014-10-10 13:48:27', '69', 'Proposal Added');
INSERT INTO `funding_sources` VALUES ('158', 'CSI India: Enhancing farmers’ adaptive capacity by developing CSI - India Food Security Portal', '2015-01-01', '2016-01-31', null, null, null, null, '0', '3', '1', '3', '2014-10-10 21:44:13', '98', 'Region and Flagship have been added');
INSERT INTO `funding_sources` VALUES ('159', 'Climate change analyses to support participatory investment plans in coffee-based landscapes', '2015-01-01', '2018-12-31', null, null, null, null, '0', '3', '1', '3', '2014-10-10 23:41:31', '843', 'Uploading report as per Deissy request');
INSERT INTO `funding_sources` VALUES ('161', 'Grand Challenges Explorations Round 12: Less is More: The 5Q Approach', '2014-03-01', '2015-12-30', null, null, null, null, '0', '3', '1', '3', '2014-10-12 12:42:05', '66', '');
INSERT INTO `funding_sources` VALUES ('162', 'Increasing food security and farming systems for resilience in East Africa through wide-scale adoption of climate smart agriculture practices', '2014-03-01', '2016-12-31', null, null, null, null, '0', '2', '1', '3', '2014-10-12 14:23:43', '3', 'Adding Flagship and Region as per Todd\'s request.');
INSERT INTO `funding_sources` VALUES ('164', 'Climate change, food security and policy reform in India.', '2014-04-01', '2016-03-31', null, null, null, null, '0', '3', '1', '3', '2014-11-11 21:09:56', '241', 'no significant edits made');
INSERT INTO `funding_sources` VALUES ('166', 'USAID- Unlocking Private Sector Engagement and Creating a Learning Community in Climate Smart Agriculture', '2016-01-01', '2017-12-31', null, null, null, null, '0', '3', '1', '3', '2015-10-23 21:17:25', '55', '');
INSERT INTO `funding_sources` VALUES ('167', 'Strengthening national capacities to implement the International Treaty of PGRFA: Genetic Resources Policy Initiative (GRPI) Phase II (DGIS)', '2015-12-31', '2016-12-31', null, null, null, null, '0', '3', '1', '3', '2015-10-23 22:25:00', '243', '');
INSERT INTO `funding_sources` VALUES ('168', 'Mitigation Options to Reduce Methane Emissions in Paddy Rice', '2016-01-01', '2016-05-31', null, null, null, null, '0', '3', '1', '3', '2015-10-26 03:49:10', '108', '');
INSERT INTO `funding_sources` VALUES ('170', 'Climate-smart Agriculture in Rice-based Systems of Vietnamese Deltas: Technologies, Knowledge Products and Decision Tools', '2016-04-01', '2018-12-31', null, null, null, null, '0', '3', '1', '3', '2015-10-26 08:25:27', '108', '');
INSERT INTO `funding_sources` VALUES ('172', 'Salinity Advisory as a Location-specific Timely Service for Rice farmers (SALTS)', '2016-01-01', '2017-02-28', null, null, null, null, '0', '3', '1', '3', '2015-10-26 09:17:18', '108', '');
INSERT INTO `funding_sources` VALUES ('174', 'Scalable straw management options for sustainability and low environmental footprint in rice-based production systems', '2016-01-01', '2016-12-31', null, null, null, null, '0', '3', '1', '3', '2015-10-26 09:27:27', '108', '');
INSERT INTO `funding_sources` VALUES ('175', 'Remote Sensing-based Information and Insurance for Crops in Emerging Economies - Phase II (RIICE-Ph2)', '2016-01-01', '2016-12-31', null, null, null, null, '0', '3', '1', '3', '2015-10-26 09:32:51', '108', '');
INSERT INTO `funding_sources` VALUES ('176', 'Climate Resilient Planning: Interdisciplinary Research to Improve Information Provision for Decision Making', '2016-01-01', '2017-03-31', null, null, null, null, '0', '3', '1', '3', '2015-10-26 12:46:29', '61', '');
INSERT INTO `funding_sources` VALUES ('177', 'Mutually supportive implementation of the Nagoya Protocol and Plant Treaty (Darwin Initiative)', '2016-01-01', '2018-03-31', null, null, null, null, '0', '3', '1', '3', '2015-10-26 13:04:33', '243', '');
INSERT INTO `funding_sources` VALUES ('178', 'Bioversity GRDC Partnership in Vavilov-Frankel Fellowships', '2011-07-01', '2016-06-30', null, null, null, null, '0', '3', '1', '3', '2015-10-26 13:21:34', '243', '');
INSERT INTO `funding_sources` VALUES ('180', 'Use and conservation of agrobiodiversity for increased agricultural sustainability, smallholder wellbeing and resilience to climate change in India', '2016-01-01', '2018-03-31', null, null, null, null, '0', '3', '1', '3', '2015-10-26 14:17:02', '243', '');
INSERT INTO `funding_sources` VALUES ('181', 'Integrated Seed Sector Development - Africa - ‘Global Policies and National Realities’', '2016-01-01', '2016-08-31', null, null, null, null, '0', '3', '1', '3', '2015-10-26 14:32:45', '243', '');
INSERT INTO `funding_sources` VALUES ('182', 'Standard assessment of mitigation potential and livelihoods in smallholder systems (SAMPLES)', '2016-01-01', '2016-06-30', null, null, null, null, '0', '3', '1', '3', '2015-10-28 08:04:08', '108', '');
INSERT INTO `funding_sources` VALUES ('184', 'Farmers’ seed systems and community seed banks in South Africa: a baseline study of selected sites', '2015-12-31', '2016-12-31', null, null, null, null, '0', '3', '1', '3', '2015-10-28 09:13:56', '243', '');
INSERT INTO `funding_sources` VALUES ('185', 'Reducing Methane Emissions from Rice Production in Vietnam and Bangladesh', '2016-07-01', '2018-12-31', null, null, null, null, '0', '3', '1', '3', '2015-10-29 06:59:14', '108', '');
INSERT INTO `funding_sources` VALUES ('187', 'Greenhouse gas mitigation in irrigated rice systems in Asia Part 2 (MIRSA 2)', '2016-04-01', '2018-02-28', null, null, null, null, '0', '3', '1', '3', '2015-10-30 06:42:56', '108', '');
INSERT INTO `funding_sources` VALUES ('188', 'Developing climate smart districts in Uganda', '2016-01-01', '2017-01-01', null, null, null, null, '0', '3', '1', '3', '2015-10-30 09:42:12', '271', '');
INSERT INTO `funding_sources` VALUES ('189', 'Enhancing adaptive capacity of women and ethnic minority smallholder farmers through improved agro-climate information in Laos', '2015-11-01', '2018-12-31', null, null, null, null, '0', '3', '1', '3', '2015-11-02 01:45:16', '872', '');
INSERT INTO `funding_sources` VALUES ('190', 'The Agricultural Modeling Intercomparison and Improvement project (AgMIP) - Regional Integrated Assessments in SSA and SA', '2015-01-01', '2017-03-31', null, null, null, null, '0', '3', '1', '3', '2015-11-04 10:30:31', '162', '');
INSERT INTO `funding_sources` VALUES ('191', 'Local Governance and Adapting to Climate Change in Sub-Saharan Africa (LGACC)', '2015-01-01', '2017-12-21', null, null, null, null, '0', '2', '1', '3', '2015-11-04 11:43:06', '915', '');
INSERT INTO `funding_sources` VALUES ('192', 'WD - Big Data for Climate Smart Agriculture Enhancing & Sustaining Rice Systems for Latin America and the World.', '2016-01-01', '2016-06-30', null, null, null, null, '0', '3', '1', '3', '2015-11-04 16:15:35', '69', '');
INSERT INTO `funding_sources` VALUES ('194', 'MAFF(Japan) Environmental Protection using Traits Associated with Biological Nitrification Inhibition', '2015-01-01', '2019-05-31', null, null, null, null, '0', '3', '1', '3', '2015-11-04 17:10:14', '69', '');
INSERT INTO `funding_sources` VALUES ('195', 'JIRCAS - Quantifying the BNI-residual effect from B. humidicola on N-recovery and N-use efficiencyof the subsequent annual crops', '2016-01-01', '2016-03-31', null, null, null, null, '0', '3', '1', '3', '2015-11-04 17:16:04', '69', '');
INSERT INTO `funding_sources` VALUES ('198', 'Surveillance of Climate-smart Agriculture for Nutrition (SCAN)', '2015-09-01', '2017-09-30', null, null, null, null, '0', '3', '1', '3', '2015-11-04 22:24:42', '73', '');
INSERT INTO `funding_sources` VALUES ('199', 'Tanzania Climate Smart Agriculture Reference/Learning Sites (ICRAF-USDA)', '2015-09-16', '2017-03-15', null, null, null, null, '0', '3', '1', '3', '2015-11-04 23:45:19', '73', '');
INSERT INTO `funding_sources` VALUES ('201', 'Strengthening cultivar diversity in Ethiopian seed systems to manage climate related risks and foster nutrition', '2016-01-01', '2019-12-31', null, null, null, null, '0', '3', '1', '3', '2015-11-05 08:33:09', '243', '');
INSERT INTO `funding_sources` VALUES ('202', 'In situ assessment of GHG emissions from two livestock systems in East Africa', '2015-02-16', '2017-08-28', null, null, null, null, '0', '3', '1', '3', '2015-11-05 09:00:52', '86', '');
INSERT INTO `funding_sources` VALUES ('203', 'Climate and Clean Air Coalition (CCAC) - manure management', '2015-01-01', '2015-12-31', null, null, null, null, '0', '3', '1', '3', '2015-11-05 09:15:42', '910', '');
INSERT INTO `funding_sources` VALUES ('204', 'Greening Livestock:  Incentive-based interventions for reducing the climate impact of livestock in East Africa', '2016-01-29', '2017-11-30', null, null, null, null, '0', '3', '1', '3', '2015-11-05 09:21:24', '82', '');
INSERT INTO `funding_sources` VALUES ('205', 'Promoting opensource seedsystems for beans, forage legumes, millet & sorghum for climate change adaptation in Kenya, Tanzania and Uganda', '2016-01-01', '2019-12-31', null, null, null, null, '0', '3', '1', '3', '2015-11-05 14:15:35', '243', '');
INSERT INTO `funding_sources` VALUES ('206', '(CORPOICA) - Biological Nitrification Nnhibition (BNI) in pastures to help agriculture intensification and climate change mitigation', '2016-01-01', '2016-07-31', null, null, null, null, '0', '3', '1', '3', '2015-11-05 21:11:54', '69', '');
INSERT INTO `funding_sources` VALUES ('207', 'Stimulating technology cooperation and enhancing the development and transfer of climate technologies (CTCN - Climate Technology Centre & Network)', '2015-03-27', '2016-03-31', null, null, null, null, '0', '2', '1', '3', '2015-11-10 07:26:37', '158', '');
INSERT INTO `funding_sources` VALUES ('209', 'LIVEGaps Project', '2016-01-01', '2016-12-31', null, null, null, null, '0', '2', '1', '3', '2015-11-10 11:26:10', '82', '');
INSERT INTO `funding_sources` VALUES ('211', '(UNI-CAUCA) - Plan Nacional de Regalias', '2015-01-01', '2018-02-28', null, null, null, null, '0', '3', '1', '3', '2015-11-10 20:58:44', '69', '');
INSERT INTO `funding_sources` VALUES ('212', 'African Monsoon Multidisciplinary Analysis (AMMA-2050)', '2015-06-01', '2019-05-31', null, null, null, null, '0', '2', '1', '3', '2015-11-11 11:58:26', '17', '');
INSERT INTO `funding_sources` VALUES ('213', '(Cross CRP 3.7) - Quantifying enteric methane emissions from cattle grazing on improved forages', '2015-01-01', '2016-04-01', null, null, null, null, '0', '3', '1', '3', '2015-11-11 13:52:35', '69', '');
INSERT INTO `funding_sources` VALUES ('217', 'Fish in national development: contrasting case studies in the Indo-Pacific Region (FIS/2015/031)', '2015-10-10', '2017-09-30', null, null, null, null, '0', '3', '1', '3', '2015-11-12 01:40:14', '153', '');
INSERT INTO `funding_sources` VALUES ('220', 'Classical biological control of the papaya mealybug a new invasive and highly polyphagous pest spreading throughout West and Central Africa', '2016-01-01', '2017-01-01', null, null, null, null, '0', '2', '1', '3', '2015-11-12 17:49:28', '271', '');
INSERT INTO `funding_sources` VALUES ('221', 'N2Africa Phase II - Putting nitrogen fixation to work for smallholder farmers in Africa', '2015-01-01', '2016-11-30', null, null, null, null, '0', '2', '1', '3', '2015-11-12 18:47:48', '271', '');
INSERT INTO `funding_sources` VALUES ('222', 'Production and use of biochar, compost and lime as component of integrated soil fertility management and sustainable land use', '2015-09-01', '2018-08-01', null, null, null, null, '0', '2', '1', '3', '2015-11-12 18:47:50', '271', '');
INSERT INTO `funding_sources` VALUES ('223', 'The development and expansion of sustainable agriculture activities in the periphery south of Faunal Reserve of Lomako Yokokala', '2015-12-15', '2016-12-30', null, null, null, null, '0', '2', '1', '3', '2015-11-12 18:47:51', '271', '');
INSERT INTO `funding_sources` VALUES ('224', 'FEED THE FUTURE INNOVATION LAB. FOR CLIMATE RESILIENT COWPEA', '2015-09-01', '2018-09-11', null, null, null, null, '0', '2', '1', '3', '2015-11-12 18:47:52', '271', '');
INSERT INTO `funding_sources` VALUES ('225', 'CORMACARENA', '2015-11-13', '2016-12-31', null, null, null, null, '0', '3', '1', '3', '2015-11-13 04:23:54', '69', '');
INSERT INTO `funding_sources` VALUES ('226', 'CVC', '2015-11-13', '2016-11-30', null, null, null, null, '0', '3', '1', '3', '2015-11-13 04:29:50', '69', '');
INSERT INTO `funding_sources` VALUES ('227', 'Pragmatic economic valuation of adaptation risk and responses across scales', '2015-01-01', '2016-12-31', null, null, null, null, '0', '3', '1', '3', '2015-11-13 04:31:54', '66', '');
INSERT INTO `funding_sources` VALUES ('228', 'Accelerating Adoption of Agroforestry in Western Kenya (TripleA Project)', '2015-01-01', '2016-12-31', null, null, null, null, '0', '2', '1', '3', '2015-11-13 09:13:14', '158', '');
INSERT INTO `funding_sources` VALUES ('229', 'Building Biocarbon and Rural Development in West Africa (BIODEV)', '2015-01-01', '2016-08-08', null, null, null, null, '0', '2', '1', '3', '2015-11-13 09:20:01', '158', '');
INSERT INTO `funding_sources` VALUES ('232', 'Agricultural Risk Policy and Climate Change', '2016-01-01', '2016-12-31', null, null, null, null, '0', '3', '1', '3', '2015-11-13 17:53:47', '52', '');
INSERT INTO `funding_sources` VALUES ('233', 'ACIAR-SRFSI (Sustainable and resilient farming systems intensification for eastern Gangetic plains)-CIMMYT', '2015-01-01', '2018-06-30', null, null, null, null, '0', '3', '1', '3', '2015-11-17 10:39:34', '1', '');
INSERT INTO `funding_sources` VALUES ('234', 'Cereal Systems Initiative for South Asia (CSISA)-CIMMYT', '2015-01-01', '2019-11-30', null, null, null, null, '0', '3', '1', '3', '2015-11-18 09:55:53', '89', '');
INSERT INTO `funding_sources` VALUES ('235', 'ICAR-Conservation Agriculture-CIMMYT', '2015-01-01', '2016-12-31', null, null, null, null, '0', '3', '1', '3', '2015-11-18 09:59:31', '89', '');
INSERT INTO `funding_sources` VALUES ('237', 'Facilitation of ICON Phase-2 Field Experiment', '2015-01-01', '2017-06-30', null, null, null, null, '0', '3', '1', '3', '2015-11-19 08:08:06', '108', '');
INSERT INTO `funding_sources` VALUES ('238', 'Funding position of Decision Support Expert for Climate Change at IRRI', '2015-01-01', '2016-12-31', null, null, null, null, '0', '3', '1', '3', '2015-11-19 09:48:41', '108', '');
INSERT INTO `funding_sources` VALUES ('239', 'Innovation platform for improving farmers’ adoption of Climate Smart Agriculture technologies: piloting in Honduras and Colombia', '2015-06-01', '2018-05-31', null, null, null, null, '0', '3', '1', '3', '2015-11-20 02:00:56', '987', '');
INSERT INTO `funding_sources` VALUES ('240', 'Improved Solutions for Management of Floods and Droughts in South Asia', '2015-06-01', '2018-07-31', null, null, null, null, '0', '3', '1', '3', '2015-11-20 03:50:18', '275', '');
INSERT INTO `funding_sources` VALUES ('241', 'Drought Monitoring in South Asia', '2015-07-01', '2016-04-30', null, null, null, null, '0', '3', '1', '3', '2015-11-20 04:05:49', '275', '');
INSERT INTO `funding_sources` VALUES ('242', 'Innovative Insurance Solutions for Agricultural Risk and their benefits to Climate Change Adaptation for Resilient Communities in two pilot regions', '2016-01-01', '2019-12-31', null, null, null, null, '0', '3', '1', '3', '2015-11-20 04:21:03', '275', '');
INSERT INTO `funding_sources` VALUES ('244', 'Site-and climate- specific agriculture recommendations across time-scales USAID', '2016-02-01', '2017-02-01', null, null, null, null, '0', '3', '1', '3', '2015-11-26 19:02:48', '69', '');
INSERT INTO `funding_sources` VALUES ('245', '(USDA-BAA) Identifying Opportunities for Action on Private Sector Engagement in Climate-Smart Agriculture', '2016-02-01', '2018-02-28', null, null, null, null, '0', '3', '1', '3', '2015-11-26 22:13:34', '69', '');
INSERT INTO `funding_sources` VALUES ('246', 'Science support to the Sustainable Cocoa Production Platform (SCPP)', '2016-02-01', '2016-12-30', null, null, null, null, '0', '3', '1', '3', '2015-11-26 22:30:16', '69', '');
INSERT INTO `funding_sources` VALUES ('247', '(BILATERAL-USAID) CSA Integration and Analysis: Strategic support on Climate Smart Agriculture in Feed the Future', '2015-01-01', '2015-12-31', null, null, null, null, '0', '3', '1', '3', '2016-02-23 20:19:18', '17', '');
INSERT INTO `funding_sources` VALUES ('248', 'CCAFS W1/W2', '2016-01-01', '2022-12-31', null, null, null, null, '0', '1', '1', '3', '2016-10-27 14:07:21', '3', '');
INSERT INTO `funding_sources` VALUES ('250', 'PIM W1/W2', '2016-01-01', '2022-12-31', null, null, null, null, '0', '1', '1', '3', '2016-10-27 14:08:41', '3', '');
INSERT INTO `funding_sources` VALUES ('251', 'WLE W1/W2', '2016-01-01', '2022-12-31', null, null, null, null, '0', '1', '1', '3', '2016-10-27 14:08:41', '3', '');
INSERT INTO `funding_sources` VALUES ('252', 'A4NH W1/W2', '2016-01-01', '2022-12-31', null, null, null, null, '0', '1', '1', '3', '2016-10-27 14:08:41', '3', '');
INSERT INTO `funding_sources` VALUES ('253', 'Livestock W1/W2', '2016-01-01', '2022-12-31', null, null, null, null, '0', '1', '1', '3', '2016-10-27 14:08:41', '3', '');
INSERT INTO `funding_sources` VALUES ('285', 'IRRI-International Rice Research Institute', '2016-01-01', '2022-12-31', null, null, null, '5', '0', '4', '1', '3', '2016-10-27 14:29:01', '3', '');
INSERT INTO `funding_sources` VALUES ('286', 'IITA-International Institute of Tropical Agriculture', '2016-01-01', '2022-12-31', null, null, null, '45', '0', '4', '1', '3', '2016-10-27 14:29:01', '3', '');
INSERT INTO `funding_sources` VALUES ('287', 'CIAT-Centro Internacional de Agricultura Tropical', '2016-01-01', '2022-12-31', null, null, null, '46', '0', '4', '1', '3', '2016-10-27 14:29:01', '3', '');
INSERT INTO `funding_sources` VALUES ('288', 'BIOVERSITY-Bioversity International', '2016-01-01', '2022-12-31', null, null, null, '49', '0', '4', '1', '3', '2016-10-27 14:29:01', '3', '');
INSERT INTO `funding_sources` VALUES ('289', 'CIMMYT-Centro Internacional de Mejoramiento de Maíz y Trigo', '2016-01-01', '2022-12-31', null, null, null, '50', '0', '4', '1', '3', '2016-10-27 14:29:01', '3', '');
INSERT INTO `funding_sources` VALUES ('290', 'AfricaRice-Africa Rice Center', '2016-01-01', '2022-12-31', null, null, null, '52', '0', '4', '1', '3', '2016-10-27 14:29:01', '3', '');
INSERT INTO `funding_sources` VALUES ('291', 'ILRI-International Livestock Research Institute', '2016-01-01', '2022-12-31', null, null, null, '66', '0', '4', '1', '3', '2016-10-27 14:29:01', '3', '');
INSERT INTO `funding_sources` VALUES ('292', 'CIP-Centro Internacional de la Papa', '2016-01-01', '2022-12-31', null, null, null, '67', '0', '4', '1', '3', '2016-10-27 14:29:01', '3', '');
INSERT INTO `funding_sources` VALUES ('293', 'Global Crop Diversity Trust', '2016-01-01', '2022-12-31', null, null, null, '71', '0', '4', '1', '3', '2016-10-27 14:29:01', '3', '');
INSERT INTO `funding_sources` VALUES ('294', 'ICRAF-World Agroforestry Centre', '2016-01-01', '2022-12-31', null, null, null, '88', '0', '4', '1', '3', '2016-10-27 14:29:01', '3', '');
INSERT INTO `funding_sources` VALUES ('295', 'IFPRI-International Food Policy Research Institute', '2016-01-01', '2022-12-31', null, null, null, '89', '0', '4', '1', '3', '2016-10-27 14:29:01', '3', '');
INSERT INTO `funding_sources` VALUES ('296', 'WorldFish', '2016-01-01', '2022-12-31', null, null, null, '99', '0', '4', '1', '3', '2016-10-27 14:29:01', '3', '');
INSERT INTO `funding_sources` VALUES ('297', 'CIFOR-Center for International Forestry Research', '2016-01-01', '2022-12-31', null, null, null, '115', '0', '4', '1', '3', '2016-10-27 14:29:01', '3', '');
INSERT INTO `funding_sources` VALUES ('298', 'IWMI-International Water Management Institute', '2016-01-01', '2022-12-31', null, null, null, '172', '0', '4', '1', '3', '2016-10-27 14:29:01', '3', '');
INSERT INTO `funding_sources` VALUES ('299', 'CAPRi-CGIAR Systemwide Program on Collective Action and Property Rights', '2016-01-01', '2022-12-31', null, null, null, '182', '0', '4', '1', '3', '2016-10-27 14:29:01', '3', '');
INSERT INTO `funding_sources` VALUES ('300', 'CGIAR Consortium Office', '2016-01-01', '2022-12-31', null, null, null, '221', '0', '4', '1', '3', '2016-10-27 14:29:01', '3', '');
INSERT INTO `funding_sources` VALUES ('301', 'ICRISAT-International Crops Research Institute for the Semi-Arid Tropics', '2016-01-01', '2022-12-31', null, null, null, '1273', '0', '4', '1', '3', '2016-10-27 14:29:01', '3', '');
INSERT INTO `funding_sources` VALUES ('302', 'ICARDA-International Center for Agricultural Research in the Dry Areas', '2016-01-01', '2022-12-31', null, null, null, '1279', '0', '4', '1', '3', '2016-10-27 14:29:01', '3', '');
