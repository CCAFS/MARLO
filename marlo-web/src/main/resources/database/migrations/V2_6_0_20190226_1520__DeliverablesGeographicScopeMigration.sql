INSERT INTO deliverable_geographic_scopes(id_phase, deliverable_id, rep_ind_geographic_scope_id)
SELECT
di.id_phase,
di.deliverable_id,
di.geographic_scope_id
FROM
deliverables_info AS di
WHERE
di.geographic_scope_id IS NOT NULL;