SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for loc_element_types
-- ----------------------------
DROP TABLE IF EXISTS `loc_element_types`;
CREATE TABLE `loc_element_types` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(245) DEFAULT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_loc_element_type_parent_idx` (`parent_id`) USING BTREE,
  CONSTRAINT `loc_element_types_ibfk_1` FOREIGN KEY (`parent_id`) REFERENCES `loc_element_types` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of loc_element_types
-- ----------------------------
INSERT INTO `loc_element_types` VALUES ('1', 'Region', null);
INSERT INTO `loc_element_types` VALUES ('2', 'Country', null);
INSERT INTO `loc_element_types` VALUES ('3', 'Province', null);
INSERT INTO `loc_element_types` VALUES ('4', 'District', null);
INSERT INTO `loc_element_types` VALUES ('5', 'Ward', null);
INSERT INTO `loc_element_types` VALUES ('6', 'Permanent agricultural trial site', null);
INSERT INTO `loc_element_types` VALUES ('7', 'River Basin', null);
INSERT INTO `loc_element_types` VALUES ('8', 'Village', null);
INSERT INTO `loc_element_types` VALUES ('9', 'Household', null);
INSERT INTO `loc_element_types` VALUES ('10', 'Climate smart village', null);
INSERT INTO `loc_element_types` VALUES ('11', 'CCAFS Site', null);
INSERT INTO `loc_element_types` VALUES ('12', 'Continent', null);
