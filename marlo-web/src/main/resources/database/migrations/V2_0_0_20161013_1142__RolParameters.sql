/*
Navicat MySQL Data Transfer

Source Server         : local
Source Server Version : 50617
Source Host           : localhost:3306
Source Database       : ccafs_marlo

Target Server Type    : MYSQL
Target Server Version : 50617
File Encoding         : 65001

Date: 2016-10-13 11:40:15
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
) ENGINE=InnoDB AUTO_INCREMENT=103 DEFAULT CHARSET=utf8;

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
INSERT INTO `crp_parameters` VALUES ('90', '3', 'crp_lessons_active', '1', '1', '3', '2016-10-12 11:30:41', '3', ' ');
INSERT INTO `crp_parameters` VALUES ('91', '4', 'crp_lessons_active', '1', '1', '3', '2016-10-12 11:30:41', '3', ' ');
INSERT INTO `crp_parameters` VALUES ('92', '5', 'crp_lessons_active', '1', '1', '3', '2016-10-12 11:30:41', '3', ' ');
INSERT INTO `crp_parameters` VALUES ('93', '7', 'crp_lessons_active', '1', '1', '3', '2016-10-12 11:30:41', '3', ' ');
INSERT INTO `crp_parameters` VALUES ('95', '3', 'crp_pl_rol', '52', '1', '3', '2016-10-13 11:37:42', '3', ' ');
INSERT INTO `crp_parameters` VALUES ('96', '4', 'crp_pl_rol', '57', '1', '3', '2016-10-13 11:39:03', '3', ' ');
INSERT INTO `crp_parameters` VALUES ('97', '5', 'crp_pl_rol', '58', '1', '3', '2016-10-13 11:39:04', '3', ' ');
INSERT INTO `crp_parameters` VALUES ('98', '7', 'crp_pl_rol', '59', '1', '3', '2016-10-13 11:39:04', '3', ' ');
INSERT INTO `crp_parameters` VALUES ('99', '3', 'crp_pc_rol', '53', '1', '3', '2016-10-13 11:39:40', '3', ' ');
INSERT INTO `crp_parameters` VALUES ('100', '4', 'crp_pc_rol', '54', '1', '3', '2016-10-13 11:39:40', '3', ' ');
INSERT INTO `crp_parameters` VALUES ('101', '5', 'crp_pc_rol', '55', '1', '3', '2016-10-13 11:39:40', '3', ' ');
INSERT INTO `crp_parameters` VALUES ('102', '7', 'crp_pc_rol', '56', '1', '3', '2016-10-13 11:39:40', '3', ' ');

-- ----------------------------
-- Table structure for `roles`
-- ----------------------------
DROP TABLE IF EXISTS `roles`;
CREATE TABLE `roles` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` text NOT NULL,
  `acronym` varchar(50) NOT NULL,
  `crp_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `crp_id` (`crp_id`) USING BTREE,
  CONSTRAINT `roles_ibfk_1` FOREIGN KEY (`crp_id`) REFERENCES `crps` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=60 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of roles
-- ----------------------------
INSERT INTO `roles` VALUES ('1', 'CCFAS Admin', 'Admin', '1');
INSERT INTO `roles` VALUES ('2', 'Management Liaison', 'ML', '1');
INSERT INTO `roles` VALUES ('4', 'Contact point', 'CP', '1');
INSERT INTO `roles` VALUES ('7', 'Project leader', 'PL', '1');
INSERT INTO `roles` VALUES ('8', 'Guest', 'G', '1');
INSERT INTO `roles` VALUES ('9', 'Project coordinator', 'PC', '1');
INSERT INTO `roles` VALUES ('10', 'Finance person', 'FM', '1');
INSERT INTO `roles` VALUES ('11', 'Regional Program Leaders', 'RPL', '1');
INSERT INTO `roles` VALUES ('12', 'Flagship Leaders', 'FPL', '1');
INSERT INTO `roles` VALUES ('13', 'External Evaluator', 'E', '1');
INSERT INTO `roles` VALUES ('14', 'Program Management Unit', 'PMU', '1');
INSERT INTO `roles` VALUES ('15', 'Site Integration Leader', 'SL', '1');
INSERT INTO `roles` VALUES ('16', 'Data Manager', 'DM', '1');
INSERT INTO `roles` VALUES ('17', 'Super Admin', 'SuperAdmin', '1');
INSERT INTO `roles` VALUES ('18', 'Cluster Leader', 'CL', '1');
INSERT INTO `roles` VALUES ('19', 'Admin(A4NH)', 'Admin', '5');
INSERT INTO `roles` VALUES ('20', 'Program Management Unit', 'PMU', '5');
INSERT INTO `roles` VALUES ('21', 'Regional Program Leaders', 'RPL', '5');
INSERT INTO `roles` VALUES ('22', 'Flagship Leaders', 'FPL', '5');
INSERT INTO `roles` VALUES ('23', 'Site Integration Leader', 'SL', '5');
INSERT INTO `roles` VALUES ('24', 'Cluster Leader', 'CL', '5');
INSERT INTO `roles` VALUES ('26', 'Admin(PIM)', 'Admin', '3');
INSERT INTO `roles` VALUES ('27', 'Program Management Unit', 'PMU', '3');
INSERT INTO `roles` VALUES ('28', 'Regional Program Leaders', 'RPL', '3');
INSERT INTO `roles` VALUES ('29', 'Flagship Leaders', 'FPL', '3');
INSERT INTO `roles` VALUES ('30', 'Site Integration Leader', 'SL', '3');
INSERT INTO `roles` VALUES ('31', 'Cluster Leader', 'CL', '3');
INSERT INTO `roles` VALUES ('32', 'Admin(WLE)', 'Admin', '4');
INSERT INTO `roles` VALUES ('33', 'Program Management Unit', 'PMU', '4');
INSERT INTO `roles` VALUES ('34', 'Regional Program Leaders', 'RPL', '4');
INSERT INTO `roles` VALUES ('35', 'Flagship Leaders', 'FPL', '4');
INSERT INTO `roles` VALUES ('36', 'Site Integration Leader', 'SL', '4');
INSERT INTO `roles` VALUES ('37', 'Cluster Leader', 'CL', '4');
INSERT INTO `roles` VALUES ('38', 'Program Management Unit', 'PMU', '7');
INSERT INTO `roles` VALUES ('39', 'Regional Program Leaders', 'RPL', '7');
INSERT INTO `roles` VALUES ('40', 'Flagship Leaders', 'FPL', '7');
INSERT INTO `roles` VALUES ('41', 'Site Integration Leader', 'SL', '7');
INSERT INTO `roles` VALUES ('42', 'Cluster Leader', 'CL', '7');
INSERT INTO `roles` VALUES ('43', 'Admin(Livesotckfish)', 'Admin', '7');
INSERT INTO `roles` VALUES ('48', 'Management Liaison', 'ML', '3');
INSERT INTO `roles` VALUES ('49', 'Management Liaison', 'ML', '4');
INSERT INTO `roles` VALUES ('50', 'Management Liaison', 'ML', '5');
INSERT INTO `roles` VALUES ('51', 'Management Liaison', 'ML', '7');
INSERT INTO `roles` VALUES ('52', 'Project Leader', 'PL', '3');
INSERT INTO `roles` VALUES ('53', 'Project coordinator', 'PC', '3');
INSERT INTO `roles` VALUES ('54', 'Project coordinator', 'PC', '4');
INSERT INTO `roles` VALUES ('55', 'Project coordinator', 'PC', '5');
INSERT INTO `roles` VALUES ('56', 'Project coordinator', 'PC', '7');
INSERT INTO `roles` VALUES ('57', 'Project Leader', 'PL', '4');
INSERT INTO `roles` VALUES ('58', 'Project Leader', 'PL', '5');
INSERT INTO `roles` VALUES ('59', 'Project Leader', 'PL', '7');
