SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for project_center_outcomes
-- ----------------------------
DROP TABLE IF EXISTS `project_center_outcomes`;
CREATE TABLE `project_center_outcomes` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_id` bigint(20) DEFAULT NULL,
  `center_outcome_id` int(11) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL,
  `active_since` timestamp NULL DEFAULT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  `id_phase` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `project_id` (`project_id`),
  KEY `center_outcome_id` (`center_outcome_id`),
  KEY `created_by` (`created_by`),
  KEY `modified_by` (`modified_by`),
  KEY `id_phase` (`id_phase`),
  CONSTRAINT `project_center_outcomes_ibfk_1` FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`),
  CONSTRAINT `project_center_outcomes_ibfk_2` FOREIGN KEY (`center_outcome_id`) REFERENCES `center_outcomes` (`id`),
  CONSTRAINT `project_center_outcomes_ibfk_3` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `project_center_outcomes_ibfk_4` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`),
  CONSTRAINT `project_center_outcomes_ibfk_5` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


