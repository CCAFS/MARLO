alter table oc_account_types add acronym VARCHAR(20) NOT NULL;

update oc_account_types set acronym='A1' where id = 1;
update oc_account_types set acronym='A2' where id = 2;
update oc_account_types set acronym='A3' where id = 3;
update oc_account_types set acronym='A4' where id = 4;