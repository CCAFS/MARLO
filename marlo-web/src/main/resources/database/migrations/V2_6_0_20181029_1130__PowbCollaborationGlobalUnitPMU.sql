SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for powb_collaboration_global_unit_pmus
-- ----------------------------
DROP TABLE IF EXISTS `powb_collaboration_global_unit_pmus`;
CREATE TABLE `powb_collaboration_global_unit_pmus` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `powb_collaboration_id` bigint(20) DEFAULT NULL,
  `powb_collaboration_global_unit_id` bigint(20) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL,
  `active_since` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `modified_by` bigint(20) DEFAULT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `powb_collaboration_id` (`powb_collaboration_id`),
  KEY `powb_collaboration_global_unit_id` (`powb_collaboration_global_unit_id`),
  KEY `modified_by` (`modified_by`),
  KEY `created_by` (`created_by`),
  CONSTRAINT `powb_collaboration_global_unit_pmus_ibfk_1` FOREIGN KEY (`powb_collaboration_id`) REFERENCES `powb_collaboration` (`id`),
  CONSTRAINT `powb_collaboration_global_unit_pmus_ibfk_2` FOREIGN KEY (`powb_collaboration_global_unit_id`) REFERENCES `powb_collaboration_global_units` (`id`),
  CONSTRAINT `powb_collaboration_global_unit_pmus_ibfk_3` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`),
  CONSTRAINT `powb_collaboration_global_unit_pmus_ibfk_4` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

