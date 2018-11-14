SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for powb_program_changes
-- ----------------------------
DROP TABLE IF EXISTS `powb_program_changes`;
CREATE TABLE `powb_program_changes` (
  `id` bigint(20) NOT NULL,
  `program_change` text,
  `is_active` tinyint(1) NOT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `active_since` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `created_by` (`created_by`),
  KEY `modified_by` (`modified_by`),
  CONSTRAINT `powb_program_changes_ibfk_1` FOREIGN KEY (`id`) REFERENCES `powb_synthesis` (`id`),
  CONSTRAINT `powb_program_changes_ibfk_2` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `powb_program_changes_ibfk_3` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

