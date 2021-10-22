CREATE TABLE `deliverable_project_outcomes` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`deliverable_id`  bigint(20) NOT NULL ,
`project_outcome_id`  bigint(20) NOT NULL ,
`id_phase`  bigint(20) NULL ,
PRIMARY KEY (`id`),
CONSTRAINT `deliverable_project_outcomes_ibfk_1` FOREIGN KEY (`deliverable_id`) REFERENCES `deliverables` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
CONSTRAINT `deliverable_project_outcomes_ibfk_2` FOREIGN KEY (`project_outcome_id`) REFERENCES `project_outcomes` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
CONSTRAINT `deliverable_project_outcomes_ibfk_3` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
INDEX `deliverable_id` (`deliverable_id`) USING BTREE ,
INDEX `project_outcome_id` (`project_outcome_id`) USING BTREE ,
INDEX `id_phase` (`id_phase`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
;