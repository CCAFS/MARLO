SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for general_statuses
-- ----------------------------
DROP TABLE IF EXISTS `general_statuses`;
CREATE TABLE `general_statuses` (
  `id` bigint(20) NOT NULL,
  `name` text,
  `iati_equivalence` bigint(20) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT NULL,
  `active_since` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `created_by` (`created_by`),
  KEY `modified_by` (`modified_by`),
  CONSTRAINT `general_statuses_ibfk_1` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `general_statuses_ibfk_2` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of general_statuses
-- ----------------------------
INSERT INTO `general_statuses` VALUES ('1', 'New', '1', '1', '2019-01-25 09:18:22', '1057', '1057', '');
INSERT INTO `general_statuses` VALUES ('2', 'On Going', '2', '1', '2019-01-25 09:15:23', '1057', '1057', ' ');
INSERT INTO `general_statuses` VALUES ('3', 'Complete', '3', '1', '2019-01-25 09:15:24', '1057', '1057', ' ');
INSERT INTO `general_statuses` VALUES ('4', 'Extended', null, '1', '2019-01-25 09:20:30', '1057', '1507', ' ');
INSERT INTO `general_statuses` VALUES ('5', 'Cancelled', '5', '1', '2019-01-25 09:15:24', '1057', '1057', ' ');
SET FOREIGN_KEY_CHECKS=1;
