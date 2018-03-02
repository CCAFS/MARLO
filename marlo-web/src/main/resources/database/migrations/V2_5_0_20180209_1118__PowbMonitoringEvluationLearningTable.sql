DROP TABLE IF EXISTS powb_monitoring_evaluation_learning;
CREATE TABLE `powb_monitoring_evaluation_learning` (
`id`  bigint(20) NOT NULL ,
`highlight`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`is_active`  tinyint(1) NOT NULL ,
`active_since`  timestamp NULL DEFAULT NULL ,
`created_by`  bigint(20) NULL DEFAULT NULL ,
`modified_by`  bigint(20) NULL DEFAULT NULL ,
`modification_justification`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
PRIMARY KEY (`id`),
CONSTRAINT `powb_mel_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
CONSTRAINT `powb_mel_id_fk` FOREIGN KEY (`id`) REFERENCES `powb_synthesis` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
CONSTRAINT `powb_mel_modified_by` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
)
;