UPDATE custom_parameters AS cu
INNER JOIN parameters p ON p.id = cu.parameter_id
AND p.`key` = "crp_deliverable_intellectual_asset"
AND p.global_unit_type_id = 1
SET `value`='false'
WHERE  cu.global_unit_id = 3;