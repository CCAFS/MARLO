UPDATE feedback_roles_permissions frp
JOIN roles r ON r.id = frp.role_id
JOIN feedback_permissions fp ON fp.id = frp.feedback_permission_id
JOIN cluster_types ct ON ct.name = 'Theme'
SET frp.cluster_type_id = ct.id,
    frp.description   = 'FPM - can_react_comments on thematic clusters'
WHERE r.acronym = 'FPM'
  AND fp.name = 'can_react_comments';