CREATE TABLE `disaggregated_targets_business_rules` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` text NOT NULL,
  `description` text,
  `modified_by` bigint(20) DEFAULT NULL,
  `modification_justification` text,
  `created_by` bigint(20) DEFAULT NULL,
  `active_since` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `disaggregated_targets_business_rules_modified_by` (`modified_by`),
  CONSTRAINT `disaggregated_targets_business_rules_ibfk` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;