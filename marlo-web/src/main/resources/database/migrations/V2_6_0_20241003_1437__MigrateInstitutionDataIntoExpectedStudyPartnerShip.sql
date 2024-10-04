INSERT INTO project_expected_study_partnerships (
    expected_id,
    institution_id,
    id_phase,
    expected_study_partner_type_id,
    is_active,
    active_since,
    created_by,
    modified_by,
    modification_justification
)
SELECT 
    expected_id,
    institution_id,
    id_phase,
    2 AS expected_study_partner_type_id,
    1 AS is_active,
    CURRENT_TIMESTAMP AS active_since,
    1 AS created_by,
    NULL AS modified_by,
    NULL AS modification_justification
FROM 
    project_expected_study_institutions;