UPDATE deliverable_gender_levels gl
INNER JOIN deliverables d ON d.id = gl.deliverable_id
INNER JOIN projects p ON p.id = d.project_id
SET gl.gender_level = 7
WHERE
  p.crp_id = 5
AND gl.gender_level IN (1, 2, 3, 4, 5);

