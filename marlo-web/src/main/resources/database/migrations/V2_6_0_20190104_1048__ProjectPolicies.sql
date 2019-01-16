SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for project_policies
-- ----------------------------
DROP TABLE IF EXISTS `project_policies`;
CREATE TABLE `project_policies` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_id` bigint(20) NOT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `active_since` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_by` bigint(20) NOT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `project_policy_created_fk` (`created_by`),
  KEY `project_policy_modified_fk` (`modified_by`),
  KEY `project_policy_project_fk` (`project_id`),
  CONSTRAINT `project_policy_created_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `project_policy_modified_fk` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`),
  CONSTRAINT `project_policy_project_fk` FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of project_policies
-- ----------------------------
SET FOREIGN_KEY_CHECKS=1;
