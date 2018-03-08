DROP TABLE IF EXISTS `powb_toc`;
CREATE TABLE `powb_toc` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`toc_overall`  text NULL ,
`toc_file_id`  bigint(20) NULL ,
`powb_synthesis_id`  bigint(20) NOT NULL ,
`is_active`  tinyint(1) NOT NULL ,
`created_by`  bigint(20) NULL ,
`modified_by`  bigint(20) NULL ,
`active_since`  timestamp NULL ,
`modification_justification`  text NULL ,
PRIMARY KEY (`id`),
CONSTRAINT `powb_toc_synthesis_id_fk` FOREIGN KEY (`powb_synthesis_id`) REFERENCES `powb_synthesis` (`id`),
CONSTRAINT `powb_toc_file_id_fk` FOREIGN KEY (`toc_file_id`) REFERENCES `files` (`id`),
CONSTRAINT `powb_toc_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
CONSTRAINT `powb_toc_modified_by_fk` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
)
;