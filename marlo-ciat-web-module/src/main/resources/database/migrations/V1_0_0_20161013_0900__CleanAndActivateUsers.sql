UPDATE users set is_active=0
where (id != 1 and id != 1108 and id != 1057 and id != 3);

UPDATE users set is_cgiar_user =0, last_login = NULL;

UPDATE users set first_name = 'CIAT' , username = 'ciatadmin' where id = 3;