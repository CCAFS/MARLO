CREATE TABLE `project_partners` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`project_id`  bigint(20) NOT NULL ,
`institution_id`  bigint(20) NOT NULL ,
`is_internal`  tinyint(1) NULL ,
`is_active`  tinyint(1) NOT NULL ,
`active_since`  timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP ,
`created_by`  bigint(20) NULL DEFAULT NULL ,
`modified_by`  bigint(20) NULL DEFAULT NULL ,
`modification_justification`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
PRIMARY KEY (`id`),
CONSTRAINT `project_partners_project_id_fk` FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`),
CONSTRAINT `project_partners_institution_id_fk` FOREIGN KEY (`institution_id`) REFERENCES `institutions` (`id`),
CONSTRAINT `project_partners_created_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
CONSTRAINT `project_partners_modified_fk` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
)
;