insert into global_unit_projects(project_id, global_unit_id, is_active, active_since, created_by, modified_by, modification_justification)
select id, crp_id, is_active, now(),3,3,'' from projects;