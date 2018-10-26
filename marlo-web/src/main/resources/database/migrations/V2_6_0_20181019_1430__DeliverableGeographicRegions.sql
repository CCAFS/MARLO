SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for deliverable_geographic_regions
-- ----------------------------
DROP TABLE IF EXISTS `deliverable_geographic_regions`;
CREATE TABLE `deliverable_geographic_regions` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `deliverable_id` bigint(20) DEFAULT NULL,
  `loc_element_id` bigint(20) DEFAULT NULL,
  `id_phase` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `deliverable_id` (`deliverable_id`),
  KEY `loc_element_id` (`loc_element_id`),
  KEY `id_phase` (`id_phase`),
  CONSTRAINT `deliverable_geographic_regions_ibfk_1` FOREIGN KEY (`deliverable_id`) REFERENCES `deliverables` (`id`),
  CONSTRAINT `deliverable_geographic_regions_ibfk_2` FOREIGN KEY (`loc_element_id`) REFERENCES `loc_elements` (`id`),
  CONSTRAINT `deliverable_geographic_regions_ibfk_3` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;