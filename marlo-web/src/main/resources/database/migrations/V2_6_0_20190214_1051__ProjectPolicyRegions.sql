SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for project_policy_regions
-- ----------------------------
DROP TABLE IF EXISTS `project_policy_regions`;
CREATE TABLE `project_policy_regions` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `project_policy_id` bigint(20) NOT NULL,
  `id_region` bigint(20) NOT NULL,
  `id_phase` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `project_policy_id` (`project_policy_id`),
  KEY `id_region` (`id_region`),
  KEY `id_phase` (`id_phase`),
  CONSTRAINT `project_policy_regions_ibfk_1` FOREIGN KEY (`project_policy_id`) REFERENCES `project_policies` (`id`),
  CONSTRAINT `project_policy_regions_ibfk_2` FOREIGN KEY (`id_region`) REFERENCES `loc_elements` (`id`),
  CONSTRAINT `project_policy_regions_ibfk_3` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of project_policy_regions
-- ----------------------------
SET FOREIGN_KEY_CHECKS=1;
