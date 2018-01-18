drop table if exists crp_program_outcome_indicator; 
CREATE TABLE `crp_program_outcome_indicator` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`crp_program_outcome_id`  bigint(20) NOT NULL ,
`indicator`  text NOT NULL ,
`is_active`  tinyint(1) NOT NULL DEFAULT 1 ,
`active_since`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ,
`created_by`  bigint(20) NOT NULL ,
`modified_by`  bigint(20) NOT NULL ,
`modification_justification`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,

PRIMARY KEY (`id`),
FOREIGN KEY (`crp_program_outcome_id`) REFERENCES `crp_program_outcomes` (`id`),
FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
)
ENGINE=InnoDB
;

ALTER TABLE `crp_program_outcomes`
ADD COLUMN `file_id`  bigint(20) NULL AFTER `modification_justification`;

ALTER TABLE `crp_program_outcomes` ADD FOREIGN KEY (`file_id`) REFERENCES `files` (`id`);


ALTER TABLE `crp_program_outcome_indicator`
ADD COLUMN `composed_id`  varchar(20)  NULL AFTER `modification_justification`;