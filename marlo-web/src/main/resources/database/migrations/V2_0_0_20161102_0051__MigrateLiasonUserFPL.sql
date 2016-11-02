insert into 
crp_program_leaders (crp_program_id,user_id,is_active,created_by,active_since,modified_by,modification_justification)
 select distinct li.crp_program,lu.user_id,1,3,now(),3,'' from liaison_users lu 
INNER JOIN liaison_institutions li on lu.institution_id=li.id
where li.crp_program is not null and lu.is_active=1 and li.is_active=1
and lu.user_id<>1058
and CONCAT(lu.user_id,'-',li.crp_program) not in (

SELECT  CONCAT(cp.user_id,'-',cp.crp_program_id)  from crp_program_leaders cp where cp.is_active=1 )