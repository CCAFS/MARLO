/*
Navicat MySQL Data Transfer

Source Server         : local
Source Server Version : 50617
Source Host           : localhost:3306
Source Database       : ccafs_marlo

Target Server Type    : MYSQL
Target Server Version : 50617
File Encoding         : 65001

Date: 2016-08-17 15:46:11
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `project_cluster_activities`
-- ----------------------------
DROP TABLE IF EXISTS `project_cluster_activities`;
CREATE TABLE `project_cluster_activities` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_id` bigint(20) NOT NULL,
  `cluster_activity_id` bigint(20) NOT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `active_since` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_by` bigint(20) NOT NULL,
  `modified_by` bigint(20) NOT NULL,
  `modification_justification` text NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_project_focus_project_id_idx` (`project_id`) USING BTREE,
  KEY `fk_project_focuses_users_created_by` (`created_by`) USING BTREE,
  KEY `fk_project_focuses_users_modified__by` (`modified_by`) USING BTREE,
  KEY `cluster_activity_id` (`cluster_activity_id`),
  CONSTRAINT `project_cluster_activities_ibfk_5` FOREIGN KEY (`cluster_activity_id`) REFERENCES `crp_cluster_of_activities` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `project_cluster_activities_ibfk_2` FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `project_cluster_activities_ibfk_3` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `project_cluster_activities_ibfk_4` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=461 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of project_cluster_activities
-- ----------------------------
