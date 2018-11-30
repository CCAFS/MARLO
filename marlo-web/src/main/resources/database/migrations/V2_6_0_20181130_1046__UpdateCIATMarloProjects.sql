
Delete from global_unit_projects where global_unit_id = 29 and origin = 0;

insert into global_unit_projects (project_id,global_unit_id,origin,is_active,active_since,created_by,modified_by,modification_justification)
SELECT distinct
  pr.id  ,29,0,1,NOW(),3,3,''
FROM
projects pr
join project_partners pp on pp.project_id = pr.id
join phases ph on ph.id = pp.id_phase
join global_units gu on gu.id = ph.global_unit_id
join project_focuses pf on pf.project_id = pr.id
join crp_programs prg on prg.id = pf.program_id
where pp.institution_id=46 # Solo CIAT
and ph.year =2019
and ph.name = 'AR'
and gu.id != 29;