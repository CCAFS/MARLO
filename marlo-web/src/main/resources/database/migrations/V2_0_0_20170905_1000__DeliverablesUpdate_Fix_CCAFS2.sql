UPDATE
  deliverables d
INNER JOIN projects p ON p.id = d.project_id
AND p.is_active
SET d.new_expected_year = NULL
WHERE
  d.`status` = 2
AND d.new_expected_year IS NOT NULL;