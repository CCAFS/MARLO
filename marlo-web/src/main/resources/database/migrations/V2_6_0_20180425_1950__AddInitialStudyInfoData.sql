INSERT into project_expected_study_info(project_expected_study_id, id_phase, study_type_id, `status`,title)
SELECT
project_expected_studies.id,
projects_info.id_phase,
project_expected_studies.type,
CASE 
WHEN project_expected_studies.composed_id IS NULL THEN 3
ELSE 2
END as st,
project_expected_studies.topic_study
FROM
project_expected_studies
INNER JOIN projects ON project_expected_studies.project_id = projects.id
INNER JOIN projects_info ON projects_info.project_id = projects.id
WHERE
projects_info.id_phase >= project_expected_studies.id_phase
AND project_expected_studies.is_active = 1