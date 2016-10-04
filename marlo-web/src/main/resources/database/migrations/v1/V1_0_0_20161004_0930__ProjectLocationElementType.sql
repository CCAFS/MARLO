CREATE TABLE `project_location_element_types` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`project_id`  bigint(20) NULL ,
`loc_element_type_id`  bigint(20) NULL ,
`is_global`  tinyint(1) NULL ,
PRIMARY KEY (`id`),
CONSTRAINT `project_location_element_types_fk1` FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`),
CONSTRAINT `project_location_element_types_fk2` FOREIGN KEY (`loc_element_type_id`) REFERENCES `loc_element_types` (`id`)
)
;
