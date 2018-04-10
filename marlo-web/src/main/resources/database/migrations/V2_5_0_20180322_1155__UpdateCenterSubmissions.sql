SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for center_submissions
-- ----------------------------
DROP TABLE IF EXISTS `center_submissions`;
CREATE TABLE `center_submissions` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `program_id` int(11) DEFAULT NULL,
  `crp_program_id` bigint(20) DEFAULT NULL,
  `project_id` bigint(20) DEFAULT NULL,
  `capdev_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) NOT NULL,
  `cycle_id` bigint(20) DEFAULT NULL COMMENT 'Cycling period type.',
  `date_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'The date time when the report was made.',
  `modification_justification` mediumtext,
  `year` smallint(6) DEFAULT NULL COMMENT 'Year to which the report is happening.',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`) USING BTREE,
  KEY `program_id` (`program_id`) USING BTREE,
  KEY `submissions_cycle_fk` (`cycle_id`) USING BTREE,
  KEY `submissions_project_fk` (`project_id`) USING BTREE,
  KEY `submissions_capdev_fk` (`capdev_id`) USING BTREE,
  KEY `center_submissions_ibfk_6` (`crp_program_id`),
  CONSTRAINT `center_submissions_ibfk_1` FOREIGN KEY (`capdev_id`) REFERENCES `capacity_development` (`id`),
  CONSTRAINT `center_submissions_ibfk_2` FOREIGN KEY (`cycle_id`) REFERENCES `center_cycles` (`id`),
  CONSTRAINT `center_submissions_ibfk_3` FOREIGN KEY (`program_id`) REFERENCES `center_programs` (`id`),
  CONSTRAINT `center_submissions_ibfk_4` FOREIGN KEY (`project_id`) REFERENCES `center_projects` (`id`),
  CONSTRAINT `center_submissions_ibfk_5` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `center_submissions_ibfk_6` FOREIGN KEY (`crp_program_id`) REFERENCES `crp_programs` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of center_submissions
-- ----------------------------
INSERT INTO `center_submissions` VALUES ('4', '8', '137', null, null, '1137', '1', '2018-01-17 07:46:13', null, '2018');
INSERT INTO `center_submissions` VALUES ('5', '10', '139', null, null, '1137', '1', '2018-02-12 16:22:42', null, '2018');
INSERT INTO `center_submissions` VALUES ('6', '4', '133', null, null, '1447', '1', '2018-02-13 14:25:39', null, '2018');
INSERT INTO `center_submissions` VALUES ('7', '2', '131', null, null, '1137', '1', '2018-03-01 14:49:24', null, '2018');
INSERT INTO `center_submissions` VALUES ('8', '3', '132', null, null, '1137', '1', '2018-03-13 10:55:16', null, '2018');
