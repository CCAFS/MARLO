SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for project_innovation_countries
-- ----------------------------
DROP TABLE IF EXISTS `project_innovation_countries`;
CREATE TABLE `project_innovation_countries` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `project_innovation_id` bigint(20) NOT NULL,
  `id_country` bigint(20) NOT NULL,
  `id_phase` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `project_innovation_id` (`project_innovation_id`),
  KEY `id_country` (`id_country`),
  KEY `id_phase` (`id_phase`),
  CONSTRAINT `project_innovation_countries_ibfk_1` FOREIGN KEY (`project_innovation_id`) REFERENCES `project_innovations` (`id`),
  CONSTRAINT `project_innovation_countries_ibfk_2` FOREIGN KEY (`id_country`) REFERENCES `loc_elements` (`id`),
  CONSTRAINT `project_innovation_countries_ibfk_3` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



-- ----------------------------
-- Table structure for project_innovation_deliverables
-- ----------------------------
DROP TABLE IF EXISTS `project_innovation_deliverables`;
CREATE TABLE `project_innovation_deliverables` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_innovation_id` bigint(20) DEFAULT NULL,
  `id_phase` bigint(20) DEFAULT NULL,
  `deliverable_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `project_innovation_id` (`project_innovation_id`),
  KEY `id_phase` (`id_phase`),
  KEY `deliverable_id` (`deliverable_id`),
  CONSTRAINT `project_innovation_deliverables_ibfk_1` FOREIGN KEY (`project_innovation_id`) REFERENCES `project_innovations` (`id`),
  CONSTRAINT `project_innovation_deliverables_ibfk_2` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`),
  CONSTRAINT `project_innovation_deliverables_ibfk_3` FOREIGN KEY (`deliverable_id`) REFERENCES `deliverables` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for project_innovation_organizations
-- ----------------------------
DROP TABLE IF EXISTS `project_innovation_organizations`;
CREATE TABLE `project_innovation_organizations` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_innovation_id` bigint(20) DEFAULT NULL,
  `id_phase` bigint(20) DEFAULT NULL,
  `rep_ind_organization_type_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `project_innovation_id` (`project_innovation_id`),
  KEY `id_phase` (`id_phase`),
  KEY `rep_ind_organization_type_id` (`rep_ind_organization_type_id`),
  CONSTRAINT `project_innovation_organizations_ibfk_1` FOREIGN KEY (`project_innovation_id`) REFERENCES `project_innovations` (`id`),
  CONSTRAINT `project_innovation_organizations_ibfk_2` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`),
  CONSTRAINT `project_innovation_organizations_ibfk_3` FOREIGN KEY (`rep_ind_organization_type_id`) REFERENCES `rep_ind_organization_types` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of project_innovation_organizations
-- ----------------------------
