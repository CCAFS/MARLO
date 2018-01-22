CREATE TABLE `project_expected_studies` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`project_id`  bigint(20) NOT NULL ,
`id_phase`  bigint(20) NULL ,
`composed_id`  varchar(20) NULL ,
`topic_study`  text NULL ,
`scope`  int NULL ,
`type`  int NULL ,
`other_type`  text NULL ,
`sub_ido`  bigint(20) NULL ,
`srf_target`  bigint(20) NULL ,
`comments`  text NULL ,
`is_active`  tinyint(1) NOT NULL DEFAULT 1 ,
`active_since`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ,
`created_by`  bigint(20) NOT NULL ,
`modified_by`  bigint(20) NOT NULL ,
`modification_justification`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
PRIMARY KEY (`id`),
FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`),
FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`),
FOREIGN KEY (`sub_ido`) REFERENCES `srf_sub_idos` (`id`),
FOREIGN KEY (`srf_target`) REFERENCES `srf_slo_indicators` (`id`),

FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
)
ENGINE=InnoDB
;

