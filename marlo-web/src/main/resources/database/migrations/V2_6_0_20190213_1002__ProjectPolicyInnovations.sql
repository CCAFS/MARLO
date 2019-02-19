SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for project_policy_innovations
-- ----------------------------
DROP TABLE IF EXISTS `project_policy_innovations`;
CREATE TABLE `project_policy_innovations` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_policy_id` bigint(20) DEFAULT NULL,
  `project_innovation_id` bigint(20) DEFAULT NULL,
  `id_phase` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `project_policy_id` (`project_policy_id`),
  KEY `project_innovation_id` (`project_innovation_id`),
  KEY `id_phase` (`id_phase`),
  CONSTRAINT `project_policy_innovations_ibfk_1` FOREIGN KEY (`project_policy_id`) REFERENCES `project_policies` (`id`),
  CONSTRAINT `project_policy_innovations_ibfk_2` FOREIGN KEY (`project_innovation_id`) REFERENCES `project_innovations` (`id`),
  CONSTRAINT `project_policy_innovations_ibfk_3` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of project_policy_innovations
-- ----------------------------
SET FOREIGN_KEY_CHECKS=1;
