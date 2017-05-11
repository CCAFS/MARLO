delete from user_roles where role_id=12 and user_id=16;
delete from user_roles where role_id=12 and user_id=17;
delete from user_roles where role_id=12 and user_id=50;
delete from user_roles where role_id=12 and user_id=32;
delete from user_roles where role_id=12 and user_id=852;
delete from user_roles where role_id=12 and user_id=1083;

insert into user_roles (role_id,user_id)values (68,16);
insert into user_roles (role_id,user_id)values (68,17);
insert into user_roles (role_id,user_id)values (68,50);
insert into user_roles (role_id,user_id)values (68,32);
insert into user_roles (role_id,user_id)values (68,852);
insert into user_roles (role_id,user_id)values (68,1083);


update 
crp_program_leaders 
set manager=1
where user_id=18 and is_active=1;

update liaison_users 
set is_active=0
where user_id=18;
delete from user_roles where role_id=11 and user_id=18;
insert into user_roles (role_id,user_id)values (75,18);



---
update 
crp_program_leaders 
set manager=1
where user_id=179 and is_active=1;

update liaison_users 
set is_active=0
where user_id=179;
delete from user_roles where role_id=11 and user_id=179;
insert into user_roles (role_id,user_id)values (75,179);

---

update 
crp_program_leaders 
set manager=1
where user_id=25 and is_active=1;

update liaison_users 
set is_active=0
where user_id=25;
delete from user_roles where role_id=11 and user_id=25;
insert into user_roles (role_id,user_id)values (75,25);
UPDATE projects set liaison_user_id=61 where liaison_user_id=12;
---
update 
crp_program_leaders 
set manager=1
where user_id=855 and is_active=1;

update liaison_users 
set is_active=0
where user_id=855;
delete from user_roles where role_id=11 and user_id=855;
insert into user_roles (role_id,user_id)values (75,855);

---
update 
crp_program_leaders 
set manager=1
where user_id=855 and is_active=1;

update liaison_users 
set is_active=0
where user_id=855;
delete from user_roles where role_id=11 and user_id=855;
insert into user_roles (role_id,user_id)values (75,855);

---

update 
crp_program_leaders 
set manager=1
where user_id=186 and is_active=1;

update liaison_users 
set is_active=0
where user_id=186;
delete from user_roles where role_id=11 and user_id=186;
insert into user_roles (role_id,user_id)values (75,186);
--

update 
crp_program_leaders 
set manager=1
where user_id=26 and is_active=1;

update liaison_users 
set is_active=0
where user_id=26;
delete from user_roles where role_id=11 and user_id=26;
insert into user_roles (role_id,user_id)values (75,26);


--

update 
crp_program_leaders 
set manager=1
where user_id=27 and is_active=1;

update liaison_users 
set is_active=0
where user_id=27;
delete from user_roles where role_id=11 and user_id=27;
insert into user_roles (role_id,user_id)values (75,27);

--

update 
crp_program_leaders 
set manager=1
where user_id=21 and is_active=1;

update liaison_users 
set is_active=0
where user_id=21;
delete from user_roles where role_id=11 and user_id=21;
insert into user_roles (role_id,user_id)values (75,21);


