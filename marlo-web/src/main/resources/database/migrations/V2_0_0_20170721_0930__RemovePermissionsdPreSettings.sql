delete rp
from  role_permissions rp 
INNER JOIN permissions pe on rp.permission_id=pe.id
where rp.role_id in (29,69)
and (permission like '%projectSwitch%')