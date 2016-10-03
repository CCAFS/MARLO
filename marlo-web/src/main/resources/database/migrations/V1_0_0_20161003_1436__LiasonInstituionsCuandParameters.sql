/*
Navicat MySQL Data Transfer

Source Server         : local
Source Server Version : 50617
Source Host           : localhost:3306
Source Database       : ccafs_marlo

Target Server Type    : MYSQL
Target Server Version : 50617
File Encoding         : 65001

Date: 2016-10-03 14:36:51
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `crp_parameters`
-- ----------------------------
DROP TABLE IF EXISTS `crp_parameters`;
CREATE TABLE `crp_parameters` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `crp_id` bigint(20) NOT NULL,
  `key` varchar(500) NOT NULL,
  `value` text NOT NULL,
  `is_active` tinyint(1) NOT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `active_since` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified_by` bigint(20) NOT NULL,
  `modification_justification` text NOT NULL,
  PRIMARY KEY (`id`),
  KEY `crp_id` (`crp_id`) USING BTREE,
  KEY `fk_crp_parameters_created_by_users_id` (`created_by`) USING BTREE,
  KEY `fk_crp_parameters_modified_by_users_id` (`modified_by`) USING BTREE,
  CONSTRAINT `crp_parameters_ibfk_1` FOREIGN KEY (`crp_id`) REFERENCES `crps` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `crp_parameters_ibfk_2` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `crp_parameters_ibfk_3` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=90 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of crp_parameters
-- ----------------------------
INSERT INTO `crp_parameters` VALUES ('2', '1', 'crp_pmu_rol', '14', '1', null, '2016-06-07 14:08:40', '1', '');
INSERT INTO `crp_parameters` VALUES ('9', '1', 'crp_has_regions', 'true', '1', '844', '2016-10-03 14:26:36', '844', '');
INSERT INTO `crp_parameters` VALUES ('11', '1', 'crp_fpl_rol', '12', '1', null, '2016-06-07 14:08:40', '1', '');
INSERT INTO `crp_parameters` VALUES ('12', '1', 'crp_rpl_rol', '11', '1', null, '2016-06-07 14:08:40', '1', '');
INSERT INTO `crp_parameters` VALUES ('13', '1', 'crp_admin_active', 'true', '1', '1', '2016-06-23 15:27:53', '1', ' ');
INSERT INTO `crp_parameters` VALUES ('14', '1', 'crp_impPath_active', 'true', '1', '1', '2016-06-23 15:27:53', '1', ' ');
INSERT INTO `crp_parameters` VALUES ('15', '1', 'crp_cl_rol', '18', '1', '1', '2016-06-23 15:27:53', '1', ' ');
INSERT INTO `crp_parameters` VALUES ('16', '1', 'crp_sl_rol', '15', '1', '1', '2016-06-23 15:27:54', '1', '');
INSERT INTO `crp_parameters` VALUES ('19', '5', 'crp_pmu_rol', '20', '1', null, '2016-06-07 14:08:40', '1', '');
INSERT INTO `crp_parameters` VALUES ('20', '5', 'crp_has_regions', 'false', '1', '988', '2016-07-22 09:00:32', '988', '');
INSERT INTO `crp_parameters` VALUES ('21', '5', 'crp_fpl_rol', '22', '1', null, '2016-06-07 14:08:40', '1', '');
INSERT INTO `crp_parameters` VALUES ('22', '5', 'crp_rpl_rol', '21', '1', null, '2016-06-07 14:08:40', '1', '');
INSERT INTO `crp_parameters` VALUES ('23', '5', 'crp_admin_active', 'true', '1', '1', '2016-06-23 15:27:53', '1', '');
INSERT INTO `crp_parameters` VALUES ('24', '5', 'crp_impPath_active', 'true', '1', '1', '2016-06-23 15:27:53', '1', '');
INSERT INTO `crp_parameters` VALUES ('25', '5', 'crp_cl_rol', '24', '1', '1', '2016-06-23 15:27:53', '1', '');
INSERT INTO `crp_parameters` VALUES ('26', '5', 'crp_sl_rol', '23', '1', '1', '2016-06-23 15:27:54', '1', '');
INSERT INTO `crp_parameters` VALUES ('27', '3', 'crp_pmu_rol', '27', '1', null, '2016-06-07 14:08:40', '1', '');
INSERT INTO `crp_parameters` VALUES ('28', '3', 'crp_has_regions', 'false', '1', '1061', '2016-08-03 12:11:49', '1061', '');
INSERT INTO `crp_parameters` VALUES ('29', '3', 'crp_fpl_rol', '29', '1', null, '2016-06-07 14:08:40', '1', '');
INSERT INTO `crp_parameters` VALUES ('30', '3', 'crp_rpl_rol', '28', '1', null, '2016-06-07 14:08:40', '1', '');
INSERT INTO `crp_parameters` VALUES ('31', '3', 'crp_admin_active', 'true', '1', '1', '2016-06-23 15:27:53', '1', '');
INSERT INTO `crp_parameters` VALUES ('32', '3', 'crp_impPath_active', 'true', '1', '1', '2016-06-23 15:27:53', '1', '');
INSERT INTO `crp_parameters` VALUES ('33', '3', 'crp_cl_rol', '31', '1', '1', '2016-06-23 15:27:53', '1', '');
INSERT INTO `crp_parameters` VALUES ('34', '3', 'crp_sl_rol', '30', '1', '1', '2016-06-23 15:27:54', '1', '');
INSERT INTO `crp_parameters` VALUES ('35', '4', 'crp_pmu_rol', '33', '1', null, '2016-06-07 14:08:40', '1', '');
INSERT INTO `crp_parameters` VALUES ('36', '4', 'crp_has_regions', 'false', '1', '844', '2016-06-23 16:00:06', '844', '');
INSERT INTO `crp_parameters` VALUES ('37', '4', 'crp_fpl_rol', '35', '1', null, '2016-06-07 14:08:40', '1', '');
INSERT INTO `crp_parameters` VALUES ('38', '4', 'crp_rpl_rol', '34', '1', null, '2016-06-07 14:08:40', '1', '');
INSERT INTO `crp_parameters` VALUES ('39', '4', 'crp_admin_active', 'true', '1', '1', '2016-06-23 15:27:53', '1', '');
INSERT INTO `crp_parameters` VALUES ('40', '4', 'crp_impPath_active', 'true', '1', '1', '2016-06-23 15:27:53', '1', '');
INSERT INTO `crp_parameters` VALUES ('41', '4', 'crp_cl_rol', '37', '1', '1', '2016-06-23 15:27:53', '1', '');
INSERT INTO `crp_parameters` VALUES ('42', '4', 'crp_sl_rol', '36', '1', '1', '2016-06-23 15:27:54', '1', '');
INSERT INTO `crp_parameters` VALUES ('43', '1', 'crp_pl_rol', '7', '1', '3', '2016-08-08 16:01:32', '3', '');
INSERT INTO `crp_parameters` VALUES ('44', '1', 'crp_pc_rol', '9', '1', '3', '2016-08-08 16:01:32', '3', '');
INSERT INTO `crp_parameters` VALUES ('45', '1', 'crp_lessons_active', '1', '1', '3', '2016-08-08 16:01:33', '3', '');
INSERT INTO `crp_parameters` VALUES ('46', '7', 'crp_pmu_rol', '38', '1', '3', '2016-08-05 15:00:00', '3', ' ');
INSERT INTO `crp_parameters` VALUES ('47', '7', 'crp_has_regions', 'false', '1', '1087', '2016-08-11 06:27:45', '1087', '');
INSERT INTO `crp_parameters` VALUES ('48', '7', 'crp_fpl_rol', '40', '1', '3', '2016-08-05 15:00:00', '3', ' ');
INSERT INTO `crp_parameters` VALUES ('49', '7', 'crp_rpl_rol', '39', '1', '3', '2016-08-05 15:00:00', '3', ' ');
INSERT INTO `crp_parameters` VALUES ('50', '7', 'crp_admin_active', 'true', '1', '3', '2016-08-05 15:00:00', '3', ' ');
INSERT INTO `crp_parameters` VALUES ('51', '7', 'crp_impPath_active', 'true', '1', '3', '2016-08-05 15:00:00', '3', ' ');
INSERT INTO `crp_parameters` VALUES ('52', '7', 'crp_cl_rol', '42', '1', '3', '2016-08-05 15:00:00', '3', ' ');
INSERT INTO `crp_parameters` VALUES ('53', '7', 'crp_sl_rol', '41', '1', '3', '2016-08-05 15:00:00', '3', ' ');
INSERT INTO `crp_parameters` VALUES ('54', '1', 'crp_planning_active', '1', '1', '3', '2016-08-08 16:01:34', '3', '');
INSERT INTO `crp_parameters` VALUES ('55', '1', 'crp_reporting_active', '0', '1', '3', '2016-08-08 16:01:34', '3', '');
INSERT INTO `crp_parameters` VALUES ('56', '1', 'crp_reporting_year', '2015', '1', '3', '2016-08-08 16:01:34', '3', '');
INSERT INTO `crp_parameters` VALUES ('57', '1', 'crp_planning_year', '2017', '1', '3', '2016-08-08 16:01:34', '3', '');
INSERT INTO `crp_parameters` VALUES ('58', '3', 'crp_planning_active', '1', '1', '3', '2016-09-27 11:57:36', '3', ' ');
INSERT INTO `crp_parameters` VALUES ('59', '3', 'crp_reporting_active', '0', '1', '3', '2016-09-27 11:57:36', '3', ' ');
INSERT INTO `crp_parameters` VALUES ('60', '3', 'crp_reporting_year', '2015', '1', '3', '2016-09-27 11:57:36', '3', ' ');
INSERT INTO `crp_parameters` VALUES ('61', '3', 'crp_planning_year', '2017', '1', '3', '2016-09-27 11:57:36', '3', ' ');
INSERT INTO `crp_parameters` VALUES ('62', '4', 'crp_planning_active', '1', '1', '3', '2016-09-27 11:59:46', '3', ' ');
INSERT INTO `crp_parameters` VALUES ('63', '4', 'crp_reporting_active', '0', '1', '3', '2016-09-27 11:59:46', '3', ' ');
INSERT INTO `crp_parameters` VALUES ('64', '4', 'crp_reporting_year', '2015', '1', '3', '2016-09-27 11:59:46', '3', ' ');
INSERT INTO `crp_parameters` VALUES ('65', '4', 'crp_planning_year', '2017', '1', '3', '2016-09-27 11:59:46', '3', ' ');
INSERT INTO `crp_parameters` VALUES ('66', '5', 'crp_planning_active', '1', '1', '3', '2016-09-27 11:59:46', '3', ' ');
INSERT INTO `crp_parameters` VALUES ('67', '5', 'crp_reporting_active', '0', '1', '3', '2016-09-27 11:59:46', '3', ' ');
INSERT INTO `crp_parameters` VALUES ('68', '5', 'crp_reporting_year', '2015', '1', '3', '2016-09-27 11:59:47', '3', ' ');
INSERT INTO `crp_parameters` VALUES ('69', '5', 'crp_planning_year', '2017', '1', '3', '2016-09-27 11:59:47', '3', ' ');
INSERT INTO `crp_parameters` VALUES ('70', '7', 'crp_planning_active', '1', '1', '3', '2016-09-27 11:59:47', '3', ' ');
INSERT INTO `crp_parameters` VALUES ('71', '7', 'crp_reporting_active', '0', '1', '3', '2016-09-27 11:59:47', '3', ' ');
INSERT INTO `crp_parameters` VALUES ('72', '7', 'crp_reporting_year', '2015', '1', '3', '2016-09-27 11:59:47', '3', ' ');
INSERT INTO `crp_parameters` VALUES ('73', '7', 'crp_planning_year', '2017', '1', '3', '2016-09-27 11:59:47', '3', ' ');
INSERT INTO `crp_parameters` VALUES ('74', '1', 'crp_open_planing_date', '2016-09-25', '1', '3', '2016-09-28 09:30:16', '3', ' ');
INSERT INTO `crp_parameters` VALUES ('75', '1', 'crp_open_reporting_date', '2017-10-01', '1', '3', '2016-09-28 15:22:03', '3', ' ');
INSERT INTO `crp_parameters` VALUES ('76', '3', 'crp_open_planing_date', '2016-09-25', '1', '3', '2016-09-28 15:22:45', '3', ' ');
INSERT INTO `crp_parameters` VALUES ('77', '3', 'crp_open_reporting_date', '2017-10-01', '1', '3', '2016-09-28 15:22:46', '3', ' ');
INSERT INTO `crp_parameters` VALUES ('78', '4', 'crp_open_planing_date', '2016-09-25', '1', '3', '2016-09-28 15:23:25', '3', ' ');
INSERT INTO `crp_parameters` VALUES ('79', '4', 'crp_open_reporting_date', '2017-10-01', '1', '3', '2016-09-28 15:23:25', '3', ' ');
INSERT INTO `crp_parameters` VALUES ('80', '5', 'crp_open_planing_date', '2016-09-25', '1', '3', '2016-09-28 15:23:58', '3', ' ');
INSERT INTO `crp_parameters` VALUES ('81', '5', 'crp_open_reporting_date', '2017-09-25', '1', '3', '2016-09-28 15:23:58', '3', ' ');
INSERT INTO `crp_parameters` VALUES ('82', '7', 'crp_open_planing_date', '2015-09-25', '1', '3', '2016-09-28 15:24:28', '3', ' ');
INSERT INTO `crp_parameters` VALUES ('83', '7', 'crp_open_reporting_date', '2017-10-01', '1', '3', '2016-09-28 15:24:28', '3', ' ');
INSERT INTO `crp_parameters` VALUES ('85', '1', 'crp_cu', '1', '1', '3', '2016-10-03 10:40:00', '3', '');
INSERT INTO `crp_parameters` VALUES ('86', '3', 'crp_cu', '34', '1', '3', '2016-10-03 14:35:04', '3', '');
INSERT INTO `crp_parameters` VALUES ('87', '4', 'crp_cu', '35', '1', '3', '2016-10-03 14:35:13', '3', '');
INSERT INTO `crp_parameters` VALUES ('88', '5', 'crp_cu', '36', '1', '3', '2016-10-03 14:35:23', '3', '');
INSERT INTO `crp_parameters` VALUES ('89', '7', 'crp_cu', '37', '1', '3', '2016-10-03 14:35:31', '3', '');

-- ----------------------------
-- Table structure for `liaison_institutions`
-- ----------------------------
DROP TABLE IF EXISTS `liaison_institutions`;
CREATE TABLE `liaison_institutions` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `institution_id` bigint(20) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `acronym` varchar(255) DEFAULT NULL,
  `crp_program` bigint(20) DEFAULT NULL,
  `crp_id` bigint(20) DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `FK_liaison_institutions_institutions_idx` (`institution_id`) USING BTREE,
  KEY `crp_program` (`crp_program`) USING BTREE,
  KEY `crp_id` (`crp_id`) USING BTREE,
  CONSTRAINT `liaison_institutions_ibfk_1` FOREIGN KEY (`institution_id`) REFERENCES `institutions` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `liaison_institutions_ibfk_2` FOREIGN KEY (`crp_program`) REFERENCES `crp_programs` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `liaison_institutions_ibfk_3` FOREIGN KEY (`crp_id`) REFERENCES `crps` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of liaison_institutions
-- ----------------------------
INSERT INTO `liaison_institutions` VALUES ('1', '114', 'Coordinating Unit', 'CU', null, '1');
INSERT INTO `liaison_institutions` VALUES ('2', '46', 'Flagship 1', 'F1', '84', '1');
INSERT INTO `liaison_institutions` VALUES ('3', '100', 'Flagship 2', 'F2', '85', '1');
INSERT INTO `liaison_institutions` VALUES ('4', '1053', 'Flagship 3', 'F3', '86', '1');
INSERT INTO `liaison_institutions` VALUES ('5', '66', 'Flagship 4', 'F4', '87', '1');
INSERT INTO `liaison_institutions` VALUES ('6', '66', 'East Africa Region', 'RP EA', '89', '1');
INSERT INTO `liaison_institutions` VALUES ('7', '46', 'Latin America Region', 'RP LAM', '88', '1');
INSERT INTO `liaison_institutions` VALUES ('8', '172', 'South Asia Region', 'RP SAs', '92', '1');
INSERT INTO `liaison_institutions` VALUES ('9', '5', 'South East Asia Region', 'RP SEA', '91', '1');
INSERT INTO `liaison_institutions` VALUES ('10', '103', 'West Africa Region', 'RP WA', '90', '1');
INSERT INTO `liaison_institutions` VALUES ('11', '52', 'Africa Rice Center', 'AfricaRice', null, null);
INSERT INTO `liaison_institutions` VALUES ('12', '49', 'Bioversity International', 'BI', null, null);
INSERT INTO `liaison_institutions` VALUES ('13', '46', 'Centro Internacional de Agricultura Tropical', 'CIAT', null, null);
INSERT INTO `liaison_institutions` VALUES ('14', '115', 'Center for International Forestry Research', 'CIFOR', null, null);
INSERT INTO `liaison_institutions` VALUES ('15', '50', 'International Maize and Wheat Improvement Center', 'CIMMYT', null, null);
INSERT INTO `liaison_institutions` VALUES ('16', '67', 'Centro Internacional de la Papa', 'CIP', null, null);
INSERT INTO `liaison_institutions` VALUES ('17', '51', 'International Center for Agricultural Research in the Dry Areas', 'ICARDA', null, null);
INSERT INTO `liaison_institutions` VALUES ('18', '88', 'World Agroforestry Centre', 'ICRAF', null, null);
INSERT INTO `liaison_institutions` VALUES ('19', '103', 'International Crops Research Institute for the Semi-Arid Tropics', 'ICRISAT', null, null);
INSERT INTO `liaison_institutions` VALUES ('20', '89', 'International Food Policy Research Institute', 'IFPRI', null, null);
INSERT INTO `liaison_institutions` VALUES ('21', '45', 'International Institute of Tropical Agriculture', 'IITA', null, null);
INSERT INTO `liaison_institutions` VALUES ('22', '66', 'International Livestock Research Institute', 'ILRI', null, null);
INSERT INTO `liaison_institutions` VALUES ('23', '5', 'International Rice Research Institute', 'IRRI', null, null);
INSERT INTO `liaison_institutions` VALUES ('24', '172', 'International Water Management Institute', 'IWMI', null, null);
INSERT INTO `liaison_institutions` VALUES ('25', '99', 'WorldFish Center', 'WorldFish', null, null);
INSERT INTO `liaison_institutions` VALUES ('34', null, 'Coordinating Unit', 'CU', null, '3');
INSERT INTO `liaison_institutions` VALUES ('35', null, 'Coordinating Unit', 'CU', null, '4');
INSERT INTO `liaison_institutions` VALUES ('36', null, 'Coodrinating Unit', 'CU', null, '5');
INSERT INTO `liaison_institutions` VALUES ('37', null, 'Coodrinating Unit', 'CU', null, '7');
