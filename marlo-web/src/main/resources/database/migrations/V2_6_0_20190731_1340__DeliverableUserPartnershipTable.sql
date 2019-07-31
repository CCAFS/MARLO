SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for deliverable_user_parnetships
-- ----------------------------
DROP TABLE IF EXISTS `deliverable_user_parnetships`;
CREATE TABLE `deliverable_user_parnetships` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `deliverable_id` bigint(20) NOT NULL,
  `id_phase` bigint(20) DEFAULT NULL,
  `institution_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `deliverable_partner_type_id` bigint(20) DEFAULT NULL,
  `division_id` bigint(20) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `active_since` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_by` bigint(20) NOT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text CHARACTER SET utf8 COLLATE utf8_general_ci,
  PRIMARY KEY (`id`),
  KEY `deliverable_id` (`deliverable_id`),
  KEY `id_phase` (`id_phase`),
  KEY `institution_id` (`institution_id`),
  KEY `user_id` (`user_id`),
  KEY `deliverable_partner_type_id` (`deliverable_partner_type_id`),
  KEY `created_by` (`created_by`),
  KEY `modified_by` (`modified_by`),
  KEY `division_id` (`division_id`),
  CONSTRAINT `deliverable_user_parnetships_ibfk_1` FOREIGN KEY (`deliverable_id`) REFERENCES `deliverables` (`id`),
  CONSTRAINT `deliverable_user_parnetships_ibfk_2` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`),
  CONSTRAINT `deliverable_user_parnetships_ibfk_3` FOREIGN KEY (`institution_id`) REFERENCES `institutions` (`id`),
  CONSTRAINT `deliverable_user_parnetships_ibfk_4` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `deliverable_user_parnetships_ibfk_5` FOREIGN KEY (`deliverable_partner_type_id`) REFERENCES `deliverable_partner_type` (`id`),
  CONSTRAINT `deliverable_user_parnetships_ibfk_6` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `deliverable_user_parnetships_ibfk_7` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`),
  CONSTRAINT `deliverable_user_parnetships_ibfk_8` FOREIGN KEY (`division_id`) REFERENCES `partner_divisions` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=196606 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
SET FOREIGN_KEY_CHECKS=1;
