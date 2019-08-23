SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for deliverable_user_partnership_persons
-- ----------------------------
DROP TABLE IF EXISTS `deliverable_user_partnership_persons`;
CREATE TABLE `deliverable_user_partnership_persons` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_partnership_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `division_id` bigint(20) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `active_since` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_by` bigint(20) NOT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text CHARACTER SET utf8 COLLATE utf8_general_ci,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `division_id` (`division_id`),
  KEY `modified_by` (`modified_by`),
  KEY `created_by` (`created_by`),
  KEY `user_partnership_id` (`user_partnership_id`),
  CONSTRAINT `deliverable_user_partnership_persons_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `deliverable_user_partnership_persons_ibfk_4` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`),
  CONSTRAINT `deliverable_user_partnership_persons_ibfk_5` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `deliverable_user_partnership_persons_ibfk_3` FOREIGN KEY (`division_id`) REFERENCES `partner_divisions` (`id`),
  CONSTRAINT `deliverable_user_partnership_persons_ibfk_6` FOREIGN KEY (`user_partnership_id`) REFERENCES `deliverable_user_partnerships` (`id`)
) ENGINE=InnoDB;

-- ----------------------------
-- Records of deliverable_user_partnership_persons
-- ----------------------------
SET FOREIGN_KEY_CHECKS=1;
