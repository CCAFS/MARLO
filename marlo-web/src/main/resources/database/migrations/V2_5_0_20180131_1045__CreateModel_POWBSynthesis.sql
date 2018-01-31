DROP TABLE IF EXISTS `powb_synthesis`;

CREATE TABLE `powb_synthesis` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`plan_summary`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`flagship_program_file`  bigint(20) NULL DEFAULT NULL ,
`id_phase`  bigint(20) NOT NULL ,
`liaison_institution_id`  bigint(20) NOT NULL ,
`is_active`  tinyint(1) NOT NULL ,
`created_by`  bigint(20) NULL DEFAULT NULL ,
`active_since`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ,
`modified_by`  bigint(20) NOT NULL ,
PRIMARY KEY (`id`),
CONSTRAINT `powb_synthesis_fk1` FOREIGN KEY (`flagship_program_file`) REFERENCES `files` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
CONSTRAINT `powb_synthesis_fk2` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
CONSTRAINT `powb_synthesis_fk3` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
CONSTRAINT `powb_synthesis_fk4` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
CONSTRAINT `powb_synthesis_fk5` FOREIGN KEY (`liaison_institution_id`) REFERENCES `liaison_institutions` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
INDEX `powb_synthesis_index_flagship_program_file` (`flagship_program_file`) USING BTREE ,
INDEX `powb_synthesis_index_created_by` (`created_by`) USING BTREE ,
INDEX `powb_synthesis_index_modified_by` (`modified_by`) USING BTREE ,
INDEX `powb_synthesis_index_id_phase` (`id_phase`) USING BTREE,
INDEX `powb_synthesis_index_liaison_institution_id` (`liaison_institution_id`) USING BTREE
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
;