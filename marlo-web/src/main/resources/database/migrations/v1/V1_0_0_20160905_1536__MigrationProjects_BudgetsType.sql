/*
Navicat MySQL Data Transfer

Source Server         : produccion
Source Server Version : 50537
Source Host           : 54.83.57.241:3306
Source Database       : ccafspr_ip

Target Server Type    : MYSQL
Target Server Version : 50537
File Encoding         : 65001

Date: 2016-09-05 15:36:17
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `budget_types`
-- ----------------------------
DROP TABLE IF EXISTS `budget_types`;
CREATE TABLE `budget_types` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of budget_types
-- ----------------------------
INSERT INTO `budget_types` VALUES ('1', 'W1 W2 Budget');
INSERT INTO `budget_types` VALUES ('2', 'W3/Bilateral Budget');
INSERT INTO `budget_types` VALUES ('3', 'Bilateral');
INSERT INTO `budget_types` VALUES ('4', 'Center');

