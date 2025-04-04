INSERT INTO crp_loc_element_types(loc_element_type_id, global_unit_id) 
SELECT loc_element_type_id, 47 AS global_unit_id 
  FROM crp_loc_element_types 
WHERE global_unit_id IN (1); 