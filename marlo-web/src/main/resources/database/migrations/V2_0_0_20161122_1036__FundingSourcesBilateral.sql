UPDATE

 project_budgets  pb INNER JOIN funding_sources fs on fs.id=pb.project_id
set pb.is_active=0
where fs.type=3
;


update 

funding_sources  fs INNER JOIN funding_source_budgets fb on fb.funding_source_id=fs.id  
set fb.budget =(select sum(pb.amount) from project_budgets  pb where pb.year=fb.year and pb.funding_source_id=fs.id and pb.is_active=1 )
 where fs.type=3;