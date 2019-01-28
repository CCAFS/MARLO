INSERT INTO project_policy_countries (project_policy_id, id_phase, id_country)
SELECT
ppi.project_policy_id,
pesc.id_phase,
pesc.loc_element_id
FROM
project_policy_info AS ppi
INNER JOIN project_expected_studies AS pes ON ppi.project_expected_study_id = pes.id
INNER JOIN project_expected_study_countries AS pesc ON pesc.expected_id = pes.id
WHERE
ppi.id_phase = pesc.id_phase
