CREATE TABLE `liaison_institutions_all` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`liason_institution`  bigint(20) NOT NULL ,
`institution`  bigint(20) NOT NULL ,
PRIMARY KEY (`id`),
FOREIGN KEY (`liason_institution`) REFERENCES `liaison_institutions` (`id`),
FOREIGN KEY (`institution`) REFERENCES `institutions` (`id`)
)
;

