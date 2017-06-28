CREATE TABLE `deliverable_outputs` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`deliverable_id`  bigint(20) NULL ,
`output_id`  int(11) NULL ,
`is_active`  tinyint(1) NOT NULL ,
`active_since`  timestamp NULL ,
`created_by`  bigint(20) NULL ,
`modified_by`  bigint(20) NULL ,
`modification_justification`  text NULL ,
PRIMARY KEY (`id`),
CONSTRAINT `do_deliverable_fk` FOREIGN KEY (`deliverable_id`) REFERENCES `deliverables` (`id`),
CONSTRAINT `do_outputs_fk` FOREIGN KEY (`output_id`) REFERENCES `research_outputs` (`id`),
CONSTRAINT `do_created_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
CONSTRAINT `do_modified_fk` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
)
;

