

create temporary table tmpTable (id int);

insert  tmpTable
        (id)
select  id
from    project_budgets yt
where   yt.is_active=1 and exists
        (
        select  *
        from    project_budgets  yt2
        where   yt2.project_id = yt.project_id
                and yt2.`year`= yt.`year`
                and yt2.institution_id= yt.institution_id
                and yt2.is_active= yt.is_active
and yt2.funding_source_id=yt.funding_source_id
                and yt2.is_active=1
                and yt2.id > yt.id
        );

delete  
from    project_budgets
where   ID in (select id from tmpTable);