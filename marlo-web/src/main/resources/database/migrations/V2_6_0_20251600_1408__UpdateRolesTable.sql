insert into roles(description,acronym,`order`,global_unit_id) 
select description,acronym,`order`,47 
from roles  
where global_unit_id=25; 