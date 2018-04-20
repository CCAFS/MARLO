UPDATE center_outcomes,
center_target_units as ct,
srf_target_units as sf
set  center_outcomes.srf_target_unit_id = sf.id
WHERE
center_outcomes.target_unit_id = ct.id AND
sf.`name`  = ct.`name`;