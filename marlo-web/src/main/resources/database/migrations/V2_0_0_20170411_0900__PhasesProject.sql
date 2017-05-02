CREATE TABLE `phases` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`description`  text NOT NULL ,
`year`  int NOT NULL ,
`crp_id`  bigint NOT NULL ,
PRIMARY KEY (`id`),
FOREIGN KEY (`crp_id`) REFERENCES `crps` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
)
;

CREATE TABLE `project_phases` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`id_phase`  bigint(20) NOT NULL ,
`project_id`  bigint(20) NOT NULL ,
PRIMARY KEY (`id`),
FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
)
;

