DROP TABLE IF EXISTS `powb_flagship_plans`;

CREATE TABLE `powb_flagship_plans` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`plan_summary`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL ,
`flagship_program_file`  bigint(20) NULL DEFAULT NULL ,
`powb_synthesis_id`  bigint(20) NOT NULL ,
`is_active`  tinyint(1) NOT NULL ,
`created_by`  bigint(20) NULL DEFAULT NULL ,
`active_since`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ,
`modified_by`  bigint(20) NOT NULL ,
PRIMARY KEY (`id`),
CONSTRAINT `powb_flagship_plans_fk_flagship_program_file` FOREIGN KEY (`flagship_program_file`) REFERENCES `files` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
CONSTRAINT `powb_flagship_plans_fk_powb_synthesis_id` FOREIGN KEY (`powb_synthesis_id`) REFERENCES `powb_synthesis` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
CONSTRAINT `powb_flagship_plans_fk_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
CONSTRAINT `powb_flagship_plans_fk_modified_by` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
INDEX `powb_flagship_plans_index_flagship_program_file` (`flagship_program_file`) USING BTREE,
INDEX `powb_flagship_plans_index_powb_synthesis_id` (`powb_synthesis_id`) USING BTREE,
INDEX `powb_flagship_plans_index_created_by` (`created_by`) USING BTREE ,
INDEX `powb_flagship_plans_index_modified_by` (`modified_by`) USING BTREE
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
;