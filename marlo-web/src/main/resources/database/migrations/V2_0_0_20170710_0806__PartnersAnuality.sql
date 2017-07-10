
SET FOREIGN_KEY_CHECKS = 0;
CREATE TEMPORARY TABLE
IF NOT EXISTS table2 AS (SELECT * FROM project_partners);

TRUNCATE TABLE project_partners;



ALTER TABLE `project_partners`
ADD COLUMN `id_phase`  bigint(20) NULL AFTER `modification_justification`;

ALTER TABLE `project_partners` ADD FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`);

INSERT INTO project_partners (
 
 project_id,
institution_id,
is_active,
active_since,
created_by,
modified_by,
modification_justification,
responsibilities,


  id_phase
) SELECT 

t2.project_id,
t2.institution_id,
t2.is_active,
t2.active_since,
t2.created_by,
t2.modified_by,
t2.modification_justification,
t2.responsibilities,

  ph.id
FROM
  table2 t2
left JOIN project_phases pp ON pp.project_id = t2.project_id
left JOIN phases ph ON ph.id = pp.id_phase
;
