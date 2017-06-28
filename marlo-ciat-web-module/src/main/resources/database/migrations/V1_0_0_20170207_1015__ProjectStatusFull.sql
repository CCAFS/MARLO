SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for project_status
-- ----------------------------
DROP TABLE IF EXISTS `project_status`;
CREATE TABLE `project_status` (
  `id` int(11) NOT NULL,
  `name` text,
  `is_active` tinyint(4) NOT NULL,
  `active_since` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `project_status_created_fk` (`created_by`),
  KEY `project_status_modified_fk` (`modified_by`),
  CONSTRAINT `project_status_created_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `project_status_modified_fk` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of project_status
-- ----------------------------
INSERT INTO `project_status` VALUES ('2', 'On Going', '1', '2017-02-07 10:06:40', '3', '3', ' ');
INSERT INTO `project_status` VALUES ('3', 'Complete', '1', '2017-02-07 10:07:05', '3', '3', ' ');
INSERT INTO `project_status` VALUES ('4', 'Extended', '1', '2017-02-07 10:07:17', '3', '3', ' ');
INSERT INTO `project_status` VALUES ('5', 'Cancelled', '1', '2017-02-07 10:07:31', '3', '3', ' ');
