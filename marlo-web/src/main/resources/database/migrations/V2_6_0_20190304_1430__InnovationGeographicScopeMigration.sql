INSERT INTO project_innovation_geographic_scopes(id_phase, project_innovation_id, rep_ind_geographic_scope_id)
SELECT
ei.id_phase,
ei.project_innovation_id,
ei.geographic_scope_id
FROM
project_innovation_info AS ei
WHERE
ei.geographic_scope_id IS NOT NULL;

INSERT INTO project_innovation_regions (project_innovation_id, id_region, id_phase)
SELECT
es.project_innovation_id,
es.id_country,
es.id_phase
FROM
project_innovation_countries AS es
INNER JOIN loc_elements AS le ON es.id_country = le.id
WHERE
le.element_type_id = 1;

Delete from project_innovation_countries WHERE id in (
SELECT * FROM
(
SELECT
es.id
FROM
project_innovation_countries AS es
INNER JOIN loc_elements AS le ON es.id_country = le.id
WHERE
le.element_type_id = 1)
as p
);