CREATE TABLE `project_expected_study_lever_outcomes` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`expected_id`  bigint(20) NULL ,
`lever_outcome_id`  bigint(20) NULL ,
PRIMARY KEY (`id`),
CONSTRAINT `expected_study_lever_outcomes_ibfk_1` FOREIGN KEY (`expected_id`) REFERENCES `project_expected_studies` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
CONSTRAINT `expected_study_lever_outcomes_ibfk_2` FOREIGN KEY (`lever_outcome_id`) REFERENCES `lever_outcomes` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
INDEX `expected_id` (`expected_id`) USING BTREE ,
INDEX `lever_outcome_id` (`lever_outcome_id`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
;