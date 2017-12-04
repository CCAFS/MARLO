insert into custom_parameters (parameter_id, global_unit_id, `value`, created_by, is_active, active_since, modified_by, modification_justification)
SELECT 
p.id,
23,
cp.`value`,
cp.created_by,
cp.is_active,
cp.active_since,
cp.modified_by,
cp.modification_justification
FROM
center_custom_parameters AS cp 
INNER JOIN center_parameters AS cpp ON cp.parameter_id = cpp.id
INNER JOIN parameters AS p ON cpp.`key` = p.`key`
WHERE
p.global_unit_type_id = 2 