
SET FOREIGN_KEY_CHECKS = 0;
CREATE TEMPORARY TABLE
IF NOT EXISTS table_c2 AS (SELECT * FROM project_cluster_activities);

TRUNCATE TABLE project_cluster_activities;



ALTER TABLE `project_cluster_activities`
ADD COLUMN `id_phase`  bigint(20) NULL AFTER `modification_justification`;

ALTER TABLE `project_cluster_activities` ADD FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`);

INSERT INTO project_cluster_activities (
 
  project_id,
cluster_activity_id,
is_active,
active_since,
created_by,
modified_by,
modification_justification,

  id_phase
) SELECT 

   t2.project_id,
 t2.cluster_activity_id,
 t2.is_active,
 t2.active_since,
 t2.created_by,
 t2.modified_by,
 t2.modification_justification,
  ph.id
FROM
  table_c2 t2
left JOIN project_phases pp ON pp.project_id = t2.project_id
left JOIN phases ph ON ph.id = pp.id_phase
;










