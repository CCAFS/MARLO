CREATE TEMPORARY TABLE IF NOT EXISTS table2 AS (SELECT * FROM project_budgets) ;
UPDATE project_partner_persons  set project_partner_id=148 where project_partner_id=771;
UPDATE project_partner_contributions  set project_partner_id=148 where project_partner_id=771;
UPDATE project_partner_overall  set project_partner_id=148 where project_partner_id=771;
UPDATE project_partners set is_active=0 where id=771;
UPDATE project_budgets  pb set pb.amount=pb.amount+(select sum(pb2.amount) from table2 pb2 where pb.`year`=pb2.year and pb2.project_id=101 and pb2.institution_id=1200  ) 
where pb.project_id=101 and  pb.institution_id=46 and pb.is_active=1;

UPDATE project_budgets set is_active=0  where project_id=101 and  institution_id=1200;
UPDATE project_budgets set is_active=0  where project_id=101 and  id=1279;


UPDATE project_partner_persons  set project_partner_id=70 where project_partner_id=774;
UPDATE project_partner_contributions  set project_partner_id=70 where project_partner_id=774;
UPDATE project_partner_overall  set project_partner_id=70 where project_partner_id=774;
UPDATE project_partners set is_active=0 where id=774;
UPDATE project_budgets  pb set pb.amount=pb.amount+(select sum(pb2.amount) from table2 pb2 where pb.`year`=pb2.year and pb2.project_id=121 and pb2.institution_id=1201  ) 
where pb.project_id=121 and  pb.institution_id=46 and pb.is_active=1;
UPDATE project_budgets set is_active=0  where project_id=101 and  institution_id=1201;



UPDATE project_partner_persons  set project_partner_id=658 where project_partner_id=791;
UPDATE project_partner_contributions  set project_partner_id=658 where project_partner_id=791;
UPDATE project_partner_overall  set project_partner_id=658 where project_partner_id=791;
UPDATE project_partners set is_active=0 where id=791;
UPDATE project_budgets  pb set pb.amount=pb.amount+(select sum(pb2.amount) from table2 pb2 where pb.`year`=pb2.year and pb2.project_id=111 and pb2.institution_id=1202  ) 
where pb.project_id=111 and  pb.institution_id=46 and pb.is_active=1;
UPDATE project_budgets set is_active=0  where project_id=111 and  institution_id=1202;

UPDATE project_partner_persons  set project_partner_id=671 where project_partner_id=940;
UPDATE project_partner_contributions  set project_partner_id=671 where project_partner_id=940;
UPDATE project_partner_overall  set project_partner_id=671 where project_partner_id=940;
UPDATE project_partners set is_active=0 where id=940;
UPDATE project_budgets  pb set pb.amount=pb.amount+(select sum(pb2.amount) from table2 pb2 where pb.`year`=pb2.year and pb2.project_id=131 and pb2.institution_id=1202  ) 
where pb.project_id=131 and  pb.institution_id=46 and pb.is_active=1;
UPDATE project_budgets set is_active=0  where project_id=131 and  institution_id=1202;


UPDATE project_partner_persons  set project_partner_id=225 where project_partner_id=128;
UPDATE project_partner_contributions  set project_partner_id=225 where project_partner_id=128;
UPDATE project_partner_overall  set project_partner_id=225 where project_partner_id=128;
UPDATE project_partners set is_active=0 where id=128;
UPDATE project_budgets  pb set pb.amount=pb.amount+(select sum(pb2.amount) from table2 pb2 where pb.`year`=pb2.year and pb2.project_id=62 and pb2.institution_id=1203  ) 
where pb.project_id=62 and  pb.institution_id=46 and pb.is_active=1;
UPDATE project_budgets set is_active=0  where project_id=62 and  institution_id=1203;

UPDATE project_partner_persons  set project_partner_id=47 where project_partner_id=759;
UPDATE project_partner_contributions  set project_partner_id=47 where project_partner_id=759;
UPDATE project_partner_overall  set project_partner_id=47 where project_partner_id=759;
UPDATE project_partners set is_active=0 where id=759;
UPDATE project_budgets  pb set pb.amount=pb.amount+(select sum(pb2.amount) from table2 pb2 where pb.`year`=pb2.year and pb2.project_id=63 and pb2.institution_id=1203  ) 
where pb.project_id=63 and  pb.institution_id=46 and pb.is_active=1;
UPDATE project_budgets set is_active=0  where project_id=63 and  institution_id=1203;


UPDATE project_partner_persons  set project_partner_id=304 where project_partner_id=760;
UPDATE project_partner_contributions  set project_partner_id=304 where project_partner_id=760;
UPDATE project_partner_overall  set project_partner_id=304 where project_partner_id=760;
UPDATE project_partners set is_active=0 where id=760;
UPDATE project_budgets  pb set pb.amount=pb.amount+(select sum(pb2.amount) from table2 pb2 where pb.`year`=pb2.year and pb2.project_id=68 and pb2.institution_id=1203  ) 
where pb.project_id=68 and  pb.institution_id=89 and pb.is_active=1;
UPDATE project_budgets set is_active=0  where project_id=68 and  institution_id=1203;


UPDATE project_partner_persons  set project_partner_id=134 where project_partner_id=761;
UPDATE project_partner_contributions  set project_partner_id=134 where project_partner_id=761;
UPDATE project_partner_overall  set project_partner_id=134 where project_partner_id=761;
UPDATE project_partners set is_active=0 where id=761;
UPDATE project_budgets  pb set pb.amount=pb.amount+(select sum(pb2.amount) from table2 pb2 where pb.`year`=pb2.year and pb2.project_id=69 and pb2.institution_id=1203  ) 
where pb.project_id=69 and  pb.institution_id=89 and pb.is_active=1;
UPDATE project_budgets set is_active=0  where project_id=69 and  institution_id=1203;


UPDATE project_partner_persons  set project_partner_id=54 where project_partner_id=770;
UPDATE project_partner_contributions  set project_partner_id=54 where project_partner_id=770;
UPDATE project_partner_overall  set project_partner_id=54 where project_partner_id=770;
UPDATE project_partners set is_active=0 where id=770;
UPDATE project_budgets  pb set pb.amount=pb.amount+(select sum(pb2.amount) from table2 pb2 where pb.`year`=pb2.year and pb2.project_id=100 and pb2.institution_id=1203  ) 
where pb.project_id=100 and  pb.institution_id=89 and pb.is_active=1;
UPDATE project_budgets set is_active=0  where project_id=100 and  institution_id=1203;


UPDATE project_partner_persons  set project_partner_id=61 where project_partner_id=779;
UPDATE project_partner_contributions  set project_partner_id=61 where project_partner_id=779;
UPDATE project_partner_overall  set project_partner_id=61 where project_partner_id=779;
UPDATE project_partners set is_active=0 where id=779;
UPDATE project_budgets  pb set pb.amount=pb.amount+(select sum(pb2.amount) from table2 pb2 where pb.`year`=pb2.year and pb2.project_id=117 and pb2.institution_id=1203  ) 
where pb.project_id=117 and  pb.institution_id=89 and pb.is_active=1;
UPDATE project_budgets set is_active=0  where project_id=117 and  institution_id=1203;



UPDATE project_partner_persons  set project_partner_id=677 where project_partner_id=782;
UPDATE project_partner_contributions  set project_partner_id=677 where project_partner_id=782;
UPDATE project_partner_overall  set project_partner_id= 677 where project_partner_id=782;
UPDATE project_partners set is_active=0 where id=782;
UPDATE project_budgets  pb set pb.amount=pb.amount+(select sum(pb2.amount) from table2 pb2 where pb.`year`=pb2.year and pb2.project_id=126 and pb2.institution_id=1203  ) 
where pb.project_id=126 and  pb.institution_id=89 and pb.is_active=1;
UPDATE project_budgets set is_active=0  where project_id=126 and  institution_id=1203;

UPDATE project_partner_persons  set project_partner_id=67 where project_partner_id=762;
UPDATE project_partner_contributions  set project_partner_id=67 where project_partner_id=762;
UPDATE project_partner_overall  set project_partner_id= 67 where project_partner_id=762;
UPDATE project_partners set is_active=0 where id=762;
UPDATE project_budgets  pb set pb.amount=pb.amount+(select sum(pb2.amount) from table2 pb2 where pb.`year`=pb2.year and pb2.project_id=112 and pb2.institution_id=1205  ) 
where pb.project_id=112 and  pb.institution_id=46 and pb.is_active=1;
UPDATE project_budgets set is_active=0  where project_id=112 and  institution_id=1205;

UPDATE project_partner_persons  set project_partner_id=77 where project_partner_id=784;
UPDATE project_partner_contributions  set project_partner_id=77 where project_partner_id=784;
UPDATE project_partner_overall  set project_partner_id= 77 where project_partner_id=784;
UPDATE project_partners set is_active=0 where id=784;
UPDATE project_budgets  pb set pb.amount=pb.amount+(select sum(pb2.amount) from table2 pb2 where pb.`year`=pb2.year and pb2.project_id=118 and pb2.institution_id=1205  ) 
where pb.project_id=118 and  pb.institution_id=46 and pb.is_active=1;
UPDATE project_budgets set is_active=0  where project_id=118 and  institution_id=1205;

