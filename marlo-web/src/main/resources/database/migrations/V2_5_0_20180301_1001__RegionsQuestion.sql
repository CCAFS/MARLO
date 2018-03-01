SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for powb_collaboration_regions
-- ----------------------------
DROP TABLE IF EXISTS `powb_collaboration_regions`;
CREATE TABLE `powb_collaboration_regions` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `powb_synthesis` bigint(20) DEFAULT NULL,
  `liasion_institution_id` bigint(20) DEFAULT NULL,
  `effostorn_country` text,
  `created_by` bigint(20) DEFAULT NULL COMMENT 'foreign key to the table users',
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `active_since` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified_by` bigint(20) NOT NULL,
  `modification_justification` text NOT NULL,
  PRIMARY KEY (`id`),
  KEY `powb_synthesis` (`powb_synthesis`),
  KEY `created_by` (`created_by`),
  KEY `modified_by` (`modified_by`),
  KEY `liasion_institution_id` (`liasion_institution_id`),
  CONSTRAINT `powb_collaboration_regions_ibfk_1` FOREIGN KEY (`powb_synthesis`) REFERENCES `powb_synthesis` (`id`),
  CONSTRAINT `powb_collaboration_regions_ibfk_2` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `powb_collaboration_regions_ibfk_3` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`),
  CONSTRAINT `powb_collaboration_regions_ibfk_4` FOREIGN KEY (`liasion_institution_id`) REFERENCES `liaison_institutions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;