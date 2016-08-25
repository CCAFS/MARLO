START TRANSACTION;

CREATE TABLE `project_nextusers` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`project_outcome_id`  bigint(20) NOT NULL ,
`next_user`  text NULL ,
`knowledge`  text NULL ,
`strategies`  text NULL ,
`is_active`  tinyint(1) NOT NULL ,
`created_by`  bigint(20) NULL DEFAULT NULL ,
`active_since`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ,
`modified_by`  bigint(20) NOT NULL ,
`modification_justification`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
PRIMARY KEY (`id`),
FOREIGN KEY (`project_outcome_id`) REFERENCES `project_outcomes` (`id`),
FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
)
;


COMMIT;