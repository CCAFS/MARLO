INSERT INTO project_policy_sub_idos (project_policy_id, id_phase, sub_ido_id)
SELECT
ppi.project_policy_id,
pesi.id_phase,
pesi.sub_ido_id
FROM
project_policy_info AS ppi
INNER JOIN project_expected_studies AS pes ON ppi.project_expected_study_id = pes.id
INNER JOIN project_expected_study_sub_ido AS pesi ON pesi.expected_id = pes.id
WHERE
ppi.id_phase = pesi.id_phase
