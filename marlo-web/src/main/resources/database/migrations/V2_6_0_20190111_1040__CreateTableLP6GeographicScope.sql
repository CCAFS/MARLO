CREATE TABLE IF NOT EXISTS `lp6_contribution_geographic_scope` (
`id`  bigint(20) NOT NULL ,
`lp6_contribution_id`  bigint(20) NULL ,
`loc_element_id`  bigint(20) NULL ,
`id_phase`  bigint(20) NULL ,
PRIMARY KEY (`id`),
CONSTRAINT `lp6_contribution_geographic_regions_ibfk_1` FOREIGN KEY (`lp6_contribution_id`) REFERENCES `project_lp6_contribution` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
CONSTRAINT `lp6_contribution_geographic_regions_ibfk_2` FOREIGN KEY (`loc_element_id`) REFERENCES `loc_elements` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
CONSTRAINT `lp6_contribution_geographic_regions_ibfk_3` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
INDEX `lp6_contribution_id` (`lp6_contribution_id`) USING BTREE ,
INDEX `loc_element_id` (`loc_element_id`) USING BTREE ,
INDEX `id_phase` (`id_phase`) USING BTREE 
)
;
