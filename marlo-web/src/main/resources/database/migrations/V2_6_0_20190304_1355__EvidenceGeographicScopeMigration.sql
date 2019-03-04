INSERT INTO project_expected_study_geographic_scopes(id_phase, expected_id, rep_ind_geographic_scope_id)
SELECT
ei.id_phase,
ei.project_expected_study_id,
ei.rep_ind_geographic_scope_id
FROM
project_expected_study_info AS ei
WHERE
ei.rep_ind_geographic_scope_id IS NOT NULL;

INSERT INTO project_expected_study_regions (expected_id, id_region, id_phase)
SELECT
es.expected_id,
es.loc_element_id,
es.id_phase
FROM
project_expected_study_countries AS es
INNER JOIN loc_elements AS le ON es.loc_element_id = le.id
WHERE
le.element_type_id = 1;

Delete from project_expected_study_countries WHERE id in (
SELECT * FROM
(
SELECT
es.id
FROM
project_expected_study_countries AS es
INNER JOIN loc_elements AS le ON es.loc_element_id = le.id
WHERE
le.element_type_id = 1)
as p
);