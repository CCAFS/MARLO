CREATE TABLE `project_innovation_project_outcomes` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`project_innovation_id`  bigint(20) NOT NULL ,
`project_outcome_id`  bigint(20) NOT NULL ,
`id_phase`  bigint(20) NULL ,
PRIMARY KEY (`id`),
CONSTRAINT `project_innovation_project_outcomes_ibfk_1` FOREIGN KEY (`project_innovation_id`) REFERENCES `project_innovations` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
CONSTRAINT `project_innovation_project_outcomes_ibfk_2` FOREIGN KEY (`project_outcome_id`) REFERENCES `project_outcomes` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
CONSTRAINT `project_innovation_project_outcomes_ibfk_3` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
INDEX `project_innovation_id` (`project_innovation_id`) USING BTREE ,
INDEX `project_outcome_id` (`project_outcome_id`) USING BTREE ,
INDEX `id_phase` (`id_phase`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
;