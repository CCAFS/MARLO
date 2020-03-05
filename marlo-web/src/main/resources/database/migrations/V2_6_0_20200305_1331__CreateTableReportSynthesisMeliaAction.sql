CREATE TABLE `report_synthesis_melia_action_studies` (
`id`  bigint(20) NOT NULL ,
`report_synthesis_melia_id`  bigint(20) NULL ,
`project_expected_studies_id`  bigint(20) NULL ,
`is_active` tinyint (20) NULL ,
`created_by`  bigint(20) NULL ,
`active_since`  timestamp NULL ,
`modified_by`  bigint(20) NULL ,
`modification_justification` text NULL ,
PRIMARY KEY (`id`),
CONSTRAINT `report_synthesis_melia_actions_studies_ibfk_1` FOREIGN KEY (`report_synthesis_melia_id`) REFERENCES `report_synthesis_melia` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
CONSTRAINT `report_synthesis_melia_actions_studies_ibfk_2` FOREIGN KEY (`project_expected_studies_id`) REFERENCES `project_expected_studies` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
CONSTRAINT `report_synthesis_melia_actions_studies_ibfk_3` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
CONSTRAINT `report_synthesis_melia_actions_studies_ibfk_4` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
INDEX `report_synthesis_melia_id` (`report_synthesis_melia_id`) USING BTREE ,
INDEX `project_expected_studies_id` (`project_expected_studies_id`) USING BTREE,
INDEX `created_by` (`created_by`) USING BTREE,
INDEX `modified_by` (`modified_by`) USING BTREE
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
;

