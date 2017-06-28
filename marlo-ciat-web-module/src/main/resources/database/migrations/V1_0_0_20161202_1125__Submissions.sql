SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for submissions
-- ----------------------------
DROP TABLE IF EXISTS `submissions`;
CREATE TABLE `submissions` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `program_id` int(11) DEFAULT NULL,
  `user_id` bigint(20) NOT NULL,
  `cycle_id` bigint(20) DEFAULT NULL COMMENT 'Cycling period type.',
  `date_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'The date time when the report was made.',
  `modification_justification` mediumtext,
  `year` smallint(6) DEFAULT NULL COMMENT 'Year to which the report is happening.',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`) USING BTREE,
  KEY `program_id` (`program_id`),
  KEY `submissions_cycle_fk` (`cycle_id`),
  CONSTRAINT `submissions_cycle_fk` FOREIGN KEY (`cycle_id`) REFERENCES `research_cycles` (`id`),
  CONSTRAINT `submissions_program_fk` FOREIGN KEY (`program_id`) REFERENCES `research_programs` (`id`),
  CONSTRAINT `submissions_users_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of submissions
-- ----------------------------
