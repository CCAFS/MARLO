
SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `project_budgets_cluser_actvities`
-- ----------------------------
DROP TABLE IF EXISTS `project_budgets_cluser_actvities`;
CREATE TABLE `project_budgets_cluser_actvities` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `project_id` bigint(20) NOT NULL,
  `amount` bigint(20) DEFAULT NULL,
  `budget_type` bigint(20) DEFAULT NULL,
  `year` int(4) NOT NULL,
  `cluster_activity_id` bigint(20) NOT NULL,
  `gender_percentage` double DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT '1',
  `active_since` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_by` bigint(20) NOT NULL,
  `modified_by` bigint(20) NOT NULL,
  `modification_justification` mediumtext NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_project_budgets_temp_projects_idx` (`project_id`) USING BTREE,
  KEY `FK_institutions_project_bud_temp_idx` (`cluster_activity_id`) USING BTREE,
  KEY `FK_project_bud_temp_users_created_by_idx` (`created_by`) USING BTREE,
  KEY `FK_project_bud_temp_users_modified_by_idx` (`modified_by`) USING BTREE,
  KEY `FK_project_budgets_budget_types` (`budget_type`) USING BTREE,
  CONSTRAINT `project_budgets_cluser_actvities_ibfk_7` FOREIGN KEY (`cluster_activity_id`) REFERENCES `crp_cluster_of_activities` (`id`),
  CONSTRAINT `project_budgets_cluser_actvities_ibfk_1` FOREIGN KEY (`budget_type`) REFERENCES `budget_types` (`id`),
  CONSTRAINT `project_budgets_cluser_actvities_ibfk_5` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `project_budgets_cluser_actvities_ibfk_6` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2572 DEFAULT CHARSET=utf8;

