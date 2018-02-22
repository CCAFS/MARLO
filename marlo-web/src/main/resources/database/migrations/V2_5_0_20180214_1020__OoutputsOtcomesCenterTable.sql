DROP TABLE IF EXISTS `center_outputs_outcomes`;
CREATE TABLE `center_outputs_outcomes` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`output_id`  int(11) NOT NULL ,
`outcome_id`  int(11) NOT NULL ,
`is_active`  tinyint(1) NOT NULL ,
`active_since`  timestamp NULL ,
`created_by`  bigint(20) NULL ,
`modified_by`  bigint(20) NULL ,
`modification_justification`  text NULL ,
PRIMARY KEY (`id`),
CONSTRAINT `puts_comes_output_id_fk` FOREIGN KEY (`output_id`) REFERENCES `center_outputs` (`id`),
CONSTRAINT `puts_comes_outcome_id_fk` FOREIGN KEY (`outcome_id`) REFERENCES `center_outcomes` (`id`),
CONSTRAINT `puts_comes_created_id_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
CONSTRAINT `puts_comes_modified_id_fk` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
)
;

