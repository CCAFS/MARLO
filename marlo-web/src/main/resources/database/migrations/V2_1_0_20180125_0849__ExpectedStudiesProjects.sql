DROP TABLE IF EXISTS `expected_study_projects`;

CREATE TABLE `expected_study_projects` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`project_id`  bigint(20) NULL ,
`expected_id`  bigint(20) NULL ,
`is_active`  tinyint(1) NOT NULL DEFAULT 1 ,
`active_since`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ,
`created_by`  bigint(20) NOT NULL ,
`modified_by`  bigint(20) NOT NULL ,
`modification_justification`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
PRIMARY KEY (`id`),
FOREIGN KEY (`expected_id`) REFERENCES `project_expected_studies` (`id`),
FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`),
FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
)
ENGINE=InnoDB
;

