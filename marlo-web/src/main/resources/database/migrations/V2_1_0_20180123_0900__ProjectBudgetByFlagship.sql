CREATE TABLE `project_budgets_flagships` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`id_phase`  bigint(20) NOT NULL ,
`project_id`  bigint(20) NOT NULL ,
`amount`  double NULL ,
`budget_type`  bigint(20) NULL ,
`year`  int(4) NOT NULL ,
`crp_program`  bigint(20) NOT NULL ,
`is_active`  tinyint(1) NOT NULL ,
`active_since`  timestamp NOT NULL ON UPDATE CURRENT_TIMESTAMP ,
`created_by`  bigint(20) NULL ,
`modified_by`  bigint(20) NULL ,
`modification_justification`  text NULL ,
PRIMARY KEY (`id`),
CONSTRAINT `p_flagship_phase_fk` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`),
CONSTRAINT `p_flagship_project_fk` FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`),
CONSTRAINT `p_flagship_budget_type_fk` FOREIGN KEY (`budget_type`) REFERENCES `budget_types` (`id`),
CONSTRAINT `p_flagship_crpProgram_fk` FOREIGN KEY (`crp_program`) REFERENCES `crp_programs` (`id`),
CONSTRAINT `p_flagship_created_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
CONSTRAINT `p_flagship_modified_fk` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
)
;