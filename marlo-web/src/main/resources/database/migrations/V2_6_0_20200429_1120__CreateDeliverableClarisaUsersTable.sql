CREATE TABLE `deliverable_clarisa_users` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`deliverable_id`  bigint(20) NOT NULL ,
`id_phase`  bigint(20) NULL ,
`first_name`  varchar(500) NULL ,
`last_name`  varchar(500) NULL ,
`element_id`  text NULL ,
PRIMARY KEY (`id`),
CONSTRAINT `deliverable_clarisa_users_ibfk_1` FOREIGN KEY (`deliverable_id`) REFERENCES `deliverables` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
CONSTRAINT `deliverable_clarisa_users_ibfk_2` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
INDEX `deliverable_id` (`deliverable_id`) USING BTREE ,
INDEX `id_phase` (`id_phase`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
;