DROP TABLE IF EXISTS `powb_management_risk`;
CREATE TABLE `powb_management_risk` (
`id`  bigint(20) NOT NULL ,
`highlight`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`is_active`  tinyint(1) NOT NULL ,
`created_by`  bigint(20) NULL DEFAULT NULL ,
`modified_by`  bigint(20) NULL DEFAULT NULL ,
`active_since`  timestamp NULL DEFAULT NULL ,
`modification_justification`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
PRIMARY KEY (`id`),
CONSTRAINT `powb_risk_synthesis_id_fk` FOREIGN KEY (`id`) REFERENCES `powb_synthesis` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
CONSTRAINT `powb_risk_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
CONSTRAINT `powb_risk_modified_by_fk` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
)
;