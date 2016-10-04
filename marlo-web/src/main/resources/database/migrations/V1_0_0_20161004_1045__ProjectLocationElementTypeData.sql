START TRANSACTION;
Insert into project_location_element_types (project_id, loc_element_type_id, is_global)
SELECT DISTINCT
project_locations.project_id,
loc_element_types.id,
0
FROM
loc_elements
INNER JOIN loc_element_types ON loc_elements.element_type_id = loc_element_types.id
INNER JOIN project_locations ON project_locations.loc_element_id = loc_elements.id
ORDER BY
project_locations.project_id ASC;
COMMIT;