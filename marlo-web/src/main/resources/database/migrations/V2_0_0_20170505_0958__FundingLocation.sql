CREATE TABLE `funding_source_locations` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`funding_source_id`  bigint(20) NOT NULL ,
`loc_element_id`  bigint(20) NOT NULL ,
`is_active`  tinyint(1) NOT NULL ,
`active_since`  timestamp NULL ,
`created_by`  bigint(20) NULL ,
`modified_by`  bigint(20) NULL ,
`modification_justification`  text NULL ,
PRIMARY KEY (`id`),
CONSTRAINT `fs_location_fs_id_fk` FOREIGN KEY (`funding_source_id`) REFERENCES `funding_sources` (`id`),
CONSTRAINT `fs_location_element_fk` FOREIGN KEY (`loc_element_id`) REFERENCES `loc_elements` (`id`),
CONSTRAINT `fs_location_created_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
CONSTRAINT `fs_location_modified_by` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
)
;

