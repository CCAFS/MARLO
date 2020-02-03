CREATE TABLE `project_innovation_sub_idos` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`project_innovation_id`  bigint(20) NOT NULL ,
`sub_ido_id`  bigint(20) NOT NULL ,
`is_primary`  tinyint(1) NULL ,
`id_phase`  bigint(20) NULL ,
PRIMARY KEY (`id`),
CONSTRAINT `project_innovation_sub_idos_ibfk_1` FOREIGN KEY (`project_innovation_id`) REFERENCES `project_innovations` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
CONSTRAINT `project_innovation_sub_idos_ibfk_2` FOREIGN KEY (`sub_ido_id`) REFERENCES `srf_sub_idos` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
CONSTRAINT `project_innovation_sub_idos_ibfk_3` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
INDEX `project_innovation_id` (`project_innovation_id`) USING BTREE ,
INDEX `sub_ido_id` (`sub_ido_id`) USING BTREE ,
INDEX `id_phase` (`id_phase`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
;

