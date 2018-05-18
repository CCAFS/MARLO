SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for deliverable_participants
-- ----------------------------
DROP TABLE IF EXISTS `deliverable_participants`;
CREATE TABLE `deliverable_participants` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `deliverable_id` bigint(20) NOT NULL,
  `phase_id` bigint(20) NOT NULL,
  `has_participants` tinyint(1) NULL DEFAULT NULL,
  `event_activity_name` text NULL,
  `rep_ind_type_activity_id` bigint(20) NULL,
  `academic_degree` text NULL,
  `participants` double NULL DEFAULT NULL,
  `estimate_participants` tinyint(1) NULL DEFAULT NULL ,
  `females` double NULL DEFAULT NULL ,
  `estimate_females` tinyint(1) NULL DEFAULT NULL ,
  `dont_know_female` tinyint(1) NULL DEFAULT NULL,
  `rep_ind_type_participant_id` bigint(20) NULL,
  `rep_ind_geographic_scope_id` bigint(20) NULL,
  `rep_ind_region_id` bigint(20) NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `active_since` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_by` bigint(20) NOT NULL,
  `modified_by` bigint(20) NOT NULL,
  `modification_justification` text NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_deliverable_participants_id`(`id`) USING BTREE,
  INDEX `idx_deliverable_participants_deliverable`(`deliverable_id`) USING BTREE,
  INDEX `idx_deliverable_participants_phase`(`phase_id`) USING BTREE,
  INDEX `idx_deliverable_participants_activity`(`rep_ind_type_activity_id`) USING BTREE,
  INDEX `idx_deliverable_participants_type_participant`(`rep_ind_type_participant_id`) USING BTREE,
  INDEX `idx_deliverable_participants_geographic_scope`(`rep_ind_geographic_scope_id`) USING BTREE,
  INDEX `idx_deliverable_participants_region`(`rep_ind_region_id`) USING BTREE,
  INDEX `idx_deliverable_participants_created_by`(`created_by`) USING BTREE,
  INDEX `idx_deliverable_participants_modified_by`(`modified_by`) USING BTREE,
  CONSTRAINT `deliverable_participants_ibfk_1` FOREIGN KEY (`deliverable_id`) REFERENCES `deliverables` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `deliverable_participants_ibfk_2` FOREIGN KEY (`phase_id`) REFERENCES `phases` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `deliverable_participants_ibfk_3` FOREIGN KEY (`rep_ind_type_activity_id`) REFERENCES `rep_ind_type_activities` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `deliverable_participants_ibfk_4` FOREIGN KEY (`rep_ind_type_participant_id`) REFERENCES `rep_ind_type_participants` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `deliverable_participants_ibfk_5` FOREIGN KEY (`rep_ind_geographic_scope_id`) REFERENCES `rep_ind_geographic_scopes` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `deliverable_participants_ibfk_6` FOREIGN KEY (`rep_ind_region_id`) REFERENCES `rep_ind_regions` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `deliverable_participants_ibfk_7` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `deliverable_participants_ibfk_8` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
)ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for deliverable_participant_locations
-- ----------------------------
DROP TABLE IF EXISTS `deliverable_participant_locations`;
CREATE TABLE `deliverable_participant_locations` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `deliverable_participant_id` bigint(20) NOT NULL,
  `loc_element_id` bigint(20) NOT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `active_since` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_by` bigint(20) NOT NULL,
  `modified_by` bigint(20) NOT NULL,
  `modification_justification` text NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_deliverable_participant_locations_id`(`id`) USING BTREE,
  INDEX `idx_deliverable_participant_locations_deliverable_participant_id`(`deliverable_participant_id`) USING BTREE,
  INDEX `idx_deliverable_participant_locations_loc_element_id`(`loc_element_id`) USING BTREE,
  INDEX `idx_deliverable_participant_locations_created_by`(`created_by`) USING BTREE,
  INDEX `idx_deliverable_participant_locations_modified_by`(`modified_by`) USING BTREE,
  CONSTRAINT `deliverable_participant_locations_ibfk_1` FOREIGN KEY (`deliverable_participant_id`) REFERENCES `deliverable_participants` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `deliverable_participant_locations_ibfk_2` FOREIGN KEY (`loc_element_id`) REFERENCES `loc_elements` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `deliverable_participant_locations_ibfk_3` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `deliverable_participant_locations_ibfk_4` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
)ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;