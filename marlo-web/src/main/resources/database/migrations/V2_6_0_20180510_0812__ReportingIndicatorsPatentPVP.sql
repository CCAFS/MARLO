SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for rep_ind_filling_types
-- ----------------------------
DROP TABLE IF EXISTS `rep_ind_filling_types`;
CREATE TABLE `rep_ind_filling_types` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` text,
  `description` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of rep_ind_filling_types
-- ----------------------------
INSERT INTO `rep_ind_filling_types` VALUES ('1', 'Provisional / non-provisional', '');
INSERT INTO `rep_ind_filling_types` VALUES ('2', 'National direct, national designated', '');
INSERT INTO `rep_ind_filling_types` VALUES ('3', 'Multi-territory', '');
-- ----------------------------
-- Table structure for rep_ind_patent_statuses
-- ----------------------------
DROP TABLE IF EXISTS `rep_ind_patent_statuses`;
CREATE TABLE `rep_ind_patent_statuses` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` text,
  `description` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of rep_ind_patent_statuses
-- ----------------------------
INSERT INTO `rep_ind_patent_statuses` VALUES ('1', 'Filled', '');
INSERT INTO `rep_ind_patent_statuses` VALUES ('2', 'Pending', '');
INSERT INTO `rep_ind_patent_statuses` VALUES ('3', 'Matured to non-provisional', '');
INSERT INTO `rep_ind_patent_statuses` VALUES ('4', 'Discontinued', '');
INSERT INTO `rep_ind_patent_statuses` VALUES ('5', 'Registered', '');
INSERT INTO `rep_ind_patent_statuses` VALUES ('6', 'Lapsed', '');

-- Alter table structure for deliverable_intellectual_assets

ALTER TABLE `deliverable_intellectual_assets`
ADD COLUMN `rep_ind_filling_type_id`  bigint(20) NULL DEFAULT NULL AFTER `title`,
ADD COLUMN `rep_ind_patent_status_id`  bigint(20) NULL DEFAULT NULL AFTER `rep_ind_filling_type_id`,
ADD COLUMN `patent_type`  int(11) NULL DEFAULT NULL COMMENT 'Application = 1\r\nRegistration = 2' AFTER `rep_ind_patent_status_id`,
ADD COLUMN `variety_name`  text NULL AFTER `patent_type`,
ADD COLUMN `status`  int(11) NULL DEFAULT NULL AFTER `variety_name`,
ADD COLUMN `loc_element_id`  bigint(20) NULL DEFAULT NULL AFTER `status`,
ADD COLUMN `app_reg_number`  double NULL DEFAULT NULL AFTER `loc_element_id`,
ADD COLUMN `breeder_crop`  text NULL AFTER `app_reg_number`,
ADD COLUMN `date_filling`  date NULL DEFAULT NULL AFTER `breeder_crop`,
ADD COLUMN `date_registration`  date NULL DEFAULT NULL AFTER `date_filling`,
ADD COLUMN `date_expiry`  date NULL DEFAULT NULL AFTER `date_registration`,
ADD INDEX `idx_intellectual_assets_filling_type_id` (`rep_ind_filling_type_id`) USING BTREE ,
ADD INDEX `idx_intellectual_assets_patent_status_id` (`rep_ind_patent_status_id`) USING BTREE ,
ADD INDEX `idx_intellectual_assets_loc_element_id` (`loc_element_id`) USING BTREE ;

ALTER TABLE `deliverable_intellectual_assets` ADD CONSTRAINT `intellectual_assets_ibfk_3` FOREIGN KEY (`rep_ind_filling_type_id`) REFERENCES `rep_ind_filling_types` (`id`)  ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `deliverable_intellectual_assets` ADD CONSTRAINT `intellectual_assets_ibfk_4` FOREIGN KEY (`rep_ind_patent_status_id`) REFERENCES `rep_ind_patent_statuses` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `deliverable_intellectual_assets` ADD CONSTRAINT `intellectual_assets_ibfk_5` FOREIGN KEY (`loc_element_id`) REFERENCES `loc_elements` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;


