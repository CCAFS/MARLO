START TRANSACTION;

CREATE TABLE `project_communications` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`project_outcome_id`  bigint(20) NOT NULL ,
`communication`  text NOT NULL ,
`file_communication`  bigint NULL ,
`is_active`  tinyint(1) NOT NULL DEFAULT 1 ,
`active_since`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ,
`created_by`  bigint(20) NOT NULL ,
`modified_by`  bigint(20) NOT NULL ,
`modification_justification`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
PRIMARY KEY (`id`),
FOREIGN KEY (`project_outcome_id`) REFERENCES `project_outcomes` (`id`),
FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`),
FOREIGN KEY (`file_communication`) REFERENCES `files` (`id`)
)
;



 

COMMIT;