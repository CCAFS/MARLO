CREATE TEMPORARY TABLE
IF NOT EXISTS table2 AS (SELECT * FROM project_focuses);

TRUNCATE TABLE project_focuses;



ALTER TABLE `project_focuses`
ADD COLUMN `id_phase`  bigint(20) NULL AFTER `modification_justification`;

ALTER TABLE `project_focuses` ADD FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`);

INSERT INTO project_focuses (
 
  project_id,
  program_id,
  is_active,
  active_since,
  created_by,
  modified_by,
  modification_justification,
  id_phase
) SELECT 

  t2.project_id,
  t2.program_id,
  t2.is_active,
  t2.active_since,
  t2.created_by,
  t2.modified_by,
  t2.modification_justification,
  ph.id
FROM
  table2 t2
left JOIN project_phases pp ON pp.project_id = t2.project_id
left JOIN phases ph ON ph.id = pp.id_phase
;
