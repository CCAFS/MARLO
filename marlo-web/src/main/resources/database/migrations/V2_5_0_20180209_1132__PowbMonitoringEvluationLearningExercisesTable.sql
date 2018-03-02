DROP TABLE IF EXISTS powb_monitoring_evaluation_learning_exercises;
CREATE TABLE `powb_monitoring_evaluation_learning_exercises` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`powb_monitoring_evaluation_learning_id`  bigint(20) NOT NULL ,
`exercise`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`comments`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`is_active`  tinyint(1) NOT NULL ,
`active_since`  timestamp NULL DEFAULT NULL ,
`created_by`  bigint(20) NULL DEFAULT NULL ,
`modified_by`  bigint(20) NULL DEFAULT NULL ,
`modification_justification`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
PRIMARY KEY (`id`),
CONSTRAINT `powb_exercises_created_by_fk` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
CONSTRAINT `powb_exercises_evidence_id_fk` FOREIGN KEY (`powb_monitoring_evaluation_learning_id`) REFERENCES `powb_monitoring_evaluation_learning` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
CONSTRAINT `powb_exercises_modified_by` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
)
;
