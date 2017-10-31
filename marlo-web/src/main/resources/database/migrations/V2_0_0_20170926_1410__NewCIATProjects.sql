SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for center_projects
-- ----------------------------
DROP TABLE IF EXISTS `center_projects`;
CREATE TABLE `center_projects` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `program_id` int(11) NOT NULL,
  `name` text,
  `suggested_name` text,
  `description` text,
  `status_id` int(11) DEFAULT NULL,
  `date_created` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `start_date` timestamp NULL DEFAULT NULL,
  `end_date` timestamp NULL DEFAULT NULL,
  `project_leader_id` bigint(20) DEFAULT NULL,
  `contact_person_id` bigint(20) DEFAULT NULL,
  `is_global` tinyint(1) DEFAULT NULL,
  `is_region` tinyint(1) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL,
  `active_since` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `project_status_fk` (`status_id`) USING BTREE,
  KEY `project_leader_fk` (`project_leader_id`) USING BTREE,
  KEY `project_contact_fk` (`contact_person_id`) USING BTREE,
  KEY `project_created_fk` (`created_by`) USING BTREE,
  KEY `project_modified_fk` (`modified_by`) USING BTREE,
  KEY `project_program_fk` (`program_id`) USING BTREE,
  CONSTRAINT `center_projects_ibfk_1` FOREIGN KEY (`contact_person_id`) REFERENCES `users` (`id`),
  CONSTRAINT `center_projects_ibfk_2` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `center_projects_ibfk_3` FOREIGN KEY (`project_leader_id`) REFERENCES `users` (`id`),
  CONSTRAINT `center_projects_ibfk_4` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`),
  CONSTRAINT `center_projects_ibfk_5` FOREIGN KEY (`program_id`) REFERENCES `center_programs` (`id`),
  CONSTRAINT `center_projects_ibfk_6` FOREIGN KEY (`status_id`) REFERENCES `center_project_status` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of center_projects
-- ----------------------------
INSERT INTO `center_projects` VALUES ('1', '1', null, null, null, '2', '2017-09-25 14:57:13', '2017-09-25 14:57:13', null, null, null, null, null, '1', '2017-09-25 14:57:13', '1057', '1057', null);
