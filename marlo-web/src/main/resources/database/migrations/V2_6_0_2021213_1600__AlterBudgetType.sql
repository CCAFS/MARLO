Alter table budget_types add financial_code varchar(10);
Alter table budget_types add parent varchar(50);
Alter table budget_types add isMarlo int(1);
Alter table budget_types add isOneCGIAR int(1);

insert into budget_types (id,name,financial_code,parent,isMarlo,isOneCGIAR) values (5,'Portfolio','F1-01','All Funding Source',0,1);
insert into budget_types (id,name,financial_code,parent,isMarlo,isOneCGIAR) values (6,'Non Portfolio','F1-02','All Funding Source',0,1);
update budget_types set financial_code='F2-10',parent='Portfolio', isMarlo=1 ,isOneCGIAR=1 where id=1;
update budget_types set financial_code='F2-11',parent='Portfolio', isMarlo=1 ,isOneCGIAR=1 where id=2;
update budget_types set financial_code='F2-12',parent='Portfolio', isMarlo=1 ,isOneCGIAR=1 where id=3;
update budget_types set financial_code='',parent='', isMarlo=1 ,isOneCGIAR=0 where id=4;
insert into budget_types (id,name,financial_code,parent,isMarlo,isOneCGIAR) values (7,'Other revenues','F2-20','Non Portfolio',0,1);
insert into budget_types (id,name,financial_code,parent,isMarlo,isOneCGIAR) values (8,'Unrestricted','F2-21','Non Portfolio',0,1);