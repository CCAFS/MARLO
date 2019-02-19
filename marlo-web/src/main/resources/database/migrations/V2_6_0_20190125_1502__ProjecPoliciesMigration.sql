INSERT INTO project_policies (project_id, is_active, active_since, created_by, modified_by, modification_justification)
SELECT ev.project_id, 1, NOW(), 1057,1057, pol.title
FROM
project_expected_studies AS ev
INNER JOIN report_synthesis_policies AS pol ON pol.expected_study_id = ev.id
ORDER BY
ev.project_id ASC