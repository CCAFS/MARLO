UPDATE feedback_roles_permissions frp
JOIN cluster_types ct ON (
  LOWER(frp.description) COLLATE utf8mb3_unicode_ci LIKE CONCAT('%', LOWER(ct.name) COLLATE utf8mb3_unicode_ci, '%')
)
SET frp.cluster_type_id = ct.id
WHERE frp.global_unit_id = 45
  AND ct.name IN ('Country', 'Regional', 'Theme', 'Management');