CREATE TABLE `crp_target_units` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`target_unit_id`  bigint(20) NULL ,
`crp_id`  bigint(20) NULL ,
`is_active`  tinyint(1) NULL ,
`active_since`  timestamp NULL ON UPDATE CURRENT_TIMESTAMP ,
`created_by`  bigint(20) NULL ,
`modified_by`  bigint(20) NULL ,
`modification_justification`  text NULL ,
PRIMARY KEY (`id`),
CONSTRAINT `tu_crp_id` FOREIGN KEY (`crp_id`) REFERENCES `crps` (`id`),
CONSTRAINT `tu_target_unit_id` FOREIGN KEY (`target_unit_id`) REFERENCES `srf_target_units` (`id`),
CONSTRAINT `tu_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
CONSTRAINT `tu_modified_by` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
)
;