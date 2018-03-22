SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for center_areas
-- ----------------------------
DROP TABLE IF EXISTS `center_areas`;
CREATE TABLE `center_areas` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` text NOT NULL COMMENT 'The Name of the research area such as DAPA',
  `acronym` varchar(50) NOT NULL COMMENT 'The short form or acronym of the research area e.g DAPA',
  `color` varchar(8) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `active_since` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  `global_unit_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_research_area_created_by` (`created_by`) USING BTREE,
  KEY `fk_research_area_modified_by` (`modified_by`) USING BTREE,
  KEY `center_areas_global_unit_fk` (`global_unit_id`) USING BTREE,
  CONSTRAINT `center_areas_ibfk_1` FOREIGN KEY (`global_unit_id`) REFERENCES `global_units` (`id`),
  CONSTRAINT `center_areas_ibfk_2` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `center_areas_ibfk_3` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of center_areas
-- ----------------------------
INSERT INTO `center_areas` VALUES ('1', 'Decision and Policy Analysis', 'DAPA', '#228051', '1', '2018-01-29 09:33:17', null, '3', null, '23');
INSERT INTO `center_areas` VALUES ('2', 'Agrobiodiversity', 'AgBIO', '#228051', '1', '2018-01-29 09:33:17', null, '3', null, '23');
INSERT INTO `center_areas` VALUES ('3', 'Soils and Landscapes for Sustainability', 'SOILS', '#228051', '1', '2018-01-29 09:33:17', null, '3', null, '23');
INSERT INTO `center_areas` VALUES ('4', 'Decision and Policy Analysis', 'DAPA', '#228051', '1', '2018-01-29 09:33:17', null, '3', '', '29');
INSERT INTO `center_areas` VALUES ('5', 'Agrobiodiversity', 'AgBIO', '#228051', '1', '2018-01-29 09:33:17', null, '3', '', '29');
INSERT INTO `center_areas` VALUES ('6', 'Soils and Landscapes for Sustainability', 'SOILS', '#228051', '1', '2018-01-29 09:33:17', null, '3', '', '29');
