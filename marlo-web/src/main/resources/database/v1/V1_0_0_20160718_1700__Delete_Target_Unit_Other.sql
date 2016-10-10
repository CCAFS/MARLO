/*
Navicat MySQL Data Transfer

Source Server         : LocalHost
Source Server Version : 50712
Source Host           : localhost:3306
Source Database       : ccafspr_marlo

Target Server Type    : MYSQL
Target Server Version : 50712
File Encoding         : 65001

Date: 2016-07-18 16:33:52
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for srf_target_units
-- ----------------------------
DROP TABLE IF EXISTS `srf_target_units`;
CREATE TABLE `srf_target_units` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` text NOT NULL,
  `is_active` tinyint(1) NOT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `active_since` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified_by` bigint(20) NOT NULL,
  `modification_justification` text NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_srf_target_units_created_by_users_id` (`created_by`) USING BTREE,
  KEY `fk_srf_target_units_modified_by_users_id` (`modified_by`) USING BTREE,
  CONSTRAINT `srf_target_units_ibfk_1` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `srf_target_units_ibfk_2` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of srf_target_units
-- ----------------------------
INSERT INTO `srf_target_units` VALUES ('1', '% / year', '1', '3', '2016-06-29 08:22:36', '3', ' ');
INSERT INTO `srf_target_units` VALUES ('2', '% increase', '1', '3', '2016-07-05 17:48:58', '3', 'a');
INSERT INTO `srf_target_units` VALUES ('3', '% reduction', '1', '3', '2016-07-12 03:15:20', '3', 'a');
INSERT INTO `srf_target_units` VALUES ('4', 'Agricultural development initiatives', '1', '3', '2016-07-18 12:41:42', '3', 'a');
INSERT INTO `srf_target_units` VALUES ('5', 'Countries/states', '1', '3', '2016-07-24 22:08:04', '3', 'a');
INSERT INTO `srf_target_units` VALUES ('6', 'Development organizations', '1', '3', '2016-07-31 07:34:26', '3', 'a');
INSERT INTO `srf_target_units` VALUES ('7', 'Low emission plans', '1', '3', '2016-08-06 17:00:48', '3', 'a');
INSERT INTO `srf_target_units` VALUES ('8', 'Million farm households', '1', '3', '2016-08-13 02:27:10', '3', 'a');
INSERT INTO `srf_target_units` VALUES ('9', 'Million hectares (ha)', '1', '3', '2016-08-19 11:53:32', '3', 'a');
INSERT INTO `srf_target_units` VALUES ('10', 'Million farm households', '1', '3', '2016-08-25 21:19:54', '3', 'a');
INSERT INTO `srf_target_units` VALUES ('11', 'Million people', '1', '3', '2016-09-01 06:46:16', '3', 'a');
INSERT INTO `srf_target_units` VALUES ('12', 'Organizations and Institutions', '1', '3', '2016-09-07 16:12:38', '3', 'a');
INSERT INTO `srf_target_units` VALUES ('13', 'Policy decisions taken', '1', '3', '2016-09-14 01:39:00', '3', 'a');
INSERT INTO `srf_target_units` VALUES ('14', 'Practices', '1', '3', '2016-09-20 11:05:22', '3', 'a');
INSERT INTO `srf_target_units` VALUES ('15', 'Site-specific targeted CSA technologies/practices', '1', '3', '2016-09-26 20:31:44', '3', 'a');
INSERT INTO `srf_target_units` VALUES ('16', 'Subnational public/private initiatives', '1', '3', '2016-10-03 05:58:06', '3', 'a');
INSERT INTO `srf_target_units` VALUES ('17', 'Technologies', '1', '3', '2016-10-09 15:24:28', '3', 'a');
