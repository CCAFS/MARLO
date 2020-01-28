CREATE TABLE `project_expected_study_milestones` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`expected_id`  bigint(20) NOT NULL ,
`crp_milestone_id`  bigint(20) NOT NULL ,
`id_phase`  bigint(20) NULL ,
PRIMARY KEY (`id`),
CONSTRAINT `project_expected_study_milestones_ibfk_1` FOREIGN KEY (`expected_id`) REFERENCES `project_expected_studies` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
CONSTRAINT `project_expected_study_milestones_ibfk_2` FOREIGN KEY (`crp_milestone_id`) REFERENCES `crp_milestones` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
CONSTRAINT `project_expected_study_milestones_ibfk_3` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
INDEX `expected_id` (`expected_id`) USING BTREE ,
INDEX `id_phase` (`id_phase`) USING BTREE ,
INDEX `crp_milestone_id` (`crp_milestone_id`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
;

