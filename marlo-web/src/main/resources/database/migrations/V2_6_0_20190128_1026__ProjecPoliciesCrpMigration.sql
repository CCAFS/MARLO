INSERT INTO project_policy_crps (project_policy_id, id_phase, global_unit_id)
SELECT
ppi.project_policy_id,
pesc.id_phase,
pesc.global_unit_id
FROM
project_policy_info AS ppi
INNER JOIN project_expected_studies AS pes ON ppi.project_expected_study_id = pes.id
INNER JOIN project_expected_study_crp AS pesc ON pesc.expected_id = pes.id
WHERE
ppi.id_phase = pesc.id_phase