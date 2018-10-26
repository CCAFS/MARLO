SET FOREIGN_KEY_CHECKS = 0;
drop table if exists project_highlights_info;
CREATE TABLE `project_highlights_info` (
`id`  bigint(11) NOT NULL AUTO_INCREMENT ,
`project_highlight_id`  bigint(11) NULL ,
`id_phase`  bigint(20) NULL ,
`title`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`author`  text CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL ,
`start_date`  date NULL DEFAULT NULL ,
`end_date`  date NULL DEFAULT NULL ,
`description`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`results`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`is_global`  tinyint(1) NOT NULL ,
`publisher`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`objectives`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`partners`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`file_id`  bigint(20) NULL DEFAULT NULL ,
`links`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`keywords`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`subject`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL ,
`year`  bigint(20) NULL DEFAULT NULL ,
`status`  bigint(20) NULL DEFAULT NULL ,
PRIMARY KEY (`id`),
FOREIGN KEY (`file_id`) REFERENCES `files` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
FOREIGN KEY (`project_highlight_id`) REFERENCES `project_highlights` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
)


 ENGINE = INNODB DEFAULT CHARSET = utf8;


INSERT INTO project_highlights_info (project_highlight_id,
id_phase,
title,
author,
start_date,
end_date,
description,
results,
is_global,
publisher,
objectives,
partners,
file_id,
links,
keywords,
subject,
year,
status)
select phi.id,
ph.id,
phi.title,
phi.author,
phi.start_date,
phi.end_date,
phi.description,
phi.results,
phi.is_global,
phi.publisher,
phi.objectives,
phi.partners,
phi.file_id,
phi.links,
phi.keywords,
phi.subject,
phi.year,
phi.status
FROM project_highlights phi
inner join   projects p on  p.id=phi.project_id INNER JOIN project_phases pp on pp.project_id=p.id
INNER JOIN phases ph on ph.id=pp.id_phase 
where p.is_active=1 ;

ALTER TABLE `project_highlights` DROP FOREIGN KEY `project_highlights_ibfk_2`;

ALTER TABLE `project_highlights`
DROP COLUMN `title`,
DROP COLUMN `author`,
DROP COLUMN `start_date`,
DROP COLUMN `end_date`,
DROP COLUMN `description`,
DROP COLUMN `results`,
DROP COLUMN `is_global`,
DROP COLUMN `publisher`,
DROP COLUMN `objectives`,
DROP COLUMN `partners`,
DROP COLUMN `file_id`,
DROP COLUMN `links`,
DROP COLUMN `keywords`,
DROP COLUMN `subject`,
DROP COLUMN `year`,
DROP COLUMN `status`;

