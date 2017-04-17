UPDATE deliverable_gender_levels gl
INNER JOIN deliverables d ON d.id = gl.deliverable_id
INNER JOIN projects p ON p.id = d.project_id
SET gl.gender_level = 7
WHERE
  p.crp_id = 5
AND gl.gender_level IN (1, 2, 3, 4, 5);



DROP TEMPORARY TABLE if EXISTS tmpTable;
create temporary table tmpTable (id int);

insert  tmpTable
        (id)
select  id
from    deliverable_gender_levels yt
where   yt.is_active=1 and exists
        (
        select  *
        from    deliverable_gender_levels  yt2
        where   yt2.deliverable_id = yt.deliverable_id
                and yt2.gender_level=yt.gender_level
               
                and yt2.is_active= yt.is_active

                and yt2.is_active=1
                and yt2.id > yt.id
        );





UPDATE deliverable_gender_levels 
set is_active=0
where id in (select id from tmpTable)
