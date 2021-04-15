CREATE TABLE `project_expected_study_nexus` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT,
`expected_id`  bigint(20) NULL ,
`nexus_id`  bigint(20) NULL ,
PRIMARY KEY (`id`),
CONSTRAINT `expected_study_nexus_ibfk_1` FOREIGN KEY (`expected_id`) REFERENCES `project_expected_studies` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
CONSTRAINT `expected_study_nexus_ibfk_2` FOREIGN KEY (`nexus_id`) REFERENCES `nexus` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
INDEX `expected_id` (`expected_id`) USING BTREE ,
INDEX `nexus_id` (`nexus_id`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
;