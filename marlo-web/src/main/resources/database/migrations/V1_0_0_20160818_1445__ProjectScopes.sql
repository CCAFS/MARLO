START TRANSACTION;

/*
Navicat MySQL Data Transfer

Source Server         : local
Source Server Version : 50617
Source Host           : localhost:3306
Source Database       : ccafs_marlo

Target Server Type    : MYSQL
Target Server Version : 50617
File Encoding         : 65001

Date: 2016-08-18 14:45:23
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `project_scopes`
-- ----------------------------
DROP TABLE IF EXISTS `project_scopes`;
CREATE TABLE `project_scopes` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_id` bigint(20) NOT NULL,
  `loc_element__type_id` bigint(20) NOT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `active_since` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_by` bigint(20) NOT NULL,
  `modified_by` bigint(20) NOT NULL,
  `modification_justification` text NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_project_locations_users_created_by` (`created_by`) USING BTREE,
  KEY `fk_project_locations_users_modified_by` (`modified_by`) USING BTREE,
  KEY `fk_project_locations_projects_idx` (`project_id`) USING BTREE,
  KEY `project_scopes_ibfk_1` (`loc_element__type_id`),
  CONSTRAINT `project_scopes_ibfk_1` FOREIGN KEY (`loc_element__type_id`) REFERENCES `loc_element_types` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `project_scopes_ibfk_2` FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2054 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of project_scopes
-- ----------------------------

COMMIT;