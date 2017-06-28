CREATE TABLE `deliverable_documents` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`deliverable_id`  bigint(20) NOT NULL ,
`link`  text NULL ,
`is_active`  tinyint(1) NOT NULL ,
`created_by`  bigint(20) NULL ,
`modified_by`  bigint(20) NULL ,
`active_since`  timestamp NULL ,
`modification_justification`  text NULL ,
PRIMARY KEY (`id`),
CONSTRAINT `deliverable_documents_deliverable_id_fk` FOREIGN KEY (`deliverable_id`) REFERENCES `deliverables` (`id`),
CONSTRAINT `deliverable_documents_created_id_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
CONSTRAINT `deliverable_documents_modified_id_fk` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
)
;

