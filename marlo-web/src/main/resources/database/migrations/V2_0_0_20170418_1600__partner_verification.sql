CREATE TABLE `partner_requests` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`partner_name`  text NULL ,
`acronym`  varchar(10) NULL ,
`institution_type_id`  bigint(20) NULL ,
`institution_id`  bigint(20) NULL ,
`city`  text NULL ,
`loc_element_id`  bigint(20) NULL ,
`web_page`  text NULL ,
`acepted`  tinyint(1) NULL ,
`is_active`  tinyint(1) NOT NULL ,
`created_by`  bigint(20) NULL ,
`modified_by`  bigint(20) NULL ,
`modification_justification`  text NULL ,
PRIMARY KEY (`id`),
CONSTRAINT `p_request_inst_type_fk` FOREIGN KEY (`institution_type_id`) REFERENCES `institution_types` (`id`),
CONSTRAINT `p_request_inst_fk` FOREIGN KEY (`institution_id`) REFERENCES `institutions` (`id`),
CONSTRAINT `p_request_element_fk` FOREIGN KEY (`loc_element_id`) REFERENCES `loc_elements` (`id`),
CONSTRAINT `p_request_created_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
CONSTRAINT `p_request_modified_fk` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
)
;