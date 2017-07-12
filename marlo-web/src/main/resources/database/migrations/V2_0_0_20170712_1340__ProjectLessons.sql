
SET FOREIGN_KEY_CHECKS = 0;
CREATE TEMPORARY TABLE
IF NOT EXISTS table2 AS (SELECT * FROM project_component_lessons);

CREATE TEMPORARY TABLE
IF NOT EXISTS table3 AS (SELECT * FROM project_component_lessons);

TRUNCATE TABLE project_component_lessons;

ALTER TABLE `project_component_lessons`
ADD COLUMN `id_phase`  bigint(20) NULL AFTER `modification_justification`;

ALTER TABLE `project_component_lessons` ADD FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`);

INSERT INTO project_component_lessons (
 project_id,
component_name,
lessons,
year,
is_active,
active_since,
created_by,
modified_by,
modification_justification,
cycle,
ip_program_id,
project_outcome_id,
id_phase
) 
SELECT 
distinct 
 t2.project_id,
t2.component_name,
t2.lessons,
t2.year,
t2.is_active,
t2.active_since,
t2.created_by,
t2.modified_by,
t2.modification_justification,
t2.cycle,
t2.ip_program_id,
t2.project_outcome_id,
  ph.id
FROM
  table2 t2
inner join projects p on p.id=t2.project_id 
inner join  phases ph ON ph.description = t2.cycle and  t2.year=ph.year and ph.crp_id=p.crp_id
WHERE t2.project_id is not null 
union 
SELECT 
distinct 
 t3.project_id,
t3.component_name,
t3.lessons,
t3.year,
t3.is_active,
t3.active_since,
t3.created_by,
t3.modified_by,
t3.modification_justification,
t3.cycle,
t3.ip_program_id,
t3.project_outcome_id,
  ph.id
FROM
  table3 t3
inner join project_outcomes p on p.id=t3.project_outcome_id 
inner join projects pr on pr.id=p.project_id
inner join  phases ph ON ph.description = t3.cycle and  t3.year=ph.year and ph.crp_id=pr.crp_id
WHERE t3.project_outcome_id is not null 
