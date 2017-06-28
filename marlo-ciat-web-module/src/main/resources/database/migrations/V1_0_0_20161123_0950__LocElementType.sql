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
  `is_active` tinyint(1) NOT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `active_since` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified_by` bigint(20) NOT NULL,
  `modification_justification` text NOT NULL,
  `is_scope` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `FK_loc_element_type_parent_idx` (`parent_id`) USING BTREE,
  KEY `fk_crp_id_loc_element_type` (`crp_id`) USING BTREE,
  KEY `fk_loc_element_types_created_by_users_id` (`created_by`) USING BTREE,
  KEY `fk_loc_element_types_modified_by_users_id` (`modified_by`) USING BTREE,
  CONSTRAINT `loc_element_types_ibfk_1` FOREIGN KEY (`crp_id`) REFERENCES `crps` (`id`),
  CONSTRAINT `loc_element_types_ibfk_2` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `loc_element_types_ibfk_3` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`),
  CONSTRAINT `loc_element_types_ibfk_4` FOREIGN KEY (`parent_id`) REFERENCES `loc_element_types` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of loc_element_types
-- ----------------------------
INSERT INTO `loc_element_types` VALUES ('1', 'Region', null, null, null, '1', null, '2016-06-27 11:57:16', '1', '', '0');
INSERT INTO `loc_element_types` VALUES ('2', 'Country', null, null, null, '1', null, '2016-06-27 11:57:16', '1', '', '0');
INSERT INTO `loc_element_types` VALUES ('3', 'Province', null, null, null, '1', null, '2016-06-27 11:57:16', '1', '', '0');
INSERT INTO `loc_element_types` VALUES ('4', 'District', null, null, null, '1', null, '2016-06-27 11:57:16', '1', '', '0');
INSERT INTO `loc_element_types` VALUES ('5', 'Ward', null, null, null, '1', null, '2016-06-27 11:57:16', '1', '', '0');
INSERT INTO `loc_element_types` VALUES ('6', 'Permanent agricultural trial site', null, null, null, '1', null, '2016-06-27 11:57:16', '1', '', '0');
INSERT INTO `loc_element_types` VALUES ('7', 'River Basin', null, null, null, '1', null, '2016-06-27 11:57:16', '1', '', '0');
INSERT INTO `loc_element_types` VALUES ('8', 'Village', null, null, null, '1', null, '2016-06-27 11:57:16', '1', '', '0');
INSERT INTO `loc_element_types` VALUES ('9', 'Household', null, null, null, '1', null, '2016-06-27 11:57:16', '1', '', '0');
INSERT INTO `loc_element_types` VALUES ('10', 'Climate smart village', null, '1', '1', '1', null, '2016-06-27 11:57:16', '1', '', '0');
INSERT INTO `loc_element_types` VALUES ('11', 'CCAFS Site', null, '1', '1', '1', null, '2016-06-27 11:57:16', '1', '', '0');
INSERT INTO `loc_element_types` VALUES ('12', 'Continent', null, null, null, '1', null, '2016-06-27 11:57:16', '1', '', '0');
INSERT INTO `loc_element_types` VALUES ('13', 'PIM Sites', null, '3', '0', '0', '1', '2016-06-27 13:33:41', '1', '', '0');
INSERT INTO `loc_element_types` VALUES ('14', 'Country', null, '7', '0', '1', '1087', '2016-08-11 06:30:36', '1087', '', '0');
INSERT INTO `loc_element_types` VALUES ('15', '', null, '1', '0', '1', '1106', '2016-08-30 11:28:33', '1106', '', '0');
INSERT INTO `loc_element_types` VALUES ('16', 'asd', null, '1', '0', '0', '1106', '2016-08-30 11:28:44', '1106', '', '0');
