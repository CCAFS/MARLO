CREATE TABLE `project_locations` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`project_id`  bigint(20) NULL ,
`loc_element_id`  bigint(20) NULL ,
`is_active`  tinyint(1) NOT NULL ,
`active_since`  timestamp NOT NULL ,
`created_by`  bigint(20) NULL ,
`modified_by`  bigint(20) NULL ,
`modification_justification`  text NULL ,
PRIMARY KEY (`id`),
CONSTRAINT `project_location_id_fk` FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`),
CONSTRAINT `project_location_element_fk` FOREIGN KEY (`loc_element_id`) REFERENCES `loc_elements` (`id`),
CONSTRAINT `project_location_created_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
CONSTRAINT `project_location_modified_fk` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
)
;