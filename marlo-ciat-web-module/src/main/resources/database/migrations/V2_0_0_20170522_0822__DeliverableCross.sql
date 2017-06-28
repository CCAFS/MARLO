CREATE TABLE `deliverable_crosscuting_themes` (
`id`  bigint(20) NOT NULL ,
`climate_change`  tinyint(1) NULL ,
`gender`  tinyint(1) NULL ,
`youth`  tinyint(1) NULL ,
`policies_institutions`  tinyint(1) NULL ,
`capacity_development`  tinyint(1) NULL ,
`big_data`  tinyint(1) NULL ,
`impact_assessment`  tinyint(1) NULL ,
`n_a`  tinyint(1) NULL ,
`is_active`  tinyint(1) NOT NULL ,
`active_since`  timestamp NULL ,
`created_by`  bigint(20) NULL ,
`modified_by`  bigint(20) NULL ,
`modification_justification`  text NULL ,
PRIMARY KEY (`id`),
CONSTRAINT `deliverable_cross_id_fk` FOREIGN KEY (`id`) REFERENCES `deliverables` (`id`),
CONSTRAINT `deliverable_cross_created_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
CONSTRAINT `deliverable_cross_modified_fk` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
)
;

