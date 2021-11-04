ALTER TABLE global_units ADD financial_code varchar(20) AFTER acronym;

Update global_units set financial_code='C' where id=29;
Update global_units set financial_code='A' where id=30;
Update global_units set financial_code='B' where id=31;
Update global_units set financial_code='D' where id=33;
Update global_units set financial_code='N' where id=35;
Update global_units set financial_code='T' where id=36;
Update global_units set financial_code='L' where id=37;
Update global_units set financial_code='M' where id=38;
Update global_units set financial_code='P' where id=39;
Update global_units set financial_code='R' where id=40;
Update global_units set financial_code='W' where id=41;
Update global_units set financial_code='F' where id=43;
Update global_units set financial_code='Q' where id=46;