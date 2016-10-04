/*
Navicat MySQL Data Transfer

Source Server         : local
Source Server Version : 50626
Source Host           : localhost:3306
Source Database       : ccafs_marlo

Target Server Type    : MYSQL
Target Server Version : 50626
File Encoding         : 65001

Date: 2016-05-26 09:09:42
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `crp_assumptions`
-- ----------------------------
DROP TABLE IF EXISTS `crp_assumptions`;
CREATE TABLE `crp_assumptions` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`description`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`crp_outcome_sub_idos_id`  bigint(20) NOT NULL ,
PRIMARY KEY (`id`),
FOREIGN KEY (`crp_outcome_sub_idos_id`) REFERENCES `crp_outcome_sub_idos` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
INDEX `crp_outcome_sub_idos_id` (`crp_outcome_sub_idos_id`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Records of crp_assumptions
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for `crp_cluster_of_activities`
-- ----------------------------
DROP TABLE IF EXISTS `crp_cluster_of_activities`;
CREATE TABLE `crp_cluster_of_activities` (
`id`  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`description`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`crp_program_id`  bigint(20) NOT NULL ,
PRIMARY KEY (`id`),
FOREIGN KEY (`crp_program_id`) REFERENCES `crp_programs` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
INDEX `crp_program_id` (`crp_program_id`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci

;

-- ----------------------------
-- Records of crp_cluster_of_activities
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for `crp_milestones`
-- ----------------------------
DROP TABLE IF EXISTS `crp_milestones`;
CREATE TABLE `crp_milestones` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`title`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`year`  int(11) NOT NULL ,
`value`  decimal(10,2) NOT NULL ,
`target_unit_id`  bigint(20) NOT NULL ,
`crp_program_outcome_id`  bigint(20) NOT NULL ,
PRIMARY KEY (`id`),
FOREIGN KEY (`target_unit_id`) REFERENCES `srf_target_units` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
FOREIGN KEY (`crp_program_outcome_id`) REFERENCES `crp_program_outcomes` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
INDEX `target_unit_id` (`target_unit_id`) USING BTREE ,
INDEX `crp_program_outcome_id` (`crp_program_outcome_id`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Records of crp_milestones
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for `crp_outcome_sub_idos`
-- ----------------------------
DROP TABLE IF EXISTS `crp_outcome_sub_idos`;
CREATE TABLE `crp_outcome_sub_idos` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`contribution`  decimal(10,2) NOT NULL ,
`srf_sub_ido_id`  bigint(20) NOT NULL ,
`crp_program_outcome_id`  bigint(20) NOT NULL ,
PRIMARY KEY (`id`),
FOREIGN KEY (`srf_sub_ido_id`) REFERENCES `srf_sub_idos` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
FOREIGN KEY (`crp_program_outcome_id`) REFERENCES `crp_program_outcomes` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
INDEX `srf_sub_ido_id` (`srf_sub_ido_id`) USING BTREE ,
INDEX `crp_program_outcome_id` (`crp_program_outcome_id`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Records of crp_outcome_sub_idos
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for `crp_program_leaders`
-- ----------------------------
DROP TABLE IF EXISTS `crp_program_leaders`;
CREATE TABLE `crp_program_leaders` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`crp_program_id`  bigint(20) NOT NULL ,
`user_id`  bigint(20) NOT NULL ,
PRIMARY KEY (`id`),
FOREIGN KEY (`crp_program_id`) REFERENCES `crp_programs` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
INDEX `crp_program_id` (`crp_program_id`) USING BTREE ,
INDEX `user_id` (`user_id`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Records of crp_program_leaders
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for `crp_program_outcomes`
-- ----------------------------
DROP TABLE IF EXISTS `crp_program_outcomes`;
CREATE TABLE `crp_program_outcomes` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`description`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`year`  int(11) NOT NULL ,
`value`  decimal(10,2) NOT NULL ,
`target_unit_id`  bigint(20) NOT NULL ,
`crp_program_id`  bigint(20) NOT NULL ,
PRIMARY KEY (`id`),
FOREIGN KEY (`target_unit_id`) REFERENCES `srf_target_units` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
FOREIGN KEY (`crp_program_id`) REFERENCES `crp_programs` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
INDEX `target_unit_id` (`target_unit_id`) USING BTREE ,
INDEX `crp_program_id` (`crp_program_id`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Records of crp_program_outcomes
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for `crp_programs`
-- ----------------------------
DROP TABLE IF EXISTS `crp_programs`;
CREATE TABLE `crp_programs` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`name`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`acronym`  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`crp_id`  bigint(20) NOT NULL ,
`program_type`  int(11) NOT NULL ,
PRIMARY KEY (`id`),
FOREIGN KEY (`crp_id`) REFERENCES `crps` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
INDEX `crp_programs_ibfk_1` (`crp_id`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Records of crp_programs
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for `crp_sites_leaders`
-- ----------------------------
DROP TABLE IF EXISTS `crp_sites_leaders`;
CREATE TABLE `crp_sites_leaders` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`crp_site_integration`  bigint(20) NOT NULL ,
`user_id`  bigint(20) NOT NULL ,
PRIMARY KEY (`id`),
FOREIGN KEY (`crp_site_integration`) REFERENCES `crps_sites_integration` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
INDEX `crp_site_integration` (`crp_site_integration`) USING BTREE ,
INDEX `user_id` (`user_id`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Records of crp_sites_leaders
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for `crp_sub_idos_contributions`
-- ----------------------------
DROP TABLE IF EXISTS `crp_sub_idos_contributions`;
CREATE TABLE `crp_sub_idos_contributions` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`crp_id`  bigint(20) NOT NULL ,
`srf_sub_ido_id`  bigint(20) NOT NULL ,
PRIMARY KEY (`id`),
FOREIGN KEY (`crp_id`) REFERENCES `crps` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
FOREIGN KEY (`srf_sub_ido_id`) REFERENCES `srf_sub_idos` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
INDEX `crp_id` (`crp_id`) USING BTREE ,
INDEX `srf_sub_ido_id` (`srf_sub_ido_id`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Records of crp_sub_idos_contributions
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for `crps_sites_integration`
-- ----------------------------
DROP TABLE IF EXISTS `crps_sites_integration`;
CREATE TABLE `crps_sites_integration` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`location_id`  bigint(20) NOT NULL ,
`crp_id`  bigint(20) NOT NULL ,
PRIMARY KEY (`id`),
FOREIGN KEY (`location_id`) REFERENCES `loc_elements` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
FOREIGN KEY (`crp_id`) REFERENCES `crps` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
INDEX `location_id` (`location_id`) USING BTREE ,
INDEX `crp_id` (`crp_id`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Records of crps_sites_integration
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Auto increment value for `crp_assumptions`
-- ----------------------------
ALTER TABLE `crp_assumptions` AUTO_INCREMENT=1;

-- ----------------------------
-- Auto increment value for `crp_milestones`
-- ----------------------------
ALTER TABLE `crp_milestones` AUTO_INCREMENT=1;

-- ----------------------------
-- Auto increment value for `crp_outcome_sub_idos`
-- ----------------------------
ALTER TABLE `crp_outcome_sub_idos` AUTO_INCREMENT=1;

-- ----------------------------
-- Auto increment value for `crp_program_leaders`
-- ----------------------------
ALTER TABLE `crp_program_leaders` AUTO_INCREMENT=1;

-- ----------------------------
-- Auto increment value for `crp_program_outcomes`
-- ----------------------------
ALTER TABLE `crp_program_outcomes` AUTO_INCREMENT=1;

-- ----------------------------
-- Auto increment value for `crp_programs`
-- ----------------------------
ALTER TABLE `crp_programs` AUTO_INCREMENT=1;

-- ----------------------------
-- Auto increment value for `crp_sites_leaders`
-- ----------------------------
ALTER TABLE `crp_sites_leaders` AUTO_INCREMENT=1;

-- ----------------------------
-- Auto increment value for `crp_sub_idos_contributions`
-- ----------------------------
ALTER TABLE `crp_sub_idos_contributions` AUTO_INCREMENT=1;

-- ----------------------------
-- Auto increment value for `crps_sites_integration`
-- ----------------------------
ALTER TABLE `crps_sites_integration` AUTO_INCREMENT=1;
