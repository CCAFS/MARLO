
insert into global_unit_projects (project_id,global_unit_id,origin,is_active,active_since,created_by,modified_by,modification_justification)
SELECT distinct
  p.id  ,29,0,1,NOW(),3,3,''
FROM
  projects p
INNER JOIN project_partners pp ON pp.project_id = p.id
INNER JOIN project_partner_persons ppp ON ppp.project_partner_id = pp.id
WHERE
  p.is_active = 1
AND pp.is_active = 1
AND ppp.is_active = 1
and pp.institution_id=46
AND ppp.contact_type = 'PL';