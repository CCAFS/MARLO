CREATE TABLE `lever_outcomes` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT,
`name`  varchar(255) NULL ,
`parent_id`  bigint(20) NULL ,
`description`  text NULL ,
`indicator`  text NULL ,
PRIMARY KEY (`id`),
INDEX `lever_outcomes_parent_id` (`parent_id`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
;