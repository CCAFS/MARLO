/*
Navicat MySQL Data Transfer

Source Server         : local
Source Server Version : 50617
Source Host           : localhost:3306
Source Database       : ccafs_marlo

Target Server Type    : MYSQL
Target Server Version : 50617
File Encoding         : 65001

Date: 2017-01-12 19:11:42
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `case_studie_indicators`
-- ----------------------------
DROP TABLE IF EXISTS `case_studie_indicators`;
CREATE TABLE `case_studie_indicators` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `id_case_studie` bigint(20) NOT NULL,
  `id_indicator` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `id_case_studie` (`id_case_studie`),
  KEY `id_indicator` (`id_indicator`),
  CONSTRAINT `case_studie_indicators_ibfk_2` FOREIGN KEY (`id_indicator`) REFERENCES `ip_indicators` (`id`),
  CONSTRAINT `case_studie_indicators_ibfk_1` FOREIGN KEY (`id_case_studie`) REFERENCES `cases_studies` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=106 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of case_studie_indicators
-- ----------------------------
INSERT INTO `case_studie_indicators` VALUES ('1', '2', '36');
INSERT INTO `case_studie_indicators` VALUES ('2', '2', '13');
INSERT INTO `case_studie_indicators` VALUES ('3', '5', '13');
INSERT INTO `case_studie_indicators` VALUES ('4', '8', '38');
INSERT INTO `case_studie_indicators` VALUES ('5', '9', '36');
INSERT INTO `case_studie_indicators` VALUES ('6', '9', '52');
INSERT INTO `case_studie_indicators` VALUES ('7', '9', '13');
INSERT INTO `case_studie_indicators` VALUES ('8', '9', '37');
INSERT INTO `case_studie_indicators` VALUES ('9', '10', '36');
INSERT INTO `case_studie_indicators` VALUES ('10', '3', '50');
INSERT INTO `case_studie_indicators` VALUES ('11', '6', '52');
INSERT INTO `case_studie_indicators` VALUES ('12', '12', '50');
INSERT INTO `case_studie_indicators` VALUES ('13', '1', '36');
INSERT INTO `case_studie_indicators` VALUES ('14', '14', '36');
INSERT INTO `case_studie_indicators` VALUES ('15', '15', '50');
INSERT INTO `case_studie_indicators` VALUES ('16', '13', '52');
INSERT INTO `case_studie_indicators` VALUES ('17', '16', '39');
INSERT INTO `case_studie_indicators` VALUES ('18', '16', '38');
INSERT INTO `case_studie_indicators` VALUES ('19', '17', '13');
INSERT INTO `case_studie_indicators` VALUES ('20', '18', '50');
INSERT INTO `case_studie_indicators` VALUES ('21', '19', '36');
INSERT INTO `case_studie_indicators` VALUES ('22', '19', '50');
INSERT INTO `case_studie_indicators` VALUES ('23', '21', '50');
INSERT INTO `case_studie_indicators` VALUES ('24', '20', '50');
INSERT INTO `case_studie_indicators` VALUES ('25', '20', '36');
INSERT INTO `case_studie_indicators` VALUES ('26', '22', '36');
INSERT INTO `case_studie_indicators` VALUES ('27', '22', '50');
INSERT INTO `case_studie_indicators` VALUES ('28', '23', '14');
INSERT INTO `case_studie_indicators` VALUES ('29', '24', '50');
INSERT INTO `case_studie_indicators` VALUES ('30', '25', '36');
INSERT INTO `case_studie_indicators` VALUES ('31', '25', '50');
INSERT INTO `case_studie_indicators` VALUES ('32', '26', '36');
INSERT INTO `case_studie_indicators` VALUES ('33', '26', '50');
INSERT INTO `case_studie_indicators` VALUES ('34', '27', '13');
INSERT INTO `case_studie_indicators` VALUES ('35', '28', '13');
INSERT INTO `case_studie_indicators` VALUES ('36', '28', '14');
INSERT INTO `case_studie_indicators` VALUES ('37', '31', '39');
INSERT INTO `case_studie_indicators` VALUES ('38', '32', '36');
INSERT INTO `case_studie_indicators` VALUES ('39', '32', '13');
INSERT INTO `case_studie_indicators` VALUES ('40', '4', '52');
INSERT INTO `case_studie_indicators` VALUES ('41', '34', '38');
INSERT INTO `case_studie_indicators` VALUES ('42', '35', '36');
INSERT INTO `case_studie_indicators` VALUES ('43', '35', '13');
INSERT INTO `case_studie_indicators` VALUES ('44', '35', '37');
INSERT INTO `case_studie_indicators` VALUES ('45', '36', '36');
INSERT INTO `case_studie_indicators` VALUES ('46', '37', '38');
INSERT INTO `case_studie_indicators` VALUES ('47', '37', '39');
INSERT INTO `case_studie_indicators` VALUES ('48', '4', '13');
INSERT INTO `case_studie_indicators` VALUES ('49', '38', '50');
INSERT INTO `case_studie_indicators` VALUES ('50', '38', '38');
INSERT INTO `case_studie_indicators` VALUES ('51', '38', '39');
INSERT INTO `case_studie_indicators` VALUES ('52', '7', '52');
INSERT INTO `case_studie_indicators` VALUES ('53', '39', '50');
INSERT INTO `case_studie_indicators` VALUES ('54', '40', '39');
INSERT INTO `case_studie_indicators` VALUES ('55', '40', '38');
INSERT INTO `case_studie_indicators` VALUES ('56', '41', '38');
INSERT INTO `case_studie_indicators` VALUES ('57', '42', '38');
INSERT INTO `case_studie_indicators` VALUES ('58', '43', '13');
INSERT INTO `case_studie_indicators` VALUES ('59', '43', '37');
INSERT INTO `case_studie_indicators` VALUES ('60', '44', '36');
INSERT INTO `case_studie_indicators` VALUES ('61', '44', '13');
INSERT INTO `case_studie_indicators` VALUES ('62', '44', '37');
INSERT INTO `case_studie_indicators` VALUES ('63', '30', '52');
INSERT INTO `case_studie_indicators` VALUES ('64', '45', '50');
INSERT INTO `case_studie_indicators` VALUES ('65', '45', '39');
INSERT INTO `case_studie_indicators` VALUES ('66', '45', '38');
INSERT INTO `case_studie_indicators` VALUES ('67', '37', '50');
INSERT INTO `case_studie_indicators` VALUES ('68', '11', '37');
INSERT INTO `case_studie_indicators` VALUES ('69', '11', '36');
INSERT INTO `case_studie_indicators` VALUES ('70', '11', '52');
INSERT INTO `case_studie_indicators` VALUES ('71', '11', '14');
INSERT INTO `case_studie_indicators` VALUES ('72', '11', '13');
INSERT INTO `case_studie_indicators` VALUES ('73', '47', '13');
INSERT INTO `case_studie_indicators` VALUES ('74', '49', '13');
INSERT INTO `case_studie_indicators` VALUES ('76', '48', '50');
INSERT INTO `case_studie_indicators` VALUES ('77', '46', '50');
INSERT INTO `case_studie_indicators` VALUES ('78', '50', '50');
INSERT INTO `case_studie_indicators` VALUES ('79', '52', '52');
INSERT INTO `case_studie_indicators` VALUES ('80', '33', '36');
INSERT INTO `case_studie_indicators` VALUES ('81', '53', '36');
INSERT INTO `case_studie_indicators` VALUES ('82', '53', '37');
INSERT INTO `case_studie_indicators` VALUES ('83', '54', '50');
INSERT INTO `case_studie_indicators` VALUES ('84', '55', '50');
INSERT INTO `case_studie_indicators` VALUES ('85', '56', '52');
INSERT INTO `case_studie_indicators` VALUES ('86', '56', '13');
INSERT INTO `case_studie_indicators` VALUES ('87', '57', '50');
INSERT INTO `case_studie_indicators` VALUES ('88', '29', '14');
INSERT INTO `case_studie_indicators` VALUES ('89', '35', '39');
INSERT INTO `case_studie_indicators` VALUES ('90', '58', '52');
INSERT INTO `case_studie_indicators` VALUES ('91', '58', '50');
INSERT INTO `case_studie_indicators` VALUES ('92', '40', '50');
INSERT INTO `case_studie_indicators` VALUES ('93', '46', '13');
INSERT INTO `case_studie_indicators` VALUES ('94', '59', '13');
INSERT INTO `case_studie_indicators` VALUES ('95', '13', '50');
INSERT INTO `case_studie_indicators` VALUES ('96', '60', '50');
INSERT INTO `case_studie_indicators` VALUES ('97', '61', '36');
INSERT INTO `case_studie_indicators` VALUES ('99', '61', '13');
INSERT INTO `case_studie_indicators` VALUES ('100', '62', '50');
INSERT INTO `case_studie_indicators` VALUES ('101', '62', '38');
INSERT INTO `case_studie_indicators` VALUES ('102', '63', '36');
INSERT INTO `case_studie_indicators` VALUES ('103', '63', '13');
INSERT INTO `case_studie_indicators` VALUES ('104', '64', '36');
INSERT INTO `case_studie_indicators` VALUES ('105', '65', '36');
