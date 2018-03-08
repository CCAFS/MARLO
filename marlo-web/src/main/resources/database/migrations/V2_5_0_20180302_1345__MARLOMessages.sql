SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for marlo_messages
-- ----------------------------
DROP TABLE IF EXISTS `marlo_messages`;
CREATE TABLE `marlo_messages` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `message_type` text,
  `message_value` text,
  `active_since` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `is_active` tinyint(1) NOT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `marlo_messages_created_fk` (`created_by`),
  KEY `marlo_messages_modified_fk` (`modified_by`),
  CONSTRAINT `marlo_messages_created_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `marlo_messages_modified_fk` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of marlo_messages
-- ----------------------------
INSERT INTO `marlo_messages` VALUES ('1', 'General Message', ' ', '2018-03-02 13:48:52', '1', '3', '3', ' ');
