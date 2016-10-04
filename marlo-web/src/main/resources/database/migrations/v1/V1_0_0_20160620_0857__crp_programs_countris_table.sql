CREATE TABLE `crp_program_countries` (
`id`  bigint NOT NULL AUTO_INCREMENT ,
`loc_element_id`  bigint NOT NULL ,
`program_id`  bigint NOT NULL ,
`is_active` tinyint(1) NOT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `active_since` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified_by` bigint(20) NOT NULL,
  `modification_justification` text NOT NULL,
PRIMARY KEY (`id`),
FOREIGN KEY (`loc_element_id`) REFERENCES `loc_elements` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
FOREIGN KEY (`program_id`) REFERENCES `crp_programs` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
)
;

