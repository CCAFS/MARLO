

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
) 
SELECT distinct 
  pb.project_id,
  0,
  pb.budget_type,
  pa.`year`,
  pb.funding_source_id,
  pb.institution_id,
  pb.gender_percentage,
  pb.is_active,
  pb.active_since,
  pb.created_by,
  pb.modified_by,
  pb.modification_justification,
pa.id
FROM
  project_budgets pb
INNER JOIN projects p ON p.id = pb.project_id
inner JOIN project_phases ph on p.id=ph.project_id
INNER JOIN phases pa on pa.id=ph.id_phase
inner join funding_sources_info fs on fs.funding_source_id=pb.funding_source_id and fs.id_phase=pa.id
where pb.is_active=1 
and (
select COUNT('x')
from project_budgets pb2 
where pb2.project_id=pb.project_id
and pb2.`year`=pa.`year`
and pb2.is_active=1
and pb2.institution_id=pb.institution_id
and pb2.funding_source_id=pb.funding_source_id
and pb2.`year`>=2017

)=0

and pb.`year`>=2017
and pa.`year`>2017
and  YEAR(fs.end_date)<=pa.`year`
 
;



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
) 


SELECT distinct 
  pb.project_id,
  0,
  pb.budget_type,
  pa.`year`,
  pb.funding_source_id,
  pb.institution_id,
  pb.gender_percentage,
  pb.is_active,
  pb.active_since,
  pb.created_by,
  pb.modified_by,
  pb.modification_justification,
pa.id
FROM
  project_budgets pb
INNER JOIN projects p ON p.id = pb.project_id
inner JOIN project_phases ph on p.id=ph.project_id
INNER JOIN phases pa on pa.id=ph.id_phase
inner join funding_sources_info fs on fs.funding_source_id=pb.funding_source_id and fs.id_phase=pa.id
where pb.is_active=1 
and (
select COUNT('x')
from project_budgets pb2 
where pb2.project_id=pb.project_id
and pb2.`year`=pa.`year`
and pb2.is_active=1
and pb2.institution_id=pb.institution_id
and pb2.funding_source_id=pb.funding_source_id
and pb2.`year`>=2017

)=0
and pb.`year`>=2017
and pa.`year`>=2017
and  pa.`year`<=YEAR(fs.end_date)
 
;

