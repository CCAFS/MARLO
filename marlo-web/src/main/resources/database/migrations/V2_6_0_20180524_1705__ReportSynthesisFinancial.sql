SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for report_synthesis_financial_summaries
-- ----------------------------
DROP TABLE IF EXISTS `report_synthesis_financial_summaries`;
CREATE TABLE `report_synthesis_financial_summaries` (
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
  CONSTRAINT `report_synthesis_financial_summaries_ibfk_1` FOREIGN KEY (`id`) REFERENCES `report_synthesis` (`id`),
  CONSTRAINT `report_synthesis_financial_summaries_ibfk_2` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `report_synthesis_financial_summaries_ibfk_3` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of report_synthesis_financial_summaries
-- ----------------------------

-- ----------------------------
-- Table structure for report_synthesis_financial_summary_budgets
-- ----------------------------
DROP TABLE IF EXISTS `report_synthesis_financial_summary_budgets`;
CREATE TABLE `report_synthesis_financial_summary_budgets` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `report_synthesis_financial_summary_id` bigint(20) DEFAULT NULL,
  `liaison_institution_id` bigint(20) DEFAULT NULL,
  `w1_planned` double(30,2) DEFAULT NULL,
  `w3_planned` double(30,2) DEFAULT NULL,
  `bilateral_planned` double(30,2) DEFAULT NULL,
  `w1_actual` double(30,2) DEFAULT NULL,
  `w3_actual` double(30,2) DEFAULT NULL,
  `bilateral_actual` double(30,2) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL,
  `active_since` timestamp NULL DEFAULT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `report_synthesis_financial_summary_id` (`report_synthesis_financial_summary_id`),
  KEY `liaison_institution_id` (`liaison_institution_id`),
  KEY `created_by` (`created_by`),
  KEY `modified_by` (`modified_by`),
  CONSTRAINT `report_synthesis_financial_summary_budgets_ibfk_1` FOREIGN KEY (`report_synthesis_financial_summary_id`) REFERENCES `report_synthesis_financial_summaries` (`id`),
  CONSTRAINT `report_synthesis_financial_summary_budgets_ibfk_2` FOREIGN KEY (`liaison_institution_id`) REFERENCES `liaison_institutions` (`id`),
  CONSTRAINT `report_synthesis_financial_summary_budgets_ibfk_3` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
  CONSTRAINT `report_synthesis_financial_summary_budgets_ibfk_4` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of report_synthesis_financial_summary_budgets
-- ----------------------------

