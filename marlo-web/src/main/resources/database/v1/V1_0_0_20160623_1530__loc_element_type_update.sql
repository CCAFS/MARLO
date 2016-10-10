SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for loc_element_types
-- ----------------------------
DROP TABLE IF EXISTS `loc_element_types`;
CREATE TABLE `loc_element_types` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(245) DEFAULT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  `crp_id` bigint(20) DEFAULT NULL,
  `has_coordinates` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_loc_element_type_parent_idx` (`parent_id`) USING BTREE,
  KEY `fk_crp_id_loc_element_type` (`crp_id`),
  CONSTRAINT `fk_crp_id_loc_element_type` FOREIGN KEY (`crp_id`) REFERENCES `crps` (`id`),
  CONSTRAINT `loc_element_types_ibfk_1` FOREIGN KEY (`parent_id`) REFERENCES `loc_element_types` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of loc_element_types
-- ----------------------------
INSERT INTO `loc_element_types` VALUES ('1', 'Region', null, null, null);
INSERT INTO `loc_element_types` VALUES ('2', 'Country', null, null, null);
INSERT INTO `loc_element_types` VALUES ('3', 'Province', null, null, null);
INSERT INTO `loc_element_types` VALUES ('4', 'District', null, null, null);
INSERT INTO `loc_element_types` VALUES ('5', 'Ward', null, null, null);
INSERT INTO `loc_element_types` VALUES ('6', 'Permanent agricultural trial site', null, null, null);
INSERT INTO `loc_element_types` VALUES ('7', 'River Basin', null, null, null);
INSERT INTO `loc_element_types` VALUES ('8', 'Village', null, null, null);
INSERT INTO `loc_element_types` VALUES ('9', 'Household', null, null, null);
INSERT INTO `loc_element_types` VALUES ('10', 'Climate smart village', null, null, null);
INSERT INTO `loc_element_types` VALUES ('11', 'CCAFS Site', null, '1', '1');
INSERT INTO `loc_element_types` VALUES ('12', 'Continent', null, null, null);
