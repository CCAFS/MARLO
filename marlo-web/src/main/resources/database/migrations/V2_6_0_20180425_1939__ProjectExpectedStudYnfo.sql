/*
Navicat MySQL Data Transfer

Source Server         : LOCALHOST
Source Server Version : 50718
Source Host           : localhost:3306
Source Database       : marlo_reporting

Target Server Type    : MYSQL
Target Server Version : 50718
File Encoding         : 65001

Date: 2018-04-25 14:26:31
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for project_expected_study_info
-- ----------------------------
DROP TABLE IF EXISTS `project_expected_study_info`;
CREATE TABLE `project_expected_study_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_expected_study_id` bigint(20) DEFAULT NULL,
  `id_phase` bigint(20) DEFAULT NULL,
  `study_type_id` bigint(20) DEFAULT NULL,
  `status` int(5) DEFAULT NULL,
  `title` text,
  `outcome_impact_statement` text,
  `is_contribution` tinyint(1) DEFAULT NULL,
  `rep_ind_policy_id` bigint(20) DEFAULT NULL,
  `policy_amount` double(30,2) DEFAULT NULL,
  `rep_ind_organization_type_id` bigint(20) DEFAULT NULL,
  `rep_ind_stage_process_id` bigint(20) DEFAULT NULL,
  `rep_ind_stage_study_id` bigint(20) DEFAULT NULL,
  `top_level_comments` text,
  `rep_ind_geographic_scope_id` bigint(20) DEFAULT NULL,
  `rep_ind_region_id` bigint(20) DEFAULT NULL,
  `scope_comments` text,
  `elaboration_outcome_impact_statement` text,
  `references` text,
  `references_file_id` bigint(20) DEFAULT NULL,
  `quantification` text,
  `describe_gender` text,
  `gender_focus_level_id` bigint(20) DEFAULT NULL,
  `describe_youth` text,
  `youth_focus_level_id` bigint(20) DEFAULT NULL,
  `describe_capdev` text,
  `capdev_focus_level_id` bigint(20) DEFAULT NULL,
  `other_cross_cutting_dimensions` text,
  `comunications_material` text,
  `outcome_file_id` bigint(20) DEFAULT NULL,
  `contacts` text,
  PRIMARY KEY (`id`),
  KEY `project_expected_study_id` (`project_expected_study_id`),
  KEY `study_type_id` (`study_type_id`),
  KEY `rep_ind_policy_id` (`rep_ind_policy_id`),
  KEY `rep_ind_organization_type_id` (`rep_ind_organization_type_id`),
  KEY `rep_ind_stage_process_id` (`rep_ind_stage_process_id`),
  KEY `rep_ind_stage_study_id` (`rep_ind_stage_study_id`),
  KEY `rep_ind_geographic_scope_id` (`rep_ind_geographic_scope_id`),
  KEY `rep_ind_region_id` (`rep_ind_region_id`),
  KEY `references_file_id` (`references_file_id`),
  KEY `gender_focus_level_id` (`gender_focus_level_id`),
  KEY `youth_focus_level_id` (`youth_focus_level_id`),
  KEY `capdev_focus_level_id` (`capdev_focus_level_id`),
  KEY `outcome_file_id` (`outcome_file_id`),
  KEY `id_phase` (`id_phase`),
  CONSTRAINT `project_expected_study_info_ibfk_1` FOREIGN KEY (`project_expected_study_id`) REFERENCES `project_expected_studies` (`id`),
  CONSTRAINT `project_expected_study_info_ibfk_10` FOREIGN KEY (`gender_focus_level_id`) REFERENCES `rep_ind_gender_youth_focus_levels` (`id`),
  CONSTRAINT `project_expected_study_info_ibfk_11` FOREIGN KEY (`youth_focus_level_id`) REFERENCES `rep_ind_gender_youth_focus_levels` (`id`),
  CONSTRAINT `project_expected_study_info_ibfk_12` FOREIGN KEY (`capdev_focus_level_id`) REFERENCES `rep_ind_gender_youth_focus_levels` (`id`),
  CONSTRAINT `project_expected_study_info_ibfk_13` FOREIGN KEY (`outcome_file_id`) REFERENCES `files` (`id`),
  CONSTRAINT `project_expected_study_info_ibfk_14` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`),
  CONSTRAINT `project_expected_study_info_ibfk_2` FOREIGN KEY (`study_type_id`) REFERENCES `study_types` (`id`),
  CONSTRAINT `project_expected_study_info_ibfk_3` FOREIGN KEY (`rep_ind_policy_id`) REFERENCES `rep_ind_policy_investiment_types` (`id`),
  CONSTRAINT `project_expected_study_info_ibfk_4` FOREIGN KEY (`rep_ind_organization_type_id`) REFERENCES `rep_ind_organization_types` (`id`),
  CONSTRAINT `project_expected_study_info_ibfk_5` FOREIGN KEY (`rep_ind_stage_process_id`) REFERENCES `rep_ind_stage_process` (`id`),
  CONSTRAINT `project_expected_study_info_ibfk_6` FOREIGN KEY (`rep_ind_stage_study_id`) REFERENCES `rep_ind_stage_studies` (`id`),
  CONSTRAINT `project_expected_study_info_ibfk_7` FOREIGN KEY (`rep_ind_geographic_scope_id`) REFERENCES `rep_ind_geographic_scopes` (`id`),
  CONSTRAINT `project_expected_study_info_ibfk_8` FOREIGN KEY (`rep_ind_region_id`) REFERENCES `rep_ind_regions` (`id`),
  CONSTRAINT `project_expected_study_info_ibfk_9` FOREIGN KEY (`references_file_id`) REFERENCES `files` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

