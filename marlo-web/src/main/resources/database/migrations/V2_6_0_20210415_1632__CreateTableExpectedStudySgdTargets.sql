CREATE TABLE `project_expected_study_sdg_targets` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`expected_id`  bigint(20) NULL ,
`sdg_target_id`  int(11) NULL ,
PRIMARY KEY (`id`),
CONSTRAINT `expected_study_sgd_target_ibfk_1` FOREIGN KEY (`expected_id`) REFERENCES `project_expected_studies` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
CONSTRAINT `expected_study_sgd_target_ibfk_2` FOREIGN KEY (`sdg_target_id`) REFERENCES `sdg_targets` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
INDEX `expected_id` (`expected_id`) USING BTREE ,
INDEX `sdg_target_id` (`sdg_target_id`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
;

