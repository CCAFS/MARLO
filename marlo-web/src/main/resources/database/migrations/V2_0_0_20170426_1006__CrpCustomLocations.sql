CREATE TABLE `crp_loc_element_types` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`crp_id`  bigint(20) NOT NULL ,
`loc_element_type_id`  bigint(20) NOT NULL ,
PRIMARY KEY (`id`),
FOREIGN KEY (`crp_id`) REFERENCES `crps` (`id`),
FOREIGN KEY (`loc_element_type_id`) REFERENCES `loc_element_types` (`id`)
)
ENGINE=InnoDB
;






