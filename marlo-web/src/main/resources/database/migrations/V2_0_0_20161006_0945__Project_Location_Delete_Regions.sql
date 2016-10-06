Delete  pld.* from project_locations as pld
INNER JOIN loc_elements ON pld.loc_element_id = loc_elements.id
WHERE
loc_elements.element_type_id = 1;