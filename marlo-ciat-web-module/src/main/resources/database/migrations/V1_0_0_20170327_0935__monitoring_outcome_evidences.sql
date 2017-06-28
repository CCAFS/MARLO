CREATE TABLE `monitorign_outcome_evidences` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`monitoring_outcome_id`  bigint(20) NOT NULL ,
`evidence_link`  text NOT NULL ,
`is_active`  tinyint(1) NOT NULL ,
`active_since`  timestamp NULL ,
`created_by`  bigint(20) NULL ,
`modified_by`  bigint(20) NULL ,
`modification_justification`  text NULL ,
PRIMARY KEY (`id`),
CONSTRAINT `evidence_outcome_monitoring_id_fk` FOREIGN KEY (`monitoring_outcome_id`) REFERENCES `monitoring_outcomes` (`id`),
CONSTRAINT `evidence_created_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
CONSTRAINT `evidence_modified_fk` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
)
;

