INSERT INTO expected_study_projects (project_id, expected_id, is_active, active_since, created_by, modified_by, modification_justification)
SELECT
case_study_projects.project_id,
project_expected_studies.id,
1,
now(),
3,
3,
''
FROM
project_expected_studies
INNER JOIN cases_studies ON project_expected_studies.old_case_study_id = cases_studies.id
INNER JOIN case_study_projects ON case_study_projects.case_study_id = cases_studies.id
WHERE
case_study_projects.creator = 0 AND
cases_studies.is_active = 1;

Delete from expected_study_projects where is_active=0;