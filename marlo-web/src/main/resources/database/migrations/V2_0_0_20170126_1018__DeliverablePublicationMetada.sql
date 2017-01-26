CREATE TABLE `deliverable_publications_metada` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`deliverable_id`  bigint(20) NOT NULL ,
`volume`  text NULL ,
`issue`  text NULL ,
`pages`  text NULL ,
`journal`  text NULL ,
`isi_publication`  tinyint NULL ,
`nasr`  tinyint NULL ,
`co_author`  tinyint NULL ,
PRIMARY KEY (`id`),
FOREIGN KEY (`deliverable_id`) REFERENCES `deliverables` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
)
;

CREATE TABLE `deliverable_crps` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`deliverable_id`  bigint(20) NOT NULL ,
`crp_id`  bigint(20) NOT NULL ,
`crp_program`  bigint(20) NOT NULL ,
PRIMARY KEY (`id`),
FOREIGN KEY (`deliverable_id`) REFERENCES `deliverables` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
FOREIGN KEY (`crp_id`) REFERENCES `crps` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
FOREIGN KEY (`crp_program`) REFERENCES `crp_programs` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
)
;

