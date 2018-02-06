drop table if exists powb_expected_crp_progress;
CREATE TABLE `powb_expected_crp_progress` (
`id`  bigint(20) NOT NULL ,
`crp_milestone_id`  bigint(20) NULL ,
`assessment`  varchar(5) NULL ,
`means`  text NULL ,
`expected_highlights`  text NULL ,
`created_by`  bigint(20) NULL DEFAULT NULL COMMENT 'foreign key to the table users' ,
`is_active`  tinyint(1) NOT NULL DEFAULT 1 ,
`active_since`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ,
`modified_by`  bigint(20) NOT NULL ,
`modification_justification`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
PRIMARY KEY (`id`),
FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
FOREIGN KEY (`crp_milestone_id`) REFERENCES `crp_milestones` (`id`),
FOREIGN KEY (`id`) REFERENCES `powb_synthesis` (`id`)
)ENGINE=InnoDB
;

