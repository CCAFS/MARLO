SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for deliverables
-- ----------------------------
DROP TABLE IF EXISTS `deliverables`;
CREATE TABLE `deliverables` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_id` bigint(20) NOT NULL,
  `type_id` bigint(20) DEFAULT NULL,
  `status_id` int(11) DEFAULT NULL,
  `name` text,
  `date_created` timestamp NULL DEFAULT NULL,
  `start_date` timestamp NULL DEFAULT NULL,
  `end_date` timestamp NULL DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `active_since` timestamp NULL DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `deliverable_project_fk` (`project_id`),
  KEY `deliverable_status_fk` (`status_id`),
  KEY `deliverable_type_fk` (`type_id`),
  KEY `deliverable_created_fk` (`created_by`),
  KEY `deliverable_modified_fk` (`modified_by`),
  CONSTRAINT `deliverable_created_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `deliverable_modified_fk` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`),
  CONSTRAINT `deliverable_project_fk` FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`),
  CONSTRAINT `deliverable_status_fk` FOREIGN KEY (`status_id`) REFERENCES `project_status` (`id`),
  CONSTRAINT `deliverable_type_fk` FOREIGN KEY (`type_id`) REFERENCES `deliverable_types` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of deliverables
-- ----------------------------
