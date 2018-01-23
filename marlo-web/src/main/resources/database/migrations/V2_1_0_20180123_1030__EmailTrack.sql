CREATE TABLE `email_logs` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`to`  text NULL ,
`cc`  text NULL ,
`bbc`  text NULL ,
`subject`  text NULL ,
`message`  text NULL ,
`tried`  int NULL ,
`date`  datetime NULL ,
`error`  text NULL ,
`succes`  tinyint(1) NULL ,
PRIMARY KEY (`id`)
)
;

