CREATE TABLE `cluster_types` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`name`  text NULL ,
PRIMARY KEY (`id`),
INDEX `id` (`id`) USING BTREE 
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8 COLLATE=utf8_unicode_ci
;