delete from role_permissions   where permission_id 
in(
select p1.id
from permissions p1, permissions p2 where p1.permission=p2.permission and p1.id<p2.id);


delete 
p1
from permissions p1, permissions p2 where p1.permission=p2.permission and p1.id<p2.id

