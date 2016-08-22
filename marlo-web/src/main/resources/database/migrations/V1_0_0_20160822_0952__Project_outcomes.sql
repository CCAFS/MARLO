START TRANSACTION;

 CREATE TABLE `project_outcomes` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`project_id`  bigint(20) NOT NULL ,
`outcome_id`  bigint(20) NOT NULL ,
`expected_value`  decimal(10,0) NOT NULL ,
`expected_unit`  bigint(20) NOT NULL ,
`achieved_value`  decimal(10,0) NULL ,
`narrative_target`  text NOT NULL ,
`narrative_achieved`  text NULL ,
`year`  int(4) NOT NULL ,
`is_active`  tinyint(1) NOT NULL DEFAULT 1 ,
`active_since`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ,
`created_by`  bigint(20) NOT NULL ,
`modified_by`  bigint(20) NOT NULL ,
`modification_justification`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
PRIMARY KEY (`id`),
FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`),
FOREIGN KEY (`outcome_id`) REFERENCES `crp_program_outcomes` (`id`),
FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
)
;

 

COMMIT;