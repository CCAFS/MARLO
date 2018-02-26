DROP TABLE IF EXISTS `powb_collaboration`;

CREATE TABLE `powb_collaboration` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`powb_synthesis`  bigint(20) NULL ,
`key_external_partners`  text NULL ,
`cotributions_platafforms`  text NULL ,
`cross_crp`  text NULL ,
`effostorn_country`  text NULL ,
`created_by`  bigint(20) NULL DEFAULT NULL COMMENT 'foreign key to the table users' ,
`is_active`  tinyint(1) NOT NULL DEFAULT 1 ,
`active_since`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ,
`modified_by`  bigint(20) NOT NULL ,
`modification_justification`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
PRIMARY KEY (`id`),
FOREIGN KEY (`powb_synthesis`) REFERENCES `powb_synthesis` (`id`),
FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
)
ENGINE=InnoDB

;

