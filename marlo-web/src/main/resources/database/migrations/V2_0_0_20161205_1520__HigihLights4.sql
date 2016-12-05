/*
Navicat MySQL Data Transfer

Source Server         : local
Source Server Version : 50617
Source Host           : localhost:3306
Source Database       : ccafs_marlo

Target Server Type    : MYSQL
Target Server Version : 50617
File Encoding         : 65001

Date: 2016-12-05 13:22:08
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `project_highligths_country`
-- ----------------------------
DROP TABLE IF EXISTS `project_highligths_country`;
CREATE TABLE `project_highligths_country` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `project_highlights_id` bigint(20) NOT NULL,
  `id_country` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `project_highlights_id` (`project_highlights_id`),
  KEY `id_country` (`id_country`),
  CONSTRAINT `project_highligths_country_ibfk_1` FOREIGN KEY (`project_highlights_id`) REFERENCES `project_highligths` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=108 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of project_highligths_country
-- ----------------------------
INSERT INTO `project_highligths_country` VALUES ('1', '7', '230');
INSERT INTO `project_highligths_country` VALUES ('2', '8', '230');
INSERT INTO `project_highligths_country` VALUES ('4', '13', '53');
INSERT INTO `project_highligths_country` VALUES ('6', '13', '17');
INSERT INTO `project_highligths_country` VALUES ('7', '13', '50');
INSERT INTO `project_highligths_country` VALUES ('8', '13', '36');
INSERT INTO `project_highligths_country` VALUES ('9', '15', '53');
INSERT INTO `project_highligths_country` VALUES ('10', '16', '124');
INSERT INTO `project_highligths_country` VALUES ('11', '32', '90');
INSERT INTO `project_highligths_country` VALUES ('12', '37', '103');
INSERT INTO `project_highligths_country` VALUES ('13', '38', '72');
INSERT INTO `project_highligths_country` VALUES ('14', '40', '53');
INSERT INTO `project_highligths_country` VALUES ('15', '39', '53');
INSERT INTO `project_highligths_country` VALUES ('17', '41', '53');
INSERT INTO `project_highligths_country` VALUES ('18', '33', '103');
INSERT INTO `project_highligths_country` VALUES ('19', '20', '54');
INSERT INTO `project_highligths_country` VALUES ('20', '20', '53');
INSERT INTO `project_highligths_country` VALUES ('21', '20', '17');
INSERT INTO `project_highligths_country` VALUES ('23', '20', '36');
INSERT INTO `project_highligths_country` VALUES ('25', '50', '169');
INSERT INTO `project_highligths_country` VALUES ('26', '50', '83');
INSERT INTO `project_highligths_country` VALUES ('27', '51', '53');
INSERT INTO `project_highligths_country` VALUES ('29', '52', '124');
INSERT INTO `project_highligths_country` VALUES ('30', '55', '221');
INSERT INTO `project_highligths_country` VALUES ('31', '55', '160');
INSERT INTO `project_highligths_country` VALUES ('32', '55', '230');
INSERT INTO `project_highligths_country` VALUES ('33', '56', '142');
INSERT INTO `project_highligths_country` VALUES ('34', '62', '103');
INSERT INTO `project_highligths_country` VALUES ('35', '60', '83');
INSERT INTO `project_highligths_country` VALUES ('36', '64', '53');
INSERT INTO `project_highligths_country` VALUES ('37', '64', '90');
INSERT INTO `project_highligths_country` VALUES ('38', '65', '36');
INSERT INTO `project_highligths_country` VALUES ('41', '71', '53');
INSERT INTO `project_highligths_country` VALUES ('46', '47', '113');
INSERT INTO `project_highligths_country` VALUES ('49', '76', '27');
INSERT INTO `project_highligths_country` VALUES ('50', '21', '95');
INSERT INTO `project_highligths_country` VALUES ('51', '83', '159');
INSERT INTO `project_highligths_country` VALUES ('52', '87', '113');
INSERT INTO `project_highligths_country` VALUES ('53', '19', '172');
INSERT INTO `project_highligths_country` VALUES ('54', '92', '53');
INSERT INTO `project_highligths_country` VALUES ('58', '92', '95');
INSERT INTO `project_highligths_country` VALUES ('59', '92', '90');
INSERT INTO `project_highligths_country` VALUES ('60', '93', '95');
INSERT INTO `project_highligths_country` VALUES ('70', '20', '160');
INSERT INTO `project_highligths_country` VALUES ('71', '20', '216');
INSERT INTO `project_highligths_country` VALUES ('72', '30', '103');
INSERT INTO `project_highligths_country` VALUES ('73', '104', '124');
INSERT INTO `project_highligths_country` VALUES ('74', '104', '172');
INSERT INTO `project_highligths_country` VALUES ('75', '104', '230');
INSERT INTO `project_highligths_country` VALUES ('76', '104', '115');
INSERT INTO `project_highligths_country` VALUES ('77', '105', '143');
INSERT INTO `project_highligths_country` VALUES ('78', '106', '221');
INSERT INTO `project_highligths_country` VALUES ('79', '106', '219');
INSERT INTO `project_highligths_country` VALUES ('80', '106', '83');
INSERT INTO `project_highligths_country` VALUES ('81', '106', '95');
INSERT INTO `project_highligths_country` VALUES ('82', '106', '27');
INSERT INTO `project_highligths_country` VALUES ('83', '106', '115');
INSERT INTO `project_highligths_country` VALUES ('84', '106', '25');
INSERT INTO `project_highligths_country` VALUES ('85', '80', '54');
INSERT INTO `project_highligths_country` VALUES ('86', '107', '25');
INSERT INTO `project_highligths_country` VALUES ('87', '108', '27');
INSERT INTO `project_highligths_country` VALUES ('88', '102', '103');
INSERT INTO `project_highligths_country` VALUES ('89', '112', '221');
INSERT INTO `project_highligths_country` VALUES ('90', '119', '219');
INSERT INTO `project_highligths_country` VALUES ('91', '119', '83');
INSERT INTO `project_highligths_country` VALUES ('92', '119', '152');
INSERT INTO `project_highligths_country` VALUES ('93', '120', '83');
INSERT INTO `project_highligths_country` VALUES ('94', '122', '53');
INSERT INTO `project_highligths_country` VALUES ('95', '122', '202');
INSERT INTO `project_highligths_country` VALUES ('96', '122', '90');
INSERT INTO `project_highligths_country` VALUES ('97', '123', '198');
INSERT INTO `project_highligths_country` VALUES ('98', '125', '53');
INSERT INTO `project_highligths_country` VALUES ('99', '127', '113');
INSERT INTO `project_highligths_country` VALUES ('100', '128', '230');
INSERT INTO `project_highligths_country` VALUES ('101', '129', '230');
INSERT INTO `project_highligths_country` VALUES ('102', '130', '230');
INSERT INTO `project_highligths_country` VALUES ('103', '131', '230');
INSERT INTO `project_highligths_country` VALUES ('104', '132', '230');
INSERT INTO `project_highligths_country` VALUES ('105', '133', '230');
INSERT INTO `project_highligths_country` VALUES ('106', '134', '230');
INSERT INTO `project_highligths_country` VALUES ('107', '135', '230');

-- ----------------------------
-- Table structure for `project_highligths_types`
-- ----------------------------
DROP TABLE IF EXISTS `project_highligths_types`;
CREATE TABLE `project_highligths_types` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `project_highlights_id` bigint(20) NOT NULL,
  `id_type` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `project_highlights_id` (`project_highlights_id`),
  CONSTRAINT `project_highligths_types_ibfk_1` FOREIGN KEY (`project_highlights_id`) REFERENCES `project_highligths` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=226 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of project_highligths_types
-- ----------------------------
INSERT INTO `project_highligths_types` VALUES ('1', '1', '3');
INSERT INTO `project_highligths_types` VALUES ('2', '1', '7');
INSERT INTO `project_highligths_types` VALUES ('3', '2', '7');
INSERT INTO `project_highligths_types` VALUES ('4', '3', '3');
INSERT INTO `project_highligths_types` VALUES ('5', '7', '5');
INSERT INTO `project_highligths_types` VALUES ('6', '7', '6');
INSERT INTO `project_highligths_types` VALUES ('7', '8', '5');
INSERT INTO `project_highligths_types` VALUES ('8', '8', '6');
INSERT INTO `project_highligths_types` VALUES ('9', '10', '4');
INSERT INTO `project_highligths_types` VALUES ('10', '13', '5');
INSERT INTO `project_highligths_types` VALUES ('12', '11', '4');
INSERT INTO `project_highligths_types` VALUES ('13', '12', '4');
INSERT INTO `project_highligths_types` VALUES ('14', '13', '4');
INSERT INTO `project_highligths_types` VALUES ('15', '15', '4');
INSERT INTO `project_highligths_types` VALUES ('16', '16', '1');
INSERT INTO `project_highligths_types` VALUES ('17', '16', '2');
INSERT INTO `project_highligths_types` VALUES ('18', '22', '3');
INSERT INTO `project_highligths_types` VALUES ('19', '22', '7');
INSERT INTO `project_highligths_types` VALUES ('20', '22', '6');
INSERT INTO `project_highligths_types` VALUES ('21', '22', '9');
INSERT INTO `project_highligths_types` VALUES ('22', '23', '3');
INSERT INTO `project_highligths_types` VALUES ('23', '23', '2');
INSERT INTO `project_highligths_types` VALUES ('24', '23', '7');
INSERT INTO `project_highligths_types` VALUES ('25', '23', '5');
INSERT INTO `project_highligths_types` VALUES ('26', '23', '9');
INSERT INTO `project_highligths_types` VALUES ('27', '29', '2');
INSERT INTO `project_highligths_types` VALUES ('28', '32', '4');
INSERT INTO `project_highligths_types` VALUES ('29', '22', '8');
INSERT INTO `project_highligths_types` VALUES ('30', '37', '4');
INSERT INTO `project_highligths_types` VALUES ('31', '27', '3');
INSERT INTO `project_highligths_types` VALUES ('32', '38', '4');
INSERT INTO `project_highligths_types` VALUES ('34', '39', '3');
INSERT INTO `project_highligths_types` VALUES ('36', '39', '8');
INSERT INTO `project_highligths_types` VALUES ('37', '40', '3');
INSERT INTO `project_highligths_types` VALUES ('38', '40', '1');
INSERT INTO `project_highligths_types` VALUES ('39', '40', '8');
INSERT INTO `project_highligths_types` VALUES ('40', '41', '8');
INSERT INTO `project_highligths_types` VALUES ('41', '41', '3');
INSERT INTO `project_highligths_types` VALUES ('42', '41', '1');
INSERT INTO `project_highligths_types` VALUES ('43', '33', '3');
INSERT INTO `project_highligths_types` VALUES ('45', '43', '1');
INSERT INTO `project_highligths_types` VALUES ('46', '43', '8');
INSERT INTO `project_highligths_types` VALUES ('48', '48', '4');
INSERT INTO `project_highligths_types` VALUES ('49', '20', '5');
INSERT INTO `project_highligths_types` VALUES ('50', '20', '9');
INSERT INTO `project_highligths_types` VALUES ('51', '49', '3');
INSERT INTO `project_highligths_types` VALUES ('52', '49', '1');
INSERT INTO `project_highligths_types` VALUES ('53', '49', '8');
INSERT INTO `project_highligths_types` VALUES ('54', '50', '3');
INSERT INTO `project_highligths_types` VALUES ('55', '50', '2');
INSERT INTO `project_highligths_types` VALUES ('56', '50', '8');
INSERT INTO `project_highligths_types` VALUES ('57', '51', '3');
INSERT INTO `project_highligths_types` VALUES ('58', '51', '1');
INSERT INTO `project_highligths_types` VALUES ('59', '51', '5');
INSERT INTO `project_highligths_types` VALUES ('60', '51', '9');
INSERT INTO `project_highligths_types` VALUES ('61', '52', '3');
INSERT INTO `project_highligths_types` VALUES ('62', '52', '7');
INSERT INTO `project_highligths_types` VALUES ('63', '52', '5');
INSERT INTO `project_highligths_types` VALUES ('64', '53', '3');
INSERT INTO `project_highligths_types` VALUES ('65', '53', '5');
INSERT INTO `project_highligths_types` VALUES ('66', '53', '9');
INSERT INTO `project_highligths_types` VALUES ('67', '4', '4');
INSERT INTO `project_highligths_types` VALUES ('68', '4', '2');
INSERT INTO `project_highligths_types` VALUES ('69', '4', '8');
INSERT INTO `project_highligths_types` VALUES ('70', '55', '3');
INSERT INTO `project_highligths_types` VALUES ('71', '56', '4');
INSERT INTO `project_highligths_types` VALUES ('72', '56', '1');
INSERT INTO `project_highligths_types` VALUES ('73', '56', '2');
INSERT INTO `project_highligths_types` VALUES ('74', '56', '8');
INSERT INTO `project_highligths_types` VALUES ('75', '62', '3');
INSERT INTO `project_highligths_types` VALUES ('76', '60', '3');
INSERT INTO `project_highligths_types` VALUES ('77', '64', '7');
INSERT INTO `project_highligths_types` VALUES ('78', '65', '6');
INSERT INTO `project_highligths_types` VALUES ('79', '48', '1');
INSERT INTO `project_highligths_types` VALUES ('80', '70', '1');
INSERT INTO `project_highligths_types` VALUES ('81', '71', '5');
INSERT INTO `project_highligths_types` VALUES ('82', '72', '1');
INSERT INTO `project_highligths_types` VALUES ('83', '63', '3');
INSERT INTO `project_highligths_types` VALUES ('84', '73', '2');
INSERT INTO `project_highligths_types` VALUES ('85', '73', '8');
INSERT INTO `project_highligths_types` VALUES ('86', '17', '7');
INSERT INTO `project_highligths_types` VALUES ('87', '17', '6');
INSERT INTO `project_highligths_types` VALUES ('88', '17', '9');
INSERT INTO `project_highligths_types` VALUES ('89', '26', '8');
INSERT INTO `project_highligths_types` VALUES ('90', '47', '8');
INSERT INTO `project_highligths_types` VALUES ('91', '75', '8');
INSERT INTO `project_highligths_types` VALUES ('92', '78', '6');
INSERT INTO `project_highligths_types` VALUES ('93', '21', '3');
INSERT INTO `project_highligths_types` VALUES ('94', '21', '8');
INSERT INTO `project_highligths_types` VALUES ('95', '21', '5');
INSERT INTO `project_highligths_types` VALUES ('96', '83', '8');
INSERT INTO `project_highligths_types` VALUES ('97', '83', '5');
INSERT INTO `project_highligths_types` VALUES ('98', '66', '4');
INSERT INTO `project_highligths_types` VALUES ('99', '66', '8');
INSERT INTO `project_highligths_types` VALUES ('100', '84', '4');
INSERT INTO `project_highligths_types` VALUES ('101', '85', '4');
INSERT INTO `project_highligths_types` VALUES ('102', '85', '1');
INSERT INTO `project_highligths_types` VALUES ('103', '85', '8');
INSERT INTO `project_highligths_types` VALUES ('104', '86', '4');
INSERT INTO `project_highligths_types` VALUES ('105', '87', '3');
INSERT INTO `project_highligths_types` VALUES ('106', '88', '6');
INSERT INTO `project_highligths_types` VALUES ('107', '19', '3');
INSERT INTO `project_highligths_types` VALUES ('109', '91', '7');
INSERT INTO `project_highligths_types` VALUES ('110', '91', '8');
INSERT INTO `project_highligths_types` VALUES ('111', '91', '5');
INSERT INTO `project_highligths_types` VALUES ('112', '91', '6');
INSERT INTO `project_highligths_types` VALUES ('113', '91', '9');
INSERT INTO `project_highligths_types` VALUES ('114', '92', '4');
INSERT INTO `project_highligths_types` VALUES ('115', '92', '2');
INSERT INTO `project_highligths_types` VALUES ('116', '92', '5');
INSERT INTO `project_highligths_types` VALUES ('117', '93', '3');
INSERT INTO `project_highligths_types` VALUES ('118', '93', '2');
INSERT INTO `project_highligths_types` VALUES ('119', '93', '8');
INSERT INTO `project_highligths_types` VALUES ('120', '93', '5');
INSERT INTO `project_highligths_types` VALUES ('121', '93', '9');
INSERT INTO `project_highligths_types` VALUES ('122', '98', '1');
INSERT INTO `project_highligths_types` VALUES ('123', '98', '5');
INSERT INTO `project_highligths_types` VALUES ('124', '99', '1');
INSERT INTO `project_highligths_types` VALUES ('125', '99', '5');
INSERT INTO `project_highligths_types` VALUES ('126', '100', '8');
INSERT INTO `project_highligths_types` VALUES ('128', '30', '3');
INSERT INTO `project_highligths_types` VALUES ('129', '30', '2');
INSERT INTO `project_highligths_types` VALUES ('130', '30', '5');
INSERT INTO `project_highligths_types` VALUES ('131', '30', '9');
INSERT INTO `project_highligths_types` VALUES ('132', '76', '3');
INSERT INTO `project_highligths_types` VALUES ('133', '104', '4');
INSERT INTO `project_highligths_types` VALUES ('134', '104', '2');
INSERT INTO `project_highligths_types` VALUES ('135', '104', '7');
INSERT INTO `project_highligths_types` VALUES ('136', '104', '5');
INSERT INTO `project_highligths_types` VALUES ('137', '105', '4');
INSERT INTO `project_highligths_types` VALUES ('138', '105', '7');
INSERT INTO `project_highligths_types` VALUES ('139', '105', '8');
INSERT INTO `project_highligths_types` VALUES ('140', '105', '9');
INSERT INTO `project_highligths_types` VALUES ('141', '106', '3');
INSERT INTO `project_highligths_types` VALUES ('142', '106', '4');
INSERT INTO `project_highligths_types` VALUES ('143', '106', '1');
INSERT INTO `project_highligths_types` VALUES ('144', '106', '2');
INSERT INTO `project_highligths_types` VALUES ('145', '106', '7');
INSERT INTO `project_highligths_types` VALUES ('146', '106', '8');
INSERT INTO `project_highligths_types` VALUES ('147', '106', '5');
INSERT INTO `project_highligths_types` VALUES ('148', '106', '6');
INSERT INTO `project_highligths_types` VALUES ('149', '106', '9');
INSERT INTO `project_highligths_types` VALUES ('150', '80', '3');
INSERT INTO `project_highligths_types` VALUES ('151', '80', '4');
INSERT INTO `project_highligths_types` VALUES ('152', '80', '1');
INSERT INTO `project_highligths_types` VALUES ('153', '80', '2');
INSERT INTO `project_highligths_types` VALUES ('154', '80', '7');
INSERT INTO `project_highligths_types` VALUES ('155', '80', '8');
INSERT INTO `project_highligths_types` VALUES ('156', '80', '5');
INSERT INTO `project_highligths_types` VALUES ('157', '80', '6');
INSERT INTO `project_highligths_types` VALUES ('158', '80', '9');
INSERT INTO `project_highligths_types` VALUES ('159', '49', '4');
INSERT INTO `project_highligths_types` VALUES ('160', '108', '3');
INSERT INTO `project_highligths_types` VALUES ('161', '108', '4');
INSERT INTO `project_highligths_types` VALUES ('162', '108', '1');
INSERT INTO `project_highligths_types` VALUES ('163', '108', '2');
INSERT INTO `project_highligths_types` VALUES ('164', '108', '7');
INSERT INTO `project_highligths_types` VALUES ('165', '108', '8');
INSERT INTO `project_highligths_types` VALUES ('166', '108', '5');
INSERT INTO `project_highligths_types` VALUES ('167', '108', '6');
INSERT INTO `project_highligths_types` VALUES ('168', '108', '9');
INSERT INTO `project_highligths_types` VALUES ('169', '107', '8');
INSERT INTO `project_highligths_types` VALUES ('170', '107', '5');
INSERT INTO `project_highligths_types` VALUES ('171', '107', '9');
INSERT INTO `project_highligths_types` VALUES ('172', '102', '6');
INSERT INTO `project_highligths_types` VALUES ('173', '31', '1');
INSERT INTO `project_highligths_types` VALUES ('174', '31', '5');
INSERT INTO `project_highligths_types` VALUES ('175', '31', '9');
INSERT INTO `project_highligths_types` VALUES ('176', '110', '3');
INSERT INTO `project_highligths_types` VALUES ('177', '110', '4');
INSERT INTO `project_highligths_types` VALUES ('178', '110', '1');
INSERT INTO `project_highligths_types` VALUES ('179', '110', '2');
INSERT INTO `project_highligths_types` VALUES ('180', '110', '7');
INSERT INTO `project_highligths_types` VALUES ('181', '110', '8');
INSERT INTO `project_highligths_types` VALUES ('182', '110', '5');
INSERT INTO `project_highligths_types` VALUES ('183', '110', '6');
INSERT INTO `project_highligths_types` VALUES ('184', '110', '9');
INSERT INTO `project_highligths_types` VALUES ('185', '86', '6');
INSERT INTO `project_highligths_types` VALUES ('186', '114', '2');
INSERT INTO `project_highligths_types` VALUES ('187', '114', '7');
INSERT INTO `project_highligths_types` VALUES ('188', '112', '1');
INSERT INTO `project_highligths_types` VALUES ('189', '112', '6');
INSERT INTO `project_highligths_types` VALUES ('190', '100', '4');
INSERT INTO `project_highligths_types` VALUES ('191', '113', '3');
INSERT INTO `project_highligths_types` VALUES ('192', '113', '5');
INSERT INTO `project_highligths_types` VALUES ('193', '113', '9');
INSERT INTO `project_highligths_types` VALUES ('194', '118', '4');
INSERT INTO `project_highligths_types` VALUES ('195', '118', '5');
INSERT INTO `project_highligths_types` VALUES ('196', '119', '5');
INSERT INTO `project_highligths_types` VALUES ('197', '120', '2');
INSERT INTO `project_highligths_types` VALUES ('198', '100', '6');
INSERT INTO `project_highligths_types` VALUES ('199', '122', '1');
INSERT INTO `project_highligths_types` VALUES ('200', '123', '5');
INSERT INTO `project_highligths_types` VALUES ('201', '114', '5');
INSERT INTO `project_highligths_types` VALUES ('202', '124', '3');
INSERT INTO `project_highligths_types` VALUES ('203', '124', '4');
INSERT INTO `project_highligths_types` VALUES ('204', '124', '8');
INSERT INTO `project_highligths_types` VALUES ('205', '124', '5');
INSERT INTO `project_highligths_types` VALUES ('206', '125', '3');
INSERT INTO `project_highligths_types` VALUES ('207', '125', '1');
INSERT INTO `project_highligths_types` VALUES ('208', '125', '5');
INSERT INTO `project_highligths_types` VALUES ('209', '125', '9');
INSERT INTO `project_highligths_types` VALUES ('210', '127', '8');
INSERT INTO `project_highligths_types` VALUES ('211', '127', '6');
INSERT INTO `project_highligths_types` VALUES ('212', '127', '9');
INSERT INTO `project_highligths_types` VALUES ('213', '128', '3');
INSERT INTO `project_highligths_types` VALUES ('214', '128', '7');
INSERT INTO `project_highligths_types` VALUES ('215', '129', '3');
INSERT INTO `project_highligths_types` VALUES ('216', '130', '3');
INSERT INTO `project_highligths_types` VALUES ('217', '130', '1');
INSERT INTO `project_highligths_types` VALUES ('218', '131', '3');
INSERT INTO `project_highligths_types` VALUES ('219', '131', '1');
INSERT INTO `project_highligths_types` VALUES ('220', '132', '3');
INSERT INTO `project_highligths_types` VALUES ('221', '133', '3');
INSERT INTO `project_highligths_types` VALUES ('222', '134', '3');
INSERT INTO `project_highligths_types` VALUES ('223', '134', '1');
INSERT INTO `project_highligths_types` VALUES ('224', '135', '3');
INSERT INTO `project_highligths_types` VALUES ('225', '135', '1');
