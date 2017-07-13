SET FOREIGN_KEY_CHECKS = 0;

CREATE TEMPORARY TABLE
IF NOT EXISTS table2 AS (SELECT * FROM crp_program_outcomes);



CREATE TEMPORARY TABLE
IF NOT EXISTS table_temp_crp_cluster_key_outputs_outcome AS (
SELECT
  ppp.*,pp.description,pp.crp_program_id
FROM
  crp_cluster_key_outputs_outcome pp
INNER JOIN crp_program_outcomes ppp ON pp.id = ppp.project_partner_id)
;

TRUNCATE TABLE crp_program_outcomes;
TRUNCATE TABLE crp_cluster_key_outputs_outcome ;



ALTER TABLE `crp_program_outcomes`
ADD COLUMN `id_phase`  bigint(20) NULL AFTER `modification_justification`;

ALTER TABLE `crp_program_outcomes` ADD FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`);

INSERT INTO crp_program_outcomes (
 
description,
`year`,
`value`,
target_unit_id,
crp_program_id,
is_active,
created_by,
active_since,
modified_by,
modification_justification,
id_phase
) SELECT 

t2.description,
t2.`year`,
t2.`value`,
t2.target_unit_id,
t2.crp_program_id,
t2.is_active,
t2.created_by,
t2.active_since,
t2.modified_by,
t2.modification_justification,
  ph.id
FROM
  table2 t2
  inner join crp_programs pr on pr.id=t2.crp_program_id
inner JOIN phases ph ON ph.crp_id=pr.crp_id
;


insert into crp_cluster_key_outputs_outcome (contribution,
key_output_id,
outcome_id,
is_active,
created_by,
active_since,
modified_by,
modification_justification

)
select distinct 
temp.contribution,
temp.key_output_id,
pp.id,
temp.is_active,
temp.created_by,
temp.active_since,
temp.modified_by,
temp.modification_justification

from table_temp_crp_cluster_key_outputs_outcome temp 
INNER JOIN crp_program_outcomes pp on pp.crp_program_id=temp.crp_program_id
and pp.description =temp.description
;
