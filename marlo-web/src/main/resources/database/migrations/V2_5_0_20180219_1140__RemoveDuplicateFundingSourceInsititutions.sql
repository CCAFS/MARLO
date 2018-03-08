create temporary table tmpTable (id int);

insert  tmpTable
        (id)
select  id
from    funding_source_institutions yt
where   exists
        (
        select  *
        from    funding_source_institutions  yt2
        where   yt2.funding_source_id = yt.funding_source_id
                and yt2.institution_id= yt.institution_id
                and yt2.id_phase= yt.id_phase
            
                and yt2.id > yt.id
        );


delete  
from    funding_source_institutions
where   ID in (select id from tmpTable);

