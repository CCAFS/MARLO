CREATE TABLE `monitoring_outcomes` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`outcome_id`  int(11) NOT NULL ,
`year`  numeric(4,0) NOT NULL ,
`narrative`  text NULL ,
`is_active`  tinyint(1) NOT NULL ,
`active_since`  timestamp NULL ,
`created_by`  bigint(20) NULL ,
`modified_by`  bigint(20) NULL ,
`modification_justification`  text NULL ,
PRIMARY KEY (`id`),
CONSTRAINT `outcome_monitoring_id_fk` FOREIGN KEY (`outcome_id`) REFERENCES `research_outcomes` (`id`),
CONSTRAINT `outcome_monitoring_created_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
CONSTRAINT `outcome_monitoring_modified_fk` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
)
;

