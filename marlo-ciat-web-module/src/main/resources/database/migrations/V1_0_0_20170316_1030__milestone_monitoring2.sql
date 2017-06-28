CREATE TABLE `monitoring_milestones` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`monitoring_outcome_id`  bigint(20) NOT NULL ,
`milestone_id`  bigint(20) NOT NULL ,
`achieved_value`  decimal(10,2) NULL ,
`narrative`  text NULL ,
`is_active`  tinyint(1) NOT NULL ,
`active_since`  timestamp NULL ,
`created_by`  bigint(20) NULL ,
`modified_by`  bigint(20) NULL ,
`modification_justification`  text NULL ,
PRIMARY KEY (`id`),
CONSTRAINT `monitoring_milestone_id_fk` FOREIGN KEY (`milestone_id`) REFERENCES `research_milestones` (`id`),
CONSTRAINT `monitoring_milestone_outcome_fk` FOREIGN KEY (`monitoring_outcome_id`) REFERENCES `monitoring_outcomes` (`id`),
CONSTRAINT `monitoring_milestone_created_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
CONSTRAINT `monitoring_milestone_modified_fk` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
)
;

