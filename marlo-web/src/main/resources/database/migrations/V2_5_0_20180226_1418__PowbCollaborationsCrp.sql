DROP TABLE IF EXISTS `powb_collaboration_global_units`;

CREATE TABLE `powb_collaboration_global_units` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`powb_synthesis`  bigint(20) NULL ,
`global_unit_id`  bigint(20) NULL ,
`flagship`  text NULL ,
`collaboration_type`  varchar(100) NULL ,
`brief`  varchar(255) NULL ,
`created_by`  bigint(20) NULL DEFAULT NULL COMMENT 'foreign key to the table users' ,
`is_active`  tinyint(1) NOT NULL DEFAULT 1 ,
`active_since`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ,
`modified_by`  bigint(20) NOT NULL ,
`modification_justification`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
PRIMARY KEY (`id`),
FOREIGN KEY (`powb_synthesis`) REFERENCES `powb_synthesis` (`id`),
FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`),
FOREIGN KEY (`global_unit_id`) REFERENCES `global_units` (`id`)
)
ENGINE=InnoDB
;

