SET FOREIGN_KEY_CHECKS = 0;

CREATE TEMPORARY TABLE
IF NOT EXISTS table_project_budgets AS (SELECT * FROM project_budgets);

ALTER TABLE `project_budgets`
ADD COLUMN `id_phase`  bigint(20) NULL AFTER `modification_justification`;


CREATE TEMPORARY TABLE
IF NOT EXISTS table_project_budgets_cluser_actvities AS (SELECT * FROM project_budgets_cluser_actvities);

ALTER TABLE `project_budgets_cluser_actvities`
ADD COLUMN `id_phase`  bigint(20) NULL AFTER `modification_justification`;


TRUNCATE TABLE project_budgets;
TRUNCATE TABLE project_budgets_cluser_actvities;


INSERT INTO project_budgets (
project_id,
amount,
budget_type,
year,
funding_source_id,
institution_id,
gender_percentage,
is_active,
active_since,
created_by,
modified_by,
modification_justification,
id_phase
) SELECT 
t2.project_id,
t2.amount,
t2.budget_type,
t2.year,
t2.funding_source_id,
t2.institution_id,
t2.gender_percentage,
t2.is_active,
t2.active_since,
t2.created_by,
t2.modified_by,
t2.modification_justification,

ph.id
FROM
  table_project_budgets t2
left JOIN project_phases pp ON pp.project_id = t2.project_id
left JOIN phases ph ON ph.id = pp.id_phase
;

INSERT INTO project_budgets_cluser_actvities (
project_id,
amount,
budget_type,
year,
cluster_activity_id,
gender_percentage,
is_active,
active_since,
created_by,
modified_by,
modification_justification,

id_phase
) SELECT 
t2.project_id,
t2.amount,
t2.budget_type,
t2.year,
t2.cluster_activity_id,
t2.gender_percentage,
t2.is_active,
t2.active_since,
t2.created_by,
t2.modified_by,
t2.modification_justification,


ph.id
FROM
  table_project_budgets_cluser_actvities t2
left JOIN project_phases pp ON pp.project_id = t2.project_id
left JOIN phases ph ON ph.id = pp.id_phase
;

