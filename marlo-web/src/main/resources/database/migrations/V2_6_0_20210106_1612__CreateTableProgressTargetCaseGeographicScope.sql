CREATE TABLE `progress_target_case_geographic_scope` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`progress_target_case_id`  bigint(20) NOT NULL ,
`rep_ind_geographic_scope_id`  bigint(20) NOT NULL ,
`id_phase`  bigint(20) NOT NULL ,
PRIMARY KEY (`id`),
CONSTRAINT `progress_target_case_geographic_scope_ibkf_1` FOREIGN KEY (`progress_target_case_id`) REFERENCES `report_synthesis_srf_progress_targets_cases` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
CONSTRAINT `progress_target_case_geographic_scope_ibkf_2` FOREIGN KEY (`rep_ind_geographic_scope_id`) REFERENCES `rep_ind_geographic_scopes` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
CONSTRAINT `progress_target_case_geographic_scope_ibkf_3` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
INDEX `progress_target_case_id` (`progress_target_case_id`) USING BTREE ,
INDEX `rep_ind_geographic_scope_id` (`rep_ind_geographic_scope_id`) USING BTREE ,
INDEX `id_phase` (`id_phase`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
;

