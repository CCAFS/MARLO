CREATE TABLE `activities_titles` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`title`  text NOT NULL ,
`is_active` tinyint(1) NOT NULL ,
PRIMARY KEY (`id`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_general_ci
;