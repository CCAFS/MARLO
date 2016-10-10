CREATE TABLE `roles` (
`id`  bigint NOT NULL AUTO_INCREMENT ,
`description`  varchar(500) NOT NULL ,
`acronym`  varchar(500) NOT NULL ,
`value`  text NOT NULL ,
`crp_id`  bigint NOT NULL ,
PRIMARY KEY (`id`),
FOREIGN KEY (`crp_id`) REFERENCES `crps` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
)
;

