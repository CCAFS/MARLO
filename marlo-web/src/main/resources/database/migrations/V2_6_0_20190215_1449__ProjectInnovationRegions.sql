SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for project_innovation_regions
-- ----------------------------
DROP TABLE IF EXISTS `project_innovation_regions`;
CREATE TABLE `project_innovation_regions` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `project_innovation_id` bigint(20) NOT NULL,
  `id_region` bigint(20) NOT NULL,
  `id_phase` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `project_innovation_id` (`project_innovation_id`),
  KEY `id_region` (`id_region`),
  KEY `id_phase` (`id_phase`),
  CONSTRAINT `project_innovation_regions_ibfk_1` FOREIGN KEY (`project_innovation_id`) REFERENCES `project_innovations` (`id`),
  CONSTRAINT `project_innovation_regions_ibfk_2` FOREIGN KEY (`id_region`) REFERENCES `loc_elements` (`id`),
  CONSTRAINT `project_innovation_regions_ibfk_3` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of project_innovation_regions
-- ----------------------------
SET FOREIGN_KEY_CHECKS=1;
