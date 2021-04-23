CREATE TABLE `report_synthesis_srf_progress_target_contributions` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`report_synthesis_srf_progress_id`  bigint(20) NULL ,
`srf_slo_indicator_targets_id`  bigint(20) NULL ,
`has_evidence`  tinyint(1) NULL ,
PRIMARY KEY (`id`),
CONSTRAINT `report_synthesis_srf_progress_contribution_ibfk_1` FOREIGN KEY (`report_synthesis_srf_progress_id`) REFERENCES `report_synthesis_srf_progress` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
CONSTRAINT `report_synthesis_srf_progress_contribution_ibfk_2` FOREIGN KEY (`srf_slo_indicator_targets_id`) REFERENCES `srf_slo_indicator_targets` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
INDEX `report_synthesis_srf_progress_id` (`report_synthesis_srf_progress_id`) USING BTREE ,
INDEX `srf_slo_indicator_targets_id` (`srf_slo_indicator_targets_id`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
;

