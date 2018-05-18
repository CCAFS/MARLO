INSERT INTO expected_study_projects (project_id, expected_id, id_phase)
SELECT
expected_study_projects.project_id,
expected_study_projects.expected_id,
project_expected_study_info.id_phase
FROM
expected_study_projects
INNER JOIN project_expected_studies ON expected_study_projects.expected_id = project_expected_studies.id
INNER JOIN project_expected_study_info ON project_expected_study_info.project_expected_study_id = project_expected_studies.id;

Delete from expected_study_projects where id_phase IS NULL;