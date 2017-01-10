DROP TABLE IF EXISTS `deliverable_quality_checks`;
CREATE TABLE `deliverable_quality_checks` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`deliverable_id`  bigint(20) NULL ,
`quality_assurance`  bigint(20) NULL ,
`data_dictionary`  bigint(20) NULL ,
`data_tools`  bigint(20) NULL ,
`is_active`  tinyint(1) NOT NULL ,
`active_since`  timestamp NULL ON UPDATE CURRENT_TIMESTAMP ,
`modified_by`  bigint(20) NULL ,
`created_by`  bigint(20) NULL ,
`modification_justification`  text NULL ,
PRIMARY KEY (`id`),
CONSTRAINT `quality_deliverable_id` FOREIGN KEY (`deliverable_id`) REFERENCES `deliverables` (`id`),
CONSTRAINT `quality_assurance` FOREIGN KEY (`quality_assurance`) REFERENCES `deliverable_quality_answers` (`id`),
CONSTRAINT `quiality_dictionary` FOREIGN KEY (`data_dictionary`) REFERENCES `deliverable_quality_answers` (`id`),
CONSTRAINT `quiality_tools` FOREIGN KEY (`data_tools`) REFERENCES `deliverable_quality_answers` (`id`),
CONSTRAINT `quiality_create_by_id` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
CONSTRAINT `quiality_modified_by_id` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
);