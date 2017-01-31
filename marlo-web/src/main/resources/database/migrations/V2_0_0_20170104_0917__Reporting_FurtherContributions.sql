CREATE TABLE `project_further_contributions` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`project_id`  bigint(20) NULL ,
`outcome_id`  bigint(20) NULL ,
`year`  int(11) NULL ,
`description`  text NULL ,
`value`  int(11) NULL ,
`is_active`  tinyint(1) NULL ,
`active_since`  timestamp NULL ON UPDATE CURRENT_TIMESTAMP ,
`created_by`  bigint(20) NULL ,
`modified_by`  bigint(20) NULL ,
`modification_justification`  text NULL ,
PRIMARY KEY (`id`),
CONSTRAINT `fk_project_id` FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`),
CONSTRAINT `fk_outcome_id` FOREIGN KEY (`outcome_id`) REFERENCES `crp_program_outcomes` (`id`),
CONSTRAINT `fk_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
CONSTRAINT `fk_modified_by` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
);