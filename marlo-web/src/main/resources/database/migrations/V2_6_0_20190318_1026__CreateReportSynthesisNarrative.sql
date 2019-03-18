SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for report_synthesis_narratives
-- ----------------------------
DROP TABLE IF EXISTS `report_synthesis_narratives`;
CREATE TABLE `report_synthesis_narratives` (
  `id` bigint(20) NOT NULL,
  `narrative` text,
  `is_active` tinyint(1) NOT NULL,
  `active_since` timestamp NULL DEFAULT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `created_by` (`created_by`),
  KEY `modified_by` (`modified_by`),
  CONSTRAINT `report_synthesis_narratives_ibfk_1` FOREIGN KEY (`id`) REFERENCES `report_synthesis` (`id`),
  CONSTRAINT `report_synthesis_narratives_ibfk_2` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `report_synthesis_narratives_ibfk_3` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB 
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
ROW_FORMAT=COMPACT;

SET FOREIGN_KEY_CHECKS=1;