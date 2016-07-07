CREATE TABLE `submissions` (
`id`  bigint NOT NULL AUTO_INCREMENT ,
`crp_pgroma_id`  bigint NOT NULL ,
`user_id`  bigint NOT NULL ,
`date_time`  timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'The date time when the report was made.' ,
`modification_justification`  mediumtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`cycle`  varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT 'Cycling period type.' ,
`year`  smallint(6) NULL COMMENT 'Year to which the report is happening.' ,
PRIMARY KEY (`id`),
FOREIGN KEY (`crp_pgroma_id`) REFERENCES `crp_programs` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
)
;

