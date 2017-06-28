CREATE TABLE `project_outputs` (
`id`  int(11) NOT NULL AUTO_INCREMENT ,
`project_id`  bigint(20) NULL ,
`output_id`  int(11) NULL ,
`is_active`  tinyint(1) NOT NULL ,
`modification_justification`  text NULL ,
`created_by`  bigint(20) NULL ,
`modified_by`  bigint(20) NULL ,
`active_since`  timestamp NULL ON UPDATE CURRENT_TIMESTAMP ,
PRIMARY KEY (`id`),
CONSTRAINT `po_project_fk` FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`),
CONSTRAINT `po_output_fk` FOREIGN KEY (`output_id`) REFERENCES `research_outputs` (`id`),
CONSTRAINT `po_created_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
CONSTRAINT `po_modified_fk` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
)
;
