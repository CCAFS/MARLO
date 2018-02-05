CREATE TABLE `powb_evidences` (
`id`  bigint(20) NOT NULL ,
`narrative`  text NULL ,
`is_active`  tinyint(1) NOT NULL ,
`active_since`  timestamp NULL ,
`created_by`  bigint(20) NULL ,
`modified_by`  bigint(20) NULL ,
`modification_justification`  text NULL ,
PRIMARY KEY (`id`),
CONSTRAINT `powb_evidence_id_fk` FOREIGN KEY (`id`) REFERENCES `powb_synthesis` (`id`),
CONSTRAINT `powb_evidence_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
CONSTRAINT `powb_evidence_modified_by` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
)
;

