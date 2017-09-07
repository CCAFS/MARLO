UPDATE deliverables d
INNER JOIN projects p ON p.id = d.project_id
AND p.crp_id != 1
AND p.is_active
SET d.new_expected_year = NULL,
 d.`status` = 2
WHERE
  d.is_active
AND d.`status` = 4;