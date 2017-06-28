CREATE TABLE `research_output_partners` (
`id`  int(11) NOT NULL AUTO_INCREMENT ,
`research_output_id`  int(11) NOT NULL ,
`institution_id`  bigint(20) NOT NULL ,
`is_internal`  tinyint(1) NULL ,
`is_active`  tinyint(1) NULL ,
`active_since`  timestamp NULL ON UPDATE CURRENT_TIMESTAMP ,
`created_by`  bigint(20) NULL ,
`modified_by`  bigint(20) NULL ,
`modification_justification`  text NULL ,
PRIMARY KEY (`id`),
CONSTRAINT `partners_institutions_fk` FOREIGN KEY (`institution_id`) REFERENCES `institutions` (`id`),
CONSTRAINT `partners_output_fk` FOREIGN KEY (`research_output_id`) REFERENCES `research_outputs` (`id`),
CONSTRAINT `partners_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
CONSTRAINT `partners_modified_by` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
);