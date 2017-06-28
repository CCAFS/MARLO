CREATE TABLE `research_output_partner_persons` (
`id`  int(11) NOT NULL AUTO_INCREMENT ,
`outcome_partner_id`  int(11) NULL ,
`is_active`  tinyint NULL ,
`active_since`  timestamp NULL ON UPDATE CURRENT_TIMESTAMP ,
`created_by`  bigint(20) NULL ,
`modified_by`  bigint(20) NULL ,
PRIMARY KEY (`id`),
CONSTRAINT `person_partner_fk` FOREIGN KEY (`outcome_partner_id`) REFERENCES `research_output_partners` (`id`),
CONSTRAINT `person_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
CONSTRAINT `person_modified_by_fk` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
)
;