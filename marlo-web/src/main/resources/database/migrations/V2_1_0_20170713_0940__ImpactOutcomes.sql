SET FOREIGN_KEY_CHECKS = 0;

ALTER TABLE `crp_program_outcomes`
ADD COLUMN `composed_id`  varchar(20)  NULL AFTER `modification_justification`;

ALTER TABLE `crp_milestones`
ADD COLUMN `composed_id`  varchar(20)  NULL AFTER `modification_justification`;

update  crp_program_outcomes set composed_id=CONCAT(id,'-',crp_program_id);

update  crp_milestones ml INNER JOIN crp_program_outcomes po 
on po.id=ml.crp_program_outcome_id
set ml.composed_id=CONCAT(po.composed_id,'-',ml.id) ;

CREATE TEMPORARY TABLE
IF NOT EXISTS tableoutcomes AS (SELECT * FROM crp_program_outcomes);



CREATE TEMPORARY TABLE
IF NOT EXISTS table_temp_crp_cluster_key_outputs_outcome AS (
SELECT
  pp.*,ppp.composed_id
FROM
  crp_cluster_key_outputs_outcome pp
INNER JOIN crp_program_outcomes ppp ON pp.outcome_id = ppp.id)
;

CREATE TEMPORARY TABLE
IF NOT EXISTS table_temp_crp_outcome_sub_idos AS (

SELECT
 pp.*,ppp.composed_id,ppp.crp_program_id
FROM
  crp_outcome_sub_idos pp
INNER JOIN crp_program_outcomes ppp ON pp.crp_program_outcome_id=ppp.id
)
;

CREATE TEMPORARY TABLE
IF NOT EXISTS table_temp_crp_milestones AS (

SELECT
 pp.*,ppp.composed_id 'composed_id_outcome',ppp.crp_program_id
FROM
  crp_milestones pp
INNER JOIN crp_program_outcomes ppp ON pp.crp_program_outcome_id=ppp.id
)
;


CREATE TEMPORARY TABLE
IF NOT EXISTS table_temp_crp_assumptions AS (

SELECT
 pp1.*,ppp.composed_id,ppp.crp_program_id,pp.srf_sub_ido_id
FROM
  crp_assumptions pp1
inner join crp_outcome_sub_idos pp on pp.id=pp1.crp_outcome_sub_idos_id
INNER JOIN crp_program_outcomes ppp ON pp.crp_program_outcome_id=ppp.id
);


CREATE TEMPORARY TABLE
IF NOT EXISTS table_temp_project_outcomes AS (

SELECT
 pp.*,ppp.composed_id
FROM
  project_outcomes pp
INNER JOIN crp_program_outcomes ppp ON pp.outcome_id=ppp.id
)
;

CREATE TEMPORARY TABLE
IF NOT EXISTS table_temp_project_milestones AS (

SELECT
 pp.*,ppp.outcome_id,ppp.project_id,outc.composed_id,mile.composed_id 'mile_composed_id'
FROM
  project_milestones pp
INNER JOIN project_outcomes ppp ON pp.project_outcome_id=ppp.id
INNER JOIN crp_program_outcomes outc ON ppp.outcome_id=outc.id
INNER JOIN crp_milestones mile ON pp.crp_milestone_id=mile.id

)
;



ALTER TABLE `project_nextusers`
ADD COLUMN `composed_id`  varchar(20)  NULL AFTER `modification_justification`;

update  project_nextusers ml INNER JOIN project_outcomes po 
on po.id=ml.project_outcome_id
set ml.composed_id=CONCAT(po.project_id,'-',po.outcome_id,'-',ml.id) ;


CREATE TEMPORARY TABLE
IF NOT EXISTS table_temp_project_nextusers AS (

SELECT
 pp.*,ppp.project_id,ppp.outcome_id,outc.composed_id 'composed_id_outcome' 
FROM
  project_nextusers pp
INNER JOIN project_outcomes ppp ON pp.project_outcome_id=ppp.id
INNER JOIN crp_program_outcomes outc ON ppp.outcome_id=outc.id

)
;

CREATE TEMPORARY TABLE
IF NOT EXISTS table_project_outcomes AS (SELECT * FROM project_outcomes);

ALTER TABLE `project_outcomes`   
ADD COLUMN `id_phase`  bigint(20) NULL AFTER `modification_justification`;    
ALTER TABLE `project_outcomes` ADD FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`);

TRUNCATE TABLE project_outcomes;
TRUNCATE TABLE crp_program_outcomes;
TRUNCATE TABLE crp_cluster_key_outputs_outcome ;
TRUNCATE TABLE crp_outcome_sub_idos ;
TRUNCATE TABLE crp_milestones ;
TRUNCATE TABLE crp_assumptions ;
TRUNCATE TABLE project_milestones ;
TRUNCATE TABLE project_nextusers ;




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
composed_id,
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
t2.composed_id,
  ph.id
FROM
  tableoutcomes t2
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
INNER JOIN crp_program_outcomes pp on pp.composed_id =temp.composed_id
;



insert into crp_outcome_sub_idos (
contribution,
srf_sub_ido_id,
crp_program_outcome_id,
is_active,
created_by,
active_since,
modified_by,
modification_justification

)
select distinct 
temp.contribution,
temp.srf_sub_ido_id,
pp.id,
temp.is_active,
temp.created_by,
temp.active_since,
temp.modified_by,
temp.modification_justification
from table_temp_crp_outcome_sub_idos temp 
INNER JOIN crp_program_outcomes pp on pp.crp_program_id=temp.crp_program_id
and pp.composed_id =temp.composed_id
;


insert into crp_milestones (title,
`year`,
`value`,
target_unit_id,
crp_program_outcome_id,
is_active,
created_by,
active_since,
modified_by,
modification_justification,
composed_id


)
select distinct 
temp.title,
temp.`year`,
temp.`value`,
temp.target_unit_id,
pp.id,
temp.is_active,
temp.created_by,
temp.active_since,
temp.modified_by,
temp.modification_justification,
temp.composed_id
from table_temp_crp_milestones temp 
INNER JOIN crp_program_outcomes pp on pp.crp_program_id=temp.crp_program_id
and pp.composed_id =temp.composed_id_outcome
;


insert into crp_assumptions (description,
crp_outcome_sub_idos_id,
is_active,
created_by,
active_since,
modified_by,
modification_justification



)
select distinct 
temp.description,
temp.crp_outcome_sub_idos_id,
temp.is_active,
temp.created_by,
temp.active_since,
temp.modified_by,
temp.modification_justification
from table_temp_crp_assumptions temp 
INNER JOIN crp_program_outcomes pp on pp.crp_program_id=temp.crp_program_id
and pp.composed_id =temp.composed_id
inner join  crp_outcome_sub_idos asp on asp.srf_sub_ido_id=temp.srf_sub_ido_id
;

insert into project_outcomes (
project_id,
outcome_id,
expected_value,
expected_unit,
achieved_value,
narrative_target,
narrative_achieved,
is_active,
active_since,
created_by,
modified_by,
modification_justification,
achieved_unit,
gender_dimenssion,
youth_component,
id_phase

)
select distinct 
temp.project_id,
pp.id,
temp.expected_value,
temp.expected_unit,
temp.achieved_value,
temp.narrative_target,
temp.narrative_achieved,
temp.is_active,
temp.active_since,
temp.created_by,
temp.modified_by,
temp.modification_justification,
temp.achieved_unit,
temp.gender_dimenssion,
temp.youth_component,
pp.id_phase


from table_temp_project_outcomes temp 
INNER JOIN crp_program_outcomes pp on 
 pp.composed_id =temp.composed_id
;


insert into project_milestones (
project_outcome_id,
crp_milestone_id,
is_active,
active_since,
created_by,
modified_by,
modification_justification,
expected_value,
expected_unit,
achieved_value,
narrative_target,
narrative_achieved,
year
)
select distinct 
pp.id,
mil.id,
temp.is_active,
temp.active_since,
temp.created_by,
temp.modified_by,
temp.modification_justification,
temp.expected_value,
temp.expected_unit,
temp.achieved_value,
temp.narrative_target,
temp.narrative_achieved,
temp.year

from table_temp_project_milestones temp 
INNER JOIN project_outcomes pp on pp.project_id=temp.project_id
INNER JOIN crp_program_outcomes ppp on ppp.composed_id =temp.composed_id and ppp.id=pp.outcome_id and ppp.id_phase=pp.id_phase
inner join crp_milestones mil on mil.composed_id=temp.mile_composed_id
;

insert into project_nextusers (
project_outcome_id,
next_user,
knowledge,
strategies,
is_active,
created_by,
active_since,
modified_by,
modification_justification,
composed_id



)
select distinct 
pp.id,
temp.next_user,
temp.knowledge,
temp.strategies,
temp.is_active,
temp.created_by,
temp.active_since,
temp.modified_by,
temp.modification_justification,
temp.composed_id
from table_temp_project_nextusers temp 
INNER JOIN project_outcomes pp on pp.project_id=temp.project_id
INNER JOIN crp_program_outcomes ppp on ppp.composed_id =temp.composed_id_outcome and ppp.id=pp.outcome_id and ppp.id_phase=pp.id_phase
;
