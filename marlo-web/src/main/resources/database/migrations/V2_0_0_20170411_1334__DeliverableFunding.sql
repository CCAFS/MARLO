DELETE gl
FROM deliverable_gender_levels gl
INNER JOIN deliverables d on d.id=gl.deliverable_id
INNER JOIN projects  p on p.id=d.project_id
WHERE  p.crp_id=5 and gl.gender_level in (1,2,3,4,5);


