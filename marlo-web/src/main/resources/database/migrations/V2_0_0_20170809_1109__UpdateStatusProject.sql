UPDATE
projects p
INNER JOIN project_phases ph ON ph.project_id = p.id
set p.`status`=2 
where ph.id_phase=1 ;


UPDATE
projects p
INNER JOIN project_phases ph ON ph.project_id = p.id
set p.`status`=3 
where ph.id_phase=3 and (select  COUNT('x') from project_phases ph2 where ph2.project_id=p.id and ph2.id_phase=1 )=0  and end_date <'2017-01-01';


UPDATE
projects p
INNER JOIN project_phases ph ON ph.project_id = p.id
set p.`status`=5
where ph.id_phase=3 and (select  COUNT('x') from project_phases ph2 where ph2.project_id=p.id and ph2.id_phase=1 )=0 and end_date >'2017-01-01';

