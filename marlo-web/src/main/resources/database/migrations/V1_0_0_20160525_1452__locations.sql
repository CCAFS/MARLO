
SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `loc_element_types`
-- ----------------------------
DROP TABLE IF EXISTS `loc_element_types`;
CREATE TABLE `loc_element_types` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`name`  varchar(245) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`parent_id`  bigint(20) NULL DEFAULT NULL ,
PRIMARY KEY (`id`),
FOREIGN KEY (`parent_id`) REFERENCES `loc_element_types` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
INDEX `FK_loc_element_type_parent_idx` (`parent_id`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=12

;

-- ----------------------------
-- Table structure for `loc_elements`
-- ----------------------------
DROP TABLE IF EXISTS `loc_elements`;
CREATE TABLE `loc_elements` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`name`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`code`  varchar(45) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`parent_id`  bigint(20) NULL DEFAULT NULL ,
`element_type_id`  bigint(20) NOT NULL ,
`geoposition_id`  bigint(20) NULL DEFAULT NULL ,
`is_site_integration`  tinyint(1) NULL DEFAULT NULL ,
PRIMARY KEY (`id`),
FOREIGN KEY (`geoposition_id`) REFERENCES `loc_geopositions` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
FOREIGN KEY (`element_type_id`) REFERENCES `loc_element_types` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
INDEX `FK_loc_element_loc_element_type_id_idx` (`element_type_id`) USING BTREE ,
INDEX `FK_loc_elements_loc_geopositions_idx` (`geoposition_id`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=654

;

-- ----------------------------
-- Table structure for `loc_geopositions`
-- ----------------------------
DROP TABLE IF EXISTS `loc_geopositions`;
CREATE TABLE `loc_geopositions` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`latitude`  double NOT NULL ,
`longitude`  double NOT NULL ,
`parent_id`  bigint(20) NULL DEFAULT NULL ,
PRIMARY KEY (`id`),
FOREIGN KEY (`parent_id`) REFERENCES `loc_geopositions` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
INDEX `FK_loc_geoposition_loc_geopositon_idx` (`parent_id`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
AUTO_INCREMENT=838

;

-- ----------------------------
-- Auto increment value for `loc_element_types`
-- ----------------------------
ALTER TABLE `loc_element_types` AUTO_INCREMENT=12;

-- ----------------------------
-- Auto increment value for `loc_elements`
-- ----------------------------
ALTER TABLE `loc_elements` AUTO_INCREMENT=654;

-- ----------------------------
-- Auto increment value for `loc_geopositions`
-- ----------------------------
ALTER TABLE `loc_geopositions` AUTO_INCREMENT=838;
