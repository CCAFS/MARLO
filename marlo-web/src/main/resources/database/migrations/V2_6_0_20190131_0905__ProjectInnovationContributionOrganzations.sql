CREATE TABLE `project_innovation_contributing_organizations` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`project_innovation_id`  bigint(20) NULL ,
`id_phase`  bigint(20) NULL ,
`institution_id`  bigint(20) NULL ,
CONSTRAINT `project_innovation_contributing_organizations_ibfk_1` FOREIGN KEY (`project_innovation_id`) REFERENCES `project_innovations` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
CONSTRAINT `project_innovation_contributing_organizations_ibfk_2` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
CONSTRAINT `project_innovation_contributing_organizations_ibfk_3` FOREIGN KEY (`institution_id`) REFERENCES `institutions` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
INDEX `project_innovation_id` (`project_innovation_id`) USING BTREE ,
INDEX `id_phase` (`id_phase`) USING BTREE ,
INDEX `institution_id` (`institution_id`) USING BTREE 
)
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
;