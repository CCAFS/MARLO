CREATE TABLE `progress_target_case_geographic_region` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`progress_target_case_id`  bigint(20) NULL ,
`loc_element_id`  bigint(20) NULL ,
`id_phase`  bigint(20) NULL ,
PRIMARY KEY (`id`),
CONSTRAINT `progress_target_case_regions_ibfk_1` FOREIGN KEY (`progress_target_case_id`) REFERENCES `report_synthesis_srf_progress_targets_cases` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
CONSTRAINT `progress_target_case_regions_ibfk_2` FOREIGN KEY (`loc_element_id`) REFERENCES `loc_elements` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
CONSTRAINT `progress_target_case_regions_ibfk_3` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
INDEX `progress_target_case_id` (`progress_target_case_id`) USING BTREE ,
INDEX `loc_element_id` (`loc_element_id`) USING BTREE ,
INDEX `id_phase` (`id_phase`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
;