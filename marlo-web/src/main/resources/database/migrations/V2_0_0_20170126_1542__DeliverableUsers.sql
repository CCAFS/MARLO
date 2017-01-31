CREATE TABLE `deliverable_users` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`deliverable_id`  bigint(20) NOT NULL ,
`first_name`  varchar(500) NOT NULL ,
`last_name`  varchar(500) NOT NULL ,
`element_id`  text NOT NULL ,
PRIMARY KEY (`id`),
FOREIGN KEY (`deliverable_id`) REFERENCES `deliverables` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
)
;

