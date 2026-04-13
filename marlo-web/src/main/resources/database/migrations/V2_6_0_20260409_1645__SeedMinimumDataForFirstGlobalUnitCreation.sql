-- Minimum bootstrap data for creating the first Global Unit when catalogs are empty.
-- Safe and idempotent: only inserts rows that do not already exist.
-- Note: roles cannot be seeded here because roles.global_unit_id is NOT NULL and requires an existing Global Unit.

INSERT INTO users (first_name, last_name, username, email, password, is_cgiar_user, is_active, auto_save,
  agree_terms, created_by, active_since, modified_by, modification_justification, last_login)
SELECT 'Super', 'Admin', 'superadmin', 'superadmin@marlo.local', '202cb962ac59075b964b07152d234b70', 0, 1, 0,
  1, NULL, CURRENT_TIMESTAMP, NULL, 'Seed minimum superadmin user', CURRENT_TIMESTAMP
WHERE NOT EXISTS (
  SELECT 1 FROM users
  WHERE LOWER(username) = 'superadmin' OR LOWER(email) = 'superadmin@marlo.local'
);

INSERT INTO user_roles (user_id, role_id)
SELECT super_admin_user.id, role_value.id
FROM users super_admin_user
INNER JOIN roles role_value ON LOWER(role_value.acronym) = 'superadmin'
WHERE (LOWER(super_admin_user.username) = 'superadmin' OR LOWER(super_admin_user.email) = 'superadmin@marlo.local')
AND super_admin_user.is_active = 1
AND NOT EXISTS (
  SELECT 1 FROM user_roles user_role_value
  WHERE user_role_value.user_id = super_admin_user.id
  AND user_role_value.role_id = role_value.id
);

INSERT INTO global_unit_types (id, `name`, is_active, active_since, created_by, modified_by, modification_justification)
SELECT 1, 'CRP', 1, CURRENT_TIMESTAMP, NULL, NULL, 'Seed minimum global unit type'
WHERE NOT EXISTS (SELECT 1 FROM global_unit_types WHERE id = 1 OR LOWER(`name`) = 'crp');

INSERT INTO global_unit_types (id, `name`, is_active, active_since, created_by, modified_by, modification_justification)
SELECT 3, 'Platform', 1, CURRENT_TIMESTAMP, NULL, NULL, 'Seed minimum global unit type'
WHERE NOT EXISTS (SELECT 1 FROM global_unit_types WHERE id = 3 OR LOWER(`name`) = 'platform');

INSERT INTO global_unit_types (id, `name`, is_active, active_since, created_by, modified_by, modification_justification)
SELECT 4, 'Center', 1, CURRENT_TIMESTAMP, NULL, NULL, 'Seed minimum global unit type'
WHERE NOT EXISTS (SELECT 1 FROM global_unit_types WHERE id = 4 OR LOWER(`name`) = 'center');

INSERT INTO permissions (permission, description, type)
SELECT '*', 'Super Admin Full Access', 0
WHERE NOT EXISTS (SELECT 1 FROM permissions WHERE permission = '*');

INSERT INTO permissions (permission, description, type)
SELECT 'superadmin:canEdit', 'Super Admin can edit', 0
WHERE NOT EXISTS (SELECT 1 FROM permissions WHERE permission = 'superadmin:canEdit');

INSERT INTO parameters (global_unit_type_id, `key`, `description`, `format`, default_value, category)
SELECT global_unit_type.id, 'current_phase', 'Current phase identifier', 3, NULL, 3
FROM global_unit_types global_unit_type
WHERE global_unit_type.id IN (1, 3, 4)
AND NOT EXISTS (
  SELECT 1 FROM parameters parameter_value
  WHERE parameter_value.global_unit_type_id = global_unit_type.id
  AND parameter_value.`key` = 'current_phase'
);

INSERT INTO parameters (global_unit_type_id, `key`, `description`, `format`, default_value, category)
SELECT global_unit_type.id, 'crp_custom_file', 'Custom configuration file', 4, '', 3
FROM global_unit_types global_unit_type
WHERE global_unit_type.id IN (1, 3, 4)
AND NOT EXISTS (
  SELECT 1 FROM parameters parameter_value
  WHERE parameter_value.global_unit_type_id = global_unit_type.id
  AND parameter_value.`key` = 'crp_custom_file'
);
