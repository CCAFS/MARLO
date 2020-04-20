CREATE TABLE `url_synthesis_log` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`report_synthesis_id`  bigint(20) NULL ,
`liaison_institution_id`  bigint(20) NULL ,
`global_unit_id`  bigint(20) NULL ,
`synthesis_section`  text NULL ,
`error_text`  text NULL ,
`exception_text`  text NULL ,
`phase_id`  bigint(20) NULL ,
PRIMARY KEY (`id`),
CONSTRAINT `synthesis_log_ibfk1` FOREIGN KEY (`report_synthesis_id`) REFERENCES `report_synthesis` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
CONSTRAINT `synthesis_log_ibfk2` FOREIGN KEY (`liaison_institution_id`) REFERENCES `liaison_institutions` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
CONSTRAINT `synthesis_log_ibfk3` FOREIGN KEY (`global_unit_id`) REFERENCES `global_units` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
CONSTRAINT `synthesis_log_ibfk4` FOREIGN KEY (`phase_id`) REFERENCES `phases` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
INDEX `synthesis_log_fk1` (`report_synthesis_id`) USING BTREE ,
INDEX `synthesis_log_fk2` (`liaison_institution_id`) USING BTREE ,
INDEX `synthesis_log_fk3` (`global_unit_id`) USING BTREE ,
INDEX `synthesis_log_fk4` (`phase_id`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
;