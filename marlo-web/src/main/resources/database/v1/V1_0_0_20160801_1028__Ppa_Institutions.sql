/*
Navicat MySQL Data Transfer

Source Server         : local
Source Server Version : 50617
Source Host           : localhost:3306
Source Database       : ccafs_marlo

Target Server Type    : MYSQL
Target Server Version : 50617
File Encoding         : 65001

Date: 2016-08-01 10:24:53
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `crp_ppa_partners`
-- ----------------------------
DROP TABLE IF EXISTS `crp_ppa_partners`;
CREATE TABLE `crp_ppa_partners` (
  `crp_id` bigint(20) NOT NULL,
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `institution_id` bigint(20) NOT NULL,
  `is_active` tinyint(1) NOT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `active_since` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified_by` bigint(20) NOT NULL,
  `modification_justification` text NOT NULL,
  PRIMARY KEY (`id`),
  KEY `crp_id_ppa_fk` (`crp_id`) USING BTREE,
  KEY `institution_id_ppa_fk` (`institution_id`) USING BTREE,
  KEY `fk_crp_ppa_partners_created_by_users_id` (`created_by`) USING BTREE,
  KEY `fk_crp_ppa_partners_modified_by_users_id` (`modified_by`) USING BTREE,
  CONSTRAINT `crp_ppa_partners_ibfk_1` FOREIGN KEY (`crp_id`) REFERENCES `crps` (`id`),
  CONSTRAINT `crp_ppa_partners_ibfk_2` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `crp_ppa_partners_ibfk_3` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`),
  CONSTRAINT `crp_ppa_partners_ibfk_4` FOREIGN KEY (`institution_id`) REFERENCES `institutions` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=68 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of crp_ppa_partners
-- ----------------------------
INSERT INTO `crp_ppa_partners` VALUES ('1', '5', '1', '1', '3', '2016-08-01 10:23:47', '3', '');
INSERT INTO `crp_ppa_partners` VALUES ('1', '6', '5', '1', '3', '2016-08-01 10:23:47', '3', '');
INSERT INTO `crp_ppa_partners` VALUES ('1', '7', '45', '1', '3', '2016-08-01 10:23:47', '3', '');
INSERT INTO `crp_ppa_partners` VALUES ('1', '8', '46', '1', '3', '2016-08-01 10:23:47', '3', '');
INSERT INTO `crp_ppa_partners` VALUES ('1', '9', '49', '1', '3', '2016-08-01 10:23:47', '3', '');
INSERT INTO `crp_ppa_partners` VALUES ('1', '10', '50', '1', '3', '2016-08-01 10:23:47', '3', '');
INSERT INTO `crp_ppa_partners` VALUES ('1', '11', '51', '1', '3', '2016-08-01 10:23:47', '3', '');
INSERT INTO `crp_ppa_partners` VALUES ('1', '12', '52', '1', '3', '2016-08-01 10:23:47', '3', '');
INSERT INTO `crp_ppa_partners` VALUES ('1', '13', '66', '1', '3', '2016-08-01 10:23:47', '3', '');
INSERT INTO `crp_ppa_partners` VALUES ('1', '14', '67', '1', '3', '2016-08-01 10:23:47', '3', '');
INSERT INTO `crp_ppa_partners` VALUES ('1', '15', '88', '1', '3', '2016-08-01 10:23:47', '3', '');
INSERT INTO `crp_ppa_partners` VALUES ('1', '16', '89', '1', '3', '2016-08-01 10:23:47', '3', '');
INSERT INTO `crp_ppa_partners` VALUES ('1', '17', '99', '1', '3', '2016-08-01 10:23:47', '3', '');
INSERT INTO `crp_ppa_partners` VALUES ('1', '18', '100', '1', '3', '2016-08-01 10:23:47', '3', '');
INSERT INTO `crp_ppa_partners` VALUES ('1', '19', '103', '1', '3', '2016-08-01 10:23:47', '3', '');
INSERT INTO `crp_ppa_partners` VALUES ('1', '20', '114', '1', '3', '2016-08-01 10:23:47', '3', '');
INSERT INTO `crp_ppa_partners` VALUES ('1', '21', '115', '1', '3', '2016-08-01 10:23:47', '3', '');
INSERT INTO `crp_ppa_partners` VALUES ('1', '22', '134', '1', '3', '2016-08-01 10:23:47', '3', '');
INSERT INTO `crp_ppa_partners` VALUES ('1', '23', '172', '1', '3', '2016-08-01 10:23:47', '3', '');
INSERT INTO `crp_ppa_partners` VALUES ('1', '24', '650', '1', '3', '2016-08-01 10:23:47', '3', '');
INSERT INTO `crp_ppa_partners` VALUES ('1', '25', '680', '1', '3', '2016-08-01 10:23:47', '3', '');
INSERT INTO `crp_ppa_partners` VALUES ('1', '26', '746', '1', '3', '2016-08-01 10:23:47', '3', '');
INSERT INTO `crp_ppa_partners` VALUES ('1', '27', '775', '1', '3', '2016-08-01 10:23:47', '3', '');
INSERT INTO `crp_ppa_partners` VALUES ('1', '28', '1042', '1', '3', '2016-08-01 10:23:47', '3', '');
INSERT INTO `crp_ppa_partners` VALUES ('1', '29', '1053', '1', '3', '2016-08-01 10:23:47', '3', '');
INSERT INTO `crp_ppa_partners` VALUES ('1', '30', '1085', '1', '3', '2016-08-01 10:23:47', '3', '');
INSERT INTO `crp_ppa_partners` VALUES ('1', '31', '1099', '1', '3', '2016-08-01 10:23:47', '3', '');
INSERT INTO `crp_ppa_partners` VALUES ('1', '32', '1116', '1', '3', '2016-08-01 10:23:47', '3', '');
INSERT INTO `crp_ppa_partners` VALUES ('1', '33', '1143', '1', '3', '2016-08-01 10:23:47', '3', '');
INSERT INTO `crp_ppa_partners` VALUES ('1', '34', '1176', '1', '3', '2016-08-01 10:23:47', '3', '');
INSERT INTO `crp_ppa_partners` VALUES ('1', '35', '1200', '1', '3', '2016-08-01 10:23:47', '3', '');
INSERT INTO `crp_ppa_partners` VALUES ('1', '36', '1201', '1', '3', '2016-08-01 10:23:47', '3', '');
INSERT INTO `crp_ppa_partners` VALUES ('1', '37', '1202', '1', '3', '2016-08-01 10:23:47', '3', '');
INSERT INTO `crp_ppa_partners` VALUES ('1', '38', '1203', '1', '3', '2016-08-01 10:23:47', '3', '');
INSERT INTO `crp_ppa_partners` VALUES ('1', '39', '1204', '1', '3', '2016-08-01 10:23:47', '3', '');
INSERT INTO `crp_ppa_partners` VALUES ('1', '40', '1205', '1', '3', '2016-08-01 10:23:47', '3', '');
INSERT INTO `crp_ppa_partners` VALUES ('1', '41', '1206', '1', '3', '2016-08-01 10:23:47', '3', '');
INSERT INTO `crp_ppa_partners` VALUES ('1', '42', '1207', '1', '3', '2016-08-01 10:23:47', '3', '');
INSERT INTO `crp_ppa_partners` VALUES ('1', '43', '1208', '1', '3', '2016-08-01 10:23:47', '3', '');
INSERT INTO `crp_ppa_partners` VALUES ('1', '44', '1209', '1', '3', '2016-08-01 10:23:47', '3', '');
INSERT INTO `crp_ppa_partners` VALUES ('1', '45', '1212', '1', '3', '2016-08-01 10:23:47', '3', '');
INSERT INTO `crp_ppa_partners` VALUES ('1', '46', '1213', '1', '3', '2016-08-01 10:23:47', '3', '');
INSERT INTO `crp_ppa_partners` VALUES ('1', '47', '1214', '1', '3', '2016-08-01 10:23:47', '3', '');
INSERT INTO `crp_ppa_partners` VALUES ('1', '48', '1215', '1', '3', '2016-08-01 10:23:47', '3', '');
INSERT INTO `crp_ppa_partners` VALUES ('1', '49', '1216', '1', '3', '2016-08-01 10:23:47', '3', '');
INSERT INTO `crp_ppa_partners` VALUES ('1', '50', '1217', '1', '3', '2016-08-01 10:23:47', '3', '');
INSERT INTO `crp_ppa_partners` VALUES ('1', '51', '1219', '1', '3', '2016-08-01 10:23:47', '3', '');
INSERT INTO `crp_ppa_partners` VALUES ('1', '52', '1220', '1', '3', '2016-08-01 10:23:47', '3', '');
INSERT INTO `crp_ppa_partners` VALUES ('1', '53', '1221', '1', '3', '2016-08-01 10:23:47', '3', '');
INSERT INTO `crp_ppa_partners` VALUES ('1', '54', '1222', '1', '3', '2016-08-01 10:23:47', '3', '');
INSERT INTO `crp_ppa_partners` VALUES ('1', '55', '1223', '1', '3', '2016-08-01 10:23:47', '3', '');
INSERT INTO `crp_ppa_partners` VALUES ('1', '56', '1232', '1', '3', '2016-08-01 10:23:47', '3', '');
INSERT INTO `crp_ppa_partners` VALUES ('1', '57', '1239', '1', '3', '2016-08-01 10:23:47', '3', '');
