
SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for report_synthesis_melia_evaluation_actions
-- ----------------------------
DROP TABLE IF EXISTS `report_synthesis_melia_evaluation_actions`;
CREATE TABLE `report_synthesis_melia_evaluation_actions` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `report_synthesis_melia_evaluation_id` bigint(20) DEFAULT NULL,
  `actions` text,
  `text_whon` text,
  `text_when` text,  
  `is_active` tinyint(1) NOT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `active_since` timestamp NULL DEFAULT NULL,
  `modification_justification` text,
  PRIMARY KEY (`id`),
  KEY `report_synthesis_melia_evaluation_id` (`report_synthesis_melia_evaluation_id`),
  KEY `created_by` (`created_by`),
  KEY `modified_by` (`modified_by`),
  CONSTRAINT `report_synthesis_melia_evaluation_actions_ibfk_1` FOREIGN KEY (`report_synthesis_melia_evaluation_id`) REFERENCES `report_synthesis_melia_evaluations` (`id`),
  CONSTRAINT `report_synthesis_melia_evaluation_actions_ibfk_2` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `report_synthesis_melia_evaluation_actions_ibfk_3` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

SET FOREIGN_KEY_CHECKS=1;
