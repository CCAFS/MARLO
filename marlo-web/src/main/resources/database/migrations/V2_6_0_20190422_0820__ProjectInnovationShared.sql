SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for project_innovation_shared
-- ----------------------------
DROP TABLE IF EXISTS `project_innovation_shared`;
CREATE TABLE `project_innovation_shared` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_id` bigint(20) DEFAULT NULL,
  `project_innovation_id` bigint(20) DEFAULT NULL,
  `id_phase` bigint(20) DEFAULT NULL,
  `created_by` bigint(20) NOT NULL,
  `active_since` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `expected_id` (`project_innovation_id`) USING BTREE,
  KEY `project_id` (`project_id`) USING BTREE,
  KEY `expected_study_projects_ibfk_3` (`id_phase`),
  KEY `FK_expected_study_projects_users_created_by` (`created_by`),
  KEY `FK_expected_study_projects_users_modified_by` (`modified_by`),
  CONSTRAINT `project_innovation_shared_ibfk_1` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT `project_innovation_shared_ibfk_2` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`) ON DELETE NO ACTION ON UPDATE CASCADE,
  CONSTRAINT `project_innovation_shared_ibfk_4` FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`),
  CONSTRAINT `project_innovation_shared_ibfk_5` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`),
  CONSTRAINT `project_innovation_shared_ibfk_6` FOREIGN KEY (`project_innovation_id`) REFERENCES `project_innovations` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3566 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of project_innovation_shared
-- ----------------------------
SET FOREIGN_KEY_CHECKS=1;
