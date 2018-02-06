Create TEMPORARY table tem_table_pp AS ( 
SELECT
custom_parameters.id
FROM
parameters
INNER JOIN custom_parameters ON custom_parameters.parameter_id = parameters.id
WHERE
custom_parameters.global_unit_id = 23 AND
parameters.global_unit_type_id = 1);



Delete from custom_parameters  
where id IN (SELECT
id
FROM
tem_table_pp);