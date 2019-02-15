SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for project_innovation_geographic_scopes
-- ----------------------------
DROP TABLE IF EXISTS `project_innovation_geographic_scopes`;
CREATE TABLE `project_innovation_geographic_scopes` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `project_innovation_id` bigint(20) NOT NULL,
  `rep_ind_geographic_scope_id` bigint(20) NOT NULL,
  `id_phase` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `project_innovation_id` (`project_innovation_id`),
  KEY `rep_ind_geographic_scope_id` (`rep_ind_geographic_scope_id`),
  KEY `id_phase` (`id_phase`),
  CONSTRAINT `project_innovation_geographic_scopes_ibfk_1` FOREIGN KEY (`project_innovation_id`) REFERENCES `project_innovations` (`id`),
  CONSTRAINT `project_innovation_geographic_scopes_ibfk_2` FOREIGN KEY (`rep_ind_geographic_scope_id`) REFERENCES `rep_ind_geographic_scopes` (`id`),
  CONSTRAINT `project_innovation_geographic_scopes_ibfk_3` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of project_innovation_geographic_scopes
-- ----------------------------
SET FOREIGN_KEY_CHECKS=1;
