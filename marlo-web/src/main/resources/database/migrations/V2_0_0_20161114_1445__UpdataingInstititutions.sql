update project_budgets pb INNER JOIN institutions ins on ins.id=pb.institution_id 
set pb.institution_id=ins.headquarter
where headquarter is not null

and pb.is_active=1;