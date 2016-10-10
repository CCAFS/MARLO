/*
Navicat MySQL Data Transfer

Source Server         : local
Source Server Version : 50626
Source Host           : localhost:3306
Source Database       : ccafs_marlo

Target Server Type    : MYSQL
Target Server Version : 50626
File Encoding         : 65001

Date: 2016-05-25 12:00:35
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `crf_cross_cutting_issues`
-- ----------------------------
DROP TABLE IF EXISTS `crf_cross_cutting_issues`;
CREATE TABLE `crf_cross_cutting_issues` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`name`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Table structure for `crf_idos`
-- ----------------------------
DROP TABLE IF EXISTS `crf_idos`;
CREATE TABLE `crf_idos` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`description`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`is_cross_cutting`  tinyint(1) NOT NULL ,
`cross_cutting_issue`  bigint(20) NULL DEFAULT NULL ,
PRIMARY KEY (`id`),
FOREIGN KEY (`cross_cutting_issue`) REFERENCES `crf_cross_cutting_issues` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
INDEX `cross_cutting_issue` (`cross_cutting_issue`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Table structure for `crf_slo_idos`
-- ----------------------------
DROP TABLE IF EXISTS `crf_slo_idos`;
CREATE TABLE `crf_slo_idos` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`slo_id`  bigint(20) NOT NULL ,
`ido_id`  bigint(20) NOT NULL ,
PRIMARY KEY (`id`),
FOREIGN KEY (`slo_id`) REFERENCES `crf_slos` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
FOREIGN KEY (`ido_id`) REFERENCES `crf_idos` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
INDEX `slo_id` (`slo_id`) USING BTREE ,
INDEX `ido_id` (`ido_id`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Table structure for `crf_slo_indicator_targets`
-- ----------------------------
DROP TABLE IF EXISTS `crf_slo_indicator_targets`;
CREATE TABLE `crf_slo_indicator_targets` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`indicador_id`  bigint(20) NOT NULL ,
`value`  decimal(10,2) NOT NULL ,
`year`  int(11) NOT NULL ,
`target_unit_id`  bigint(20) NOT NULL ,
PRIMARY KEY (`id`),
FOREIGN KEY (`target_unit_id`) REFERENCES `crf_target_units` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
FOREIGN KEY (`indicador_id`) REFERENCES `crf_slo_indicators` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
INDEX `target_unit_id` (`target_unit_id`) USING BTREE ,
INDEX `indicador_id` (`indicador_id`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Table structure for `crf_slo_indicators`
-- ----------------------------
DROP TABLE IF EXISTS `crf_slo_indicators`;
CREATE TABLE `crf_slo_indicators` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`title`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`slo_id`  bigint(20) NOT NULL ,
`description`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
PRIMARY KEY (`id`),
FOREIGN KEY (`slo_id`) REFERENCES `crf_slos` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
INDEX `slo_id` (`slo_id`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Table structure for `crf_slos`
-- ----------------------------
DROP TABLE IF EXISTS `crf_slos`;
CREATE TABLE `crf_slos` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`title`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`description`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Table structure for `crf_sub_idos`
-- ----------------------------
DROP TABLE IF EXISTS `crf_sub_idos`;
CREATE TABLE `crf_sub_idos` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`description`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`ido_id`  bigint(20) NOT NULL ,
PRIMARY KEY (`id`),
FOREIGN KEY (`ido_id`) REFERENCES `crf_idos` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
INDEX `ido_id` (`ido_id`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Table structure for `crf_target_units`
-- ----------------------------
DROP TABLE IF EXISTS `crf_target_units`;
CREATE TABLE `crf_target_units` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`name`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`acronym`  varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=1

;

-- ----------------------------
-- Auto increment value for `crf_cross_cutting_issues`
-- ----------------------------
ALTER TABLE `crf_cross_cutting_issues` AUTO_INCREMENT=1;

-- ----------------------------
-- Auto increment value for `crf_idos`
-- ----------------------------
ALTER TABLE `crf_idos` AUTO_INCREMENT=1;

-- ----------------------------
-- Auto increment value for `crf_slo_idos`
-- ----------------------------
ALTER TABLE `crf_slo_idos` AUTO_INCREMENT=1;

-- ----------------------------
-- Auto increment value for `crf_slo_indicator_targets`
-- ----------------------------
ALTER TABLE `crf_slo_indicator_targets` AUTO_INCREMENT=1;

-- ----------------------------
-- Auto increment value for `crf_slo_indicators`
-- ----------------------------
ALTER TABLE `crf_slo_indicators` AUTO_INCREMENT=1;

-- ----------------------------
-- Auto increment value for `crf_slos`
-- ----------------------------
ALTER TABLE `crf_slos` AUTO_INCREMENT=1;

-- ----------------------------
-- Auto increment value for `crf_sub_idos`
-- ----------------------------
ALTER TABLE `crf_sub_idos` AUTO_INCREMENT=1;

-- ----------------------------
-- Auto increment value for `crf_target_units`
-- ----------------------------
ALTER TABLE `crf_target_units` AUTO_INCREMENT=1;
