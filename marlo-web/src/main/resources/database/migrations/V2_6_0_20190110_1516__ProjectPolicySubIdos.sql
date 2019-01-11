SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for project_policy_sub_idos
-- ----------------------------
DROP TABLE IF EXISTS `project_policy_sub_idos`;
CREATE TABLE `project_policy_sub_idos` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_policy_id` bigint(20) DEFAULT NULL,
  `sub_ido_id` bigint(20) DEFAULT NULL,
  `id_phase` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `project_policy_id` (`project_policy_id`),
  KEY `sub_ido_id` (`sub_ido_id`),
  KEY `id_phase` (`id_phase`),
  CONSTRAINT `project_policy_sub_idos_ibfk_1` FOREIGN KEY (`project_policy_id`) REFERENCES `project_policies` (`id`),
  CONSTRAINT `project_policy_sub_idos_ibfk_2` FOREIGN KEY (`sub_ido_id`) REFERENCES `srf_sub_idos` (`id`),
  CONSTRAINT `project_policy_sub_idos_ibfk_3` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of project_policy_sub_idos
-- ----------------------------
SET FOREIGN_KEY_CHECKS=1;
