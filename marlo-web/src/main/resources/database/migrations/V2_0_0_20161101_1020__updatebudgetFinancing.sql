UPDATE funding_source_budgets  fb
set budget = 

(select sum(amount) from project_budgets pb where fb.funding_source_id=pb.funding_source_id and fb.year=pb.year and pb.is_active=1) ;