CREATE TABLE `crp_parameters` (
`id`  bigint NOT NULL AUTO_INCREMENT ,
`crp_id`  bigint NOT NULL ,
`key`  varchar(500) NOT NULL ,
`value`  text NOT NULL ,
PRIMARY KEY (`id`),
FOREIGN KEY (`crp_id`) REFERENCES `crps` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
)
;

