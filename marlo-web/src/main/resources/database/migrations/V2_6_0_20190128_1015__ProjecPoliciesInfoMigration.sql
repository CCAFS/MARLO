INSERT INTO project_policy_info (project_policy_id, id_phase, `year`, title, project_expected_study_id, rep_ind_geographic_scope_id)
SELECT
pol.id,
psi.id_phase,
2017,
pol.modification_justification,
psi.project_expected_study_id,
psi.rep_ind_geographic_scope_id
FROM
projects AS p
INNER JOIN project_policies AS pol ON pol.project_id = p.id
INNER JOIN project_expected_studies AS ps ON ps.project_id = p.id
INNER JOIN project_expected_study_info AS psi ON psi.project_expected_study_id = ps.id
INNER JOIN report_synthesis_policies AS syn ON syn.expected_study_id = ps.id
WHERE
syn.title = pol.modification_justification