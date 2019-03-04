SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for project_budget_executions
-- ----------------------------
DROP TABLE IF EXISTS `project_budget_executions`;
CREATE TABLE `project_budget_executions` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT,
`project_id`  bigint(20) NOT NULL ,
`institution_id`  bigint(20) NOT NULL ,
`phase_id`  bigint(20) NOT NULL ,
`budget_type_id`  bigint(20) NOT NULL ,
`year`  int(4) NOT NULL ,
`actual_expenditure`  double(30,2) NULL DEFAULT NULL ,
`is_active`  tinyint(1) NOT NULL ,
`active_since`  timestamp NULL DEFAULT CURRENT_TIMESTAMP ,
`created_by`  bigint(20) NULL DEFAULT NULL ,
`modified_by`  bigint(20) NULL DEFAULT NULL ,
`modification_justification`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
PRIMARY KEY (`id`),
CONSTRAINT `project_budget_execution_ibfk_1` FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
CONSTRAINT `project_budget_execution_ibfk_2` FOREIGN KEY (`institution_id`) REFERENCES `institutions` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
CONSTRAINT `project_budget_execution_ibfk_3` FOREIGN KEY (`phase_id`) REFERENCES `phases` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
CONSTRAINT `project_budget_execution_ibfk_4` FOREIGN KEY (`budget_type_id`) REFERENCES `budget_types` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
CONSTRAINT `project_budget_execution_ibfk_5` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
CONSTRAINT `project_budget_execution_ibfk_6` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
INDEX `idx_project_budget_execution_id` (`id`) USING BTREE ,
INDEX `idx_project_budget_execution_project_id` (`project_id`) USING BTREE ,
INDEX `idx_project_budget_execution_institution_id` (`institution_id`) USING BTREE ,
INDEX `idx_project_budget_execution_phase_id` (`phase_id`) USING BTREE ,
INDEX `idx_project_budget_execution_budget_type_id` (`budget_type_id`) USING BTREE ,
INDEX `idx_project_budget_execution_created_by` (`created_by`) USING BTREE ,
INDEX `idx_project_budget_execution_modified_by` (`modified_by`) USING BTREE 
) ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci;

SET FOREIGN_KEY_CHECKS=1;