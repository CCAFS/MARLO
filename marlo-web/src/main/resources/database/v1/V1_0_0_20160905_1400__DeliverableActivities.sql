START TRANSACTION;
CREATE TABLE `deliverable_activities` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`deliverable_id`  bigint(20) NULL ,
`activity_id`  bigint(20) NULL ,
`is_active`  tinyint(1) NOT NULL DEFAULT 1 ,
`active_since`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ,
`created_by`  bigint(20) NOT NULL ,
`modified_by`  bigint(20) NOT NULL ,
`modification_justification`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
PRIMARY KEY (`id`),
CONSTRAINT `dev_act_fk1` FOREIGN KEY (`deliverable_id`) REFERENCES `deliverables` (`id`),
CONSTRAINT `dev_act_fk2` FOREIGN KEY (`activity_id`) REFERENCES `activities` (`id`),
CONSTRAINT `dev_act_fk3` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
CONSTRAINT `dev_act_fk4` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
)
;
COMMIT;
