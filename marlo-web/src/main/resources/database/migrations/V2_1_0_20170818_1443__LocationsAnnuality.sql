
SET FOREIGN_KEY_CHECKS = 0;

CREATE TEMPORARY TABLE
IF NOT EXISTS table_locations AS (SELECT * FROM project_locations);

TRUNCATE TABLE project_locations;


ALTER TABLE `project_locations`
ADD COLUMN `id_phase`  bigint(20) NULL AFTER `modification_justification`;

ALTER TABLE `project_locations` ADD FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`);

INSERT INTO project_locations (
project_id,
loc_element_type_id,
loc_element_id,
is_active,
active_since,
created_by,
modified_by,
modification_justification,
id_phase
) SELECT 

t2.project_id,
t2.loc_element_type_id,
t2.loc_element_id,
t2.is_active,
t2.active_since,
t2.created_by,
t2.modified_by,
t2.modification_justification,
ph.id
FROM
  table_locations t2
left JOIN project_phases pp ON pp.project_id = t2.project_id
left JOIN phases ph ON ph.id = pp.id_phase
;

