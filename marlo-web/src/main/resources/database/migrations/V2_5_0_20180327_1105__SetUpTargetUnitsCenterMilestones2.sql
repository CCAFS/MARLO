UPDATE center_milestones,
center_target_units as ct,
srf_target_units as sf
set  center_milestones.srf_target_unit = sf.id
WHERE
center_milestones.target_unit_id = ct.id AND
sf.`name`  = ct.`name`;