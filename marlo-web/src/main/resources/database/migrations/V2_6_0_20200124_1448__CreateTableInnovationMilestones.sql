CREATE TABLE `project_innovation_milestones` (
`id`  bigint(20) NOT NULL ,
`project_innovation_id`  bigint(20) NOT NULL ,
`crp_milestone_id`  bigint(20) NOT NULL ,
`id_phase`  bigint(20) NULL ,
PRIMARY KEY (`id`),
CONSTRAINT `project_innovation_milestones_ibfk_1` FOREIGN KEY (`project_innovation_id`) REFERENCES `project_innovations` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
CONSTRAINT `project_innovation_milestones_ibfk_2` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
CONSTRAINT `project_innovation_milestones_ibfk_3` FOREIGN KEY (`crp_milestone_id`) REFERENCES `crp_milestones` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
INDEX `project_innovation_id` (`project_innovation_id`) USING BTREE ,
INDEX `id_phase` (`id_phase`) USING BTREE ,
INDEX `crp_milestone_id` (`crp_milestone_id`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
;

