drop table if EXISTS project_outcome_indicators;
CREATE TABLE `project_outcome_indicators` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`crp_outcome_indicator`  bigint(20) NULL ,
`value`  double(10,0) NULL ,
`narrative`  text NULL ,
`project_outcome_id`  bigint(20) NULL ,
`is_active`  tinyint(1) NOT NULL DEFAULT 1 ,
`active_since`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ,
`modified_by`  bigint(20) NOT NULL ,
`modification_justification`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`created_by`  bigint(20) NULL DEFAULT NULL COMMENT 'foreign key to the table users' ,
PRIMARY KEY (`id`),
FOREIGN KEY (`crp_outcome_indicator`) REFERENCES `crp_program_outcome_indicator` (`id`),
FOREIGN KEY (`project_outcome_id`) REFERENCES `project_outcomes` (`id`),
FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`),
FOREIGN KEY (`created_by`) REFERENCES `users` (`id`)
)ENGINE=InnoDB
;

