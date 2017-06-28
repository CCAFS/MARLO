CREATE TABLE `project_partner_persons` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`project_partner_id`  bigint(20) NULL DEFAULT NULL ,
`user_id`  bigint(20) NULL DEFAULT NULL ,
`is_active`  tinyint(1) NOT NULL ,
`active_since`  timestamp NULL DEFAULT NULL ,
`created_by`  bigint(20) NULL DEFAULT NULL ,
`modified_by`  bigint(20) NULL DEFAULT NULL ,
`modification_justification`  text NULL ,
PRIMARY KEY (`id`),
CONSTRAINT `project_partner_person_partner_id_fk` FOREIGN KEY (`project_partner_id`) REFERENCES `project_partners` (`id`),
CONSTRAINT `project_partner_person_user_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
CONSTRAINT `project_partner_created_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
CONSTRAINT `project_partner_modified_fk` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
)
;

