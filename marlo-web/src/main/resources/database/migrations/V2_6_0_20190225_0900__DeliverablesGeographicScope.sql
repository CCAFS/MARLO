SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for deliverable_geographic_scopes
-- ----------------------------
DROP TABLE IF EXISTS `deliverable_geographic_scopes`;
CREATE TABLE `deliverable_geographic_scopes` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `deliverable_id` bigint(20) NOT NULL,
  `rep_ind_geographic_scope_id` bigint(20) NOT NULL,
  `id_phase` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `deliverable_id` (`deliverable_id`),
  KEY `rep_ind_geographic_scope_id` (`rep_ind_geographic_scope_id`),
  KEY `id_phase` (`id_phase`),
  CONSTRAINT `deliverable_geographic_scopes_ibfk_1` FOREIGN KEY (`deliverable_id`) REFERENCES `deliverables` (`id`),
  CONSTRAINT `deliverable_geographic_scopes_ibfk_2` FOREIGN KEY (`rep_ind_geographic_scope_id`) REFERENCES `rep_ind_geographic_scopes` (`id`),
  CONSTRAINT `deliverable_geographic_scopes_ibfk_3` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of deliverable_geographic_scopes
-- ----------------------------
SET FOREIGN_KEY_CHECKS=1;
