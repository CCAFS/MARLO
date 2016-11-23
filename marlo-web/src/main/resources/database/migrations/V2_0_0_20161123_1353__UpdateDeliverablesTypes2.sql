  SET FOREIGN_KEY_CHECKS=0;


  update 

  deliverables
  set type_id =
  case type_id
  
  when 13 then 51
  when 38 then 60
  else type_id
  end 


