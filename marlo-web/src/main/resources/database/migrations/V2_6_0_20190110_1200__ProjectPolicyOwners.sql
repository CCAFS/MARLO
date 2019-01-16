SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for project_policy_owners
-- ----------------------------
DROP TABLE IF EXISTS `project_policy_owners`;
CREATE TABLE `project_policy_owners` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_policy_id` bigint(20) DEFAULT NULL,
  `id_phase` bigint(20) DEFAULT NULL,
  `rep_ind_policy_type_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `project_policy_id` (`project_policy_id`),
  KEY `id_phase` (`id_phase`),
  KEY `rep_ind_policy_type_id` (`rep_ind_policy_type_id`),
  CONSTRAINT `project_policy_owners_ibfk_1` FOREIGN KEY (`project_policy_id`) REFERENCES `project_policies` (`id`),
  CONSTRAINT `project_policy_owners_ibfk_2` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`),
  CONSTRAINT `project_policy_owners_ibfk_3` FOREIGN KEY (`rep_ind_policy_type_id`) REFERENCES `rep_ind_policy_types` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of project_policy_owners
-- ----------------------------
SET FOREIGN_KEY_CHECKS=1;
