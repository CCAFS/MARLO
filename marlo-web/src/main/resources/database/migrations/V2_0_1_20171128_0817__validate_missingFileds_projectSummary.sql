update section_statuses ss 
inner join projects p on ss.project_id=p.id
set ss.missing_fields=REPLACE(REPLACE(missing_fields,'Project Summary;',''),'Project Summary','')
where ss.project_id is not null  and p.is_project_leader_edit=0
and ss.section_name='description'
and missing_fields like '%Project Summary%'