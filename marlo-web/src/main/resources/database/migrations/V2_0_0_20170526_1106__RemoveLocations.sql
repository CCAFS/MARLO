update  projects pro INNER JOIN project_locations 
pl on pl.project_id=pro.id
set pl.is_active=0
where pro.crp_id in (3,5)