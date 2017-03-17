update 
crp_program_leaders 
set manager=1
where user_id=16 and is_active=1;

update liaison_users 
set is_active=0
where user_id=16;

update 
crp_program_leaders 
set manager=1
where user_id=17 and is_active=1;


UPDATE projects set liaison_user_id=3 where liaison_user_id=4;


update liaison_users 
set is_active=0
where user_id=17;

update 
crp_program_leaders 
set manager=1
where user_id=50 and is_active=1;

update liaison_users 
set is_active=0
where user_id=50;

update 
crp_program_leaders 
set manager=1
where user_id=32 and is_active=1;

update liaison_users 
set is_active=0
where user_id=32;


update 
crp_program_leaders 
set manager=1
where user_id=852 and is_active=1;

update liaison_users 
set is_active=0
where user_id=852;

update 
crp_program_leaders 
set manager=1
where user_id=1083 and is_active=1;

update liaison_users 
set is_active=0
where user_id=1083;

UPDATE projects set liaison_user_id=5 where liaison_user_id=59;
