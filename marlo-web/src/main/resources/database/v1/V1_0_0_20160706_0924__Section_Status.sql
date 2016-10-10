CREATE TABLE `section_statuses` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`crp_program_id`  bigint(20) NULL ,
`section_name`  varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'Some section name (would be the action name)' ,
`missing_fields`  mediumtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT 'The list of missing fields per section' ,
`cycle`  varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`year`  int(11) NULL ,
PRIMARY KEY (`id`),
FOREIGN KEY (`crp_program_id`) REFERENCES `crp_programs` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
)
;

