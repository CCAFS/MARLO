INSERT INTO crp_users(user_id, is_active, created_by, active_since,modified_by, modification_justification, global_unit_id)
SELECT
crp_users.user_id,
crp_users.is_active,
crp_users.created_by,
crp_users.active_since,
crp_users.modified_by,
crp_users.modification_justification,
29
FROM
crp_users
WHERE
crp_users.global_unit_id = 23;