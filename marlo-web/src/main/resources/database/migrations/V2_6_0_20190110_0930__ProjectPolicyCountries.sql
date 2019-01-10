SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for project_policy_countries
-- ----------------------------
DROP TABLE IF EXISTS `project_policy_countries`;
CREATE TABLE `project_policy_countries` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `project_policy_id` bigint(20) NOT NULL,
  `id_country` bigint(20) NOT NULL,
  `id_phase` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `project_policy_id` (`project_policy_id`),
  KEY `id_country` (`id_country`),
  KEY `id_phase` (`id_phase`),
  CONSTRAINT `project_policy_countries_ibfk_1` FOREIGN KEY (`project_policy_id`) REFERENCES `project_policies` (`id`),
  CONSTRAINT `project_policy_countries_ibfk_2` FOREIGN KEY (`id_country`) REFERENCES `loc_elements` (`id`),
  CONSTRAINT `project_policy_countries_ibfk_3` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of project_policy_countries
-- ----------------------------
SET FOREIGN_KEY_CHECKS=1;
