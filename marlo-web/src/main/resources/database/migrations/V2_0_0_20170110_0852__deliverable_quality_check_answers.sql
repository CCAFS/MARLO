SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for deliverable_quality_answers
-- ----------------------------
DROP TABLE IF EXISTS `deliverable_quality_answers`;
CREATE TABLE `deliverable_quality_answers` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` text,
  `is_active` tinyint(1) NOT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  `active_since` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `quality_created_by` (`created_by`),
  KEY `quiality_modified_by` (`modified_by`),
  CONSTRAINT `quality_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `quiality_modified_by` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of deliverable_quality_answers
-- ----------------------------
INSERT INTO `deliverable_quality_answers` VALUES ('1', 'Yes, but not documented', '1', '3', '3', null, '2017-01-10 08:20:08');
INSERT INTO `deliverable_quality_answers` VALUES ('2', 'Yes, and documented', '1', '3', '3', null, '2017-01-10 08:20:28');
INSERT INTO `deliverable_quality_answers` VALUES ('3', 'No', '1', '3', '3', null, '2017-01-10 08:20:42');