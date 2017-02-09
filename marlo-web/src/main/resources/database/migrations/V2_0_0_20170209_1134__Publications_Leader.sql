CREATE TABLE `deliverable_leaders` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`deliverable_id`  bigint(20) NOT NULL ,
`instituion_id`  bigint(20) NOT NULL ,
PRIMARY KEY (`id`),
FOREIGN KEY (`deliverable_id`) REFERENCES `deliverables` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
FOREIGN KEY (`instituion_id`) REFERENCES `institutions` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
)
;

CREATE TABLE `deliverable_programs` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`deliverable_id`  bigint(20) NOT NULL ,
`ip_program_id`  bigint(20) NOT NULL ,
PRIMARY KEY (`id`),
FOREIGN KEY (`deliverable_id`) REFERENCES `deliverables` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
FOREIGN KEY (`ip_program_id`) REFERENCES `ip_programs` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
)
;

