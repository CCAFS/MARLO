SET FOREIGN_KEY_CHECKS = 0;
ALTER TABLE `project_leverage`
ADD COLUMN `composed_id`  varchar(20)  NULL AFTER `modification_justification`;

update  project_leverage pl 
set pl.composed_id=CONCAT(pl.project_id,'-',pl.id) ;

CREATE TEMPORARY TABLE
IF NOT EXISTS table_leverages_project AS (SELECT * FROM project_leverage);

TRUNCATE TABLE project_leverage;

ALTER TABLE `project_leverage`
ADD COLUMN `id_phase`  bigint(20) NULL AFTER `modification_justification`;

ALTER TABLE `project_leverage` ADD FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`);

INSERT INTO project_leverage ( 
project_id,
title,
institution,
year,
flagship,
budget,
is_active,
active_since,
created_by,
modified_by,
modification_justification,
composed_id,
id_phase
) SELECT
t2.project_id,
t2.title,
t2.institution,
t2.year,
t2.flagship,
t2.budget,
t2.is_active,
t2.active_since,
t2.created_by,
t2.modified_by,
t2.modification_justification,
t2.composed_id,
ph.id
FROM
  table_leverages_project t2
left JOIN project_phases pp ON pp.project_id = t2.project_id
left JOIN phases ph ON ph.id = pp.id_phase;