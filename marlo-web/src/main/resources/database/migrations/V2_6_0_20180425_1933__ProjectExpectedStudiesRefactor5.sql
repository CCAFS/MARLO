-- Temp table with Planning 2018 Project Expected Studies Info
DROP TABLE IF EXISTS `project_expected_studies_temp`;
Create Table project_expected_studies_temp(
SELECT
project_expected_studies.id,
project_expected_studies.project_id,
project_expected_studies.id_phase,
project_expected_studies.topic_study,
project_expected_studies.scope,
project_expected_studies.type,
project_expected_studies.other_type,
project_expected_studies.sub_ido,
project_expected_studies.srf_target,
project_expected_studies.comments,
project_expected_studies.is_active,
project_expected_studies.active_since,
project_expected_studies.created_by,
project_expected_studies.modified_by,
project_expected_studies.modification_justification
FROM
project_expected_studies
INNER JOIN phases ON project_expected_studies.id_phase = phases.id
WHERE
project_expected_studies.is_active = 1 AND
phases.description = 'Planning' AND
phases.`year` = 2018
);

-- Delete Not Planning 2018 Phase Information
Delete from expected_study_projects where expected_id not in (select id from project_expected_studies_temp);

Delete from project_expected_studies where id_phase not in (select id_phase from project_expected_studies_temp);

-- Insert Reporting 2017 Case Studies into Project Expected Studies Table
Insert Into project_expected_studies (project_id, id_phase, topic_study, type, is_active, active_since, created_by, modified_by, modification_justification, `year`, old_case_study_id)
SELECT
csp.project_id,
11,
cs.title,
1,
1,
NOW(),
3,
3,
'',
cs.`year`,
cs.id
FROM
cases_studies AS cs
INNER JOIN case_study_projects AS csp ON csp.case_study_id = cs.id
WHERE
cs.is_active = 1 AND
csp.creator = 1

