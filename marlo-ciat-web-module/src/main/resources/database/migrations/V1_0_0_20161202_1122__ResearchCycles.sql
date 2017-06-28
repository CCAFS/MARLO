SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for research_cycles
-- ----------------------------
DROP TABLE IF EXISTS `research_cycles`;
CREATE TABLE `research_cycles` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` text,
  `acronym` text,
  `is_active` tinyint(1) NOT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  `active_since` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `cycle_created_by_fk` (`created_by`),
  KEY `cycle_modified_by_fk` (`modified_by`),
  CONSTRAINT `cycle_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `cycle_modified_by_fk` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of research_cycles
-- ----------------------------
INSERT INTO `research_cycles` VALUES ('1', 'Impact Pathway', 'IP', '1', '3', '3', ' ', '2016-12-02 10:57:31');
INSERT INTO `research_cycles` VALUES ('2', 'Monitoring', 'M&E', '1', '3', '3', ' ', '2016-12-02 10:57:50');
