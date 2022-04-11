CREATE TABLE `project_innovation_sdg_targets` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `innovation_id` bigint DEFAULT NULL,
  `sdg_target_id` int DEFAULT NULL,
  `id_phase` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `innovation_id` (`innovation_id`) USING BTREE,
  KEY `sdg_target_id` (`sdg_target_id`) USING BTREE,
  KEY `id_phase` (`id_phase`) USING BTREE,
  CONSTRAINT `innovation_sgd_target_ibfk_1` FOREIGN KEY (`innovation_id`) REFERENCES `project_innovations` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `innovation_sgd_target_ibfk_2` FOREIGN KEY (`sdg_target_id`) REFERENCES `sdg_targets` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `innovation_sgd_target_ibfk_3` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

alter table project_innovation_info add has_legacy_crp_contribution tinyint(1) DEFAULT NULL;

CREATE TABLE `project_policy_sdg_targets` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `policy_id` bigint DEFAULT NULL,
  `sdg_target_id` int DEFAULT NULL,
  `id_phase` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `policy_id` (`policy_id`) USING BTREE,
  KEY `sdg_target_id` (`sdg_target_id`) USING BTREE,
  KEY `id_phase` (`id_phase`) USING BTREE,
  CONSTRAINT `policy_sgd_target_ibfk_1` FOREIGN KEY (`policy_id`) REFERENCES `project_policies` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `policy_sgd_target_ibfk_2` FOREIGN KEY (`sdg_target_id`) REFERENCES `sdg_targets` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `policy_sgd_target_ibfk_3` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

alter table project_policy_info add has_legacy_crp_contribution tinyint(1) DEFAULT NULL;