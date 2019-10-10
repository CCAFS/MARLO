CREATE TABLE `funding_source_divisions` (
`id`  bigint(20) NOT NULL ,
`funding_source_id`  bigint(20) NOT NULL ,
`division_id`  bigint(20) NOT NULL ,
`phase_id`  bigint(20) NULL ,
PRIMARY KEY (`id`),
CONSTRAINT `funding_source_divisions_ibfk_1` FOREIGN KEY (`funding_source_id`) REFERENCES `funding_sources` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
CONSTRAINT `funding_source_divisions_ibfk_2` FOREIGN KEY (`division_id`) REFERENCES `partner_divisions` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
CONSTRAINT `funding_source_divisions_ibfk_3` FOREIGN KEY (`phase_id`) REFERENCES `phases` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
INDEX `funding_source_id` (`funding_source_id`) USING BTREE ,
INDEX `division_id` (`division_id`) USING BTREE ,
INDEX `id_phase` (`phase_id`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
;

