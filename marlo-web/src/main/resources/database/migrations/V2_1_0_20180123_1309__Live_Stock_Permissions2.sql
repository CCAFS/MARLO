delete rp
 from roles r
inner JOIN role_permissions rp on r.id=rp.role_id
inner join  permissions pe on pe.id=rp.permission_id


where r.crp_id in (7) and r.acronym IN ('FPM') and pe.permission like '%addCoreProject%';

delete rp
 from roles r
inner JOIN role_permissions rp on r.id=rp.role_id
inner join  permissions pe on pe.id=rp.permission_id


where r.crp_id in (7) and r.acronym IN ('FPM') and pe.permission like '%addBilateralProject%';


