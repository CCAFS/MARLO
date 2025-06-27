-- PMU (Program Management Unit) - Full access
INSERT INTO feedback_roles_permissions (role_id, feedback_permission_id, cluster_type_id, global_unit_id, description)
VALUES (427, 1, NULL, 45, 'PMU - can_write_comments on all clusters');
INSERT INTO feedback_roles_permissions (role_id, feedback_permission_id, cluster_type_id, global_unit_id, description)
VALUES (427, 2, NULL, 45, 'PMU - can_approve_comments on all clusters');
INSERT INTO feedback_roles_permissions (role_id, feedback_permission_id, cluster_type_id, global_unit_id, description)
VALUES (427, 4, NULL, 45, 'PMU - can_track_comments on all clusters');

-- FPL (Flagship Leader)
INSERT INTO feedback_roles_permissions (role_id, feedback_permission_id, cluster_type_id, global_unit_id, description)
VALUES (425, 1, 2, 45, 'FPL - can_write_comments on regional clusters');
INSERT INTO feedback_roles_permissions (role_id, feedback_permission_id, cluster_type_id, global_unit_id, description)
VALUES (425, 1, 3, 45, 'FPL - can_write_comments on country clusters');
INSERT INTO feedback_roles_permissions (role_id, feedback_permission_id, cluster_type_id, global_unit_id, description)
VALUES (425, 3, 1, 45, 'FPL - can_react_comments on thematic clusters');
INSERT INTO feedback_roles_permissions (role_id, feedback_permission_id, cluster_type_id, global_unit_id, description)
VALUES (425, 4, NULL, 45, 'FPL - can_track_comments on all clusters');

-- FPM (Flagship Manager)
INSERT INTO feedback_roles_permissions (role_id, feedback_permission_id, cluster_type_id, global_unit_id, description)
VALUES (432, 1, 2, 45, 'FPM - can_write_comments on regional clusters');
INSERT INTO feedback_roles_permissions (role_id, feedback_permission_id, cluster_type_id, global_unit_id, description)
VALUES (432, 1, 3, 45, 'FPM - can_write_comments on country clusters');
INSERT INTO feedback_roles_permissions (role_id, feedback_permission_id, cluster_type_id, global_unit_id, description)
VALUES (432, 3, 1, 45, 'FPM - can_react_comments on thematic clusters');
INSERT INTO feedback_roles_permissions (role_id, feedback_permission_id, cluster_type_id, global_unit_id, description)
VALUES (432, 4, NULL, 45, 'FPM - can_track_comments on all clusters');

-- RPL (Regional Program Leader)
INSERT INTO feedback_roles_permissions (role_id, feedback_permission_id, cluster_type_id, global_unit_id, description)
VALUES (424, 1, 3, 45, 'RPL - can_write_comments on country clusters');
INSERT INTO feedback_roles_permissions (role_id, feedback_permission_id, cluster_type_id, global_unit_id, description)
VALUES (424, 2, 3, 45, 'RPL - can_approve_comments on country clusters');
INSERT INTO feedback_roles_permissions (role_id, feedback_permission_id, cluster_type_id, global_unit_id, description)
VALUES (424, 3, 2, 45, 'RPL - can_react_comments on regional clusters');
INSERT INTO feedback_roles_permissions (role_id, feedback_permission_id, cluster_type_id, global_unit_id, description)
VALUES (424, 4, NULL, 45, 'RPL - can_track_comments on all clusters');

-- RPM (Regional Program Manager)
INSERT INTO feedback_roles_permissions (role_id, feedback_permission_id, cluster_type_id, global_unit_id, description)
VALUES (433, 1, 3, 45, 'RPM - can_write_comments on country clusters');
INSERT INTO feedback_roles_permissions (role_id, feedback_permission_id, cluster_type_id, global_unit_id, description)
VALUES (433, 2, 3, 45, 'RPM - can_approve_comments on country clusters');
INSERT INTO feedback_roles_permissions (role_id, feedback_permission_id, cluster_type_id, global_unit_id, description)
VALUES (433, 3, 2, 45, 'RPM - can_react_comments on regional clusters');
INSERT INTO feedback_roles_permissions (role_id, feedback_permission_id, cluster_type_id, global_unit_id, description)
VALUES (433, 4, NULL, 45, 'RPM - can_track_comments on all clusters');

-- PL (Project Leader)
INSERT INTO feedback_roles_permissions (role_id, feedback_permission_id, cluster_type_id, global_unit_id, description)
VALUES (420, 3, NULL, 45, 'PL - can_react_comments on own cluster');

-- PC (Project Coordinator)
INSERT INTO feedback_roles_permissions (role_id, feedback_permission_id, cluster_type_id, global_unit_id, description)
VALUES (422, 3, NULL, 45, 'PC - can_react_comments on own cluster');

-- SuperAdmin
INSERT INTO feedback_roles_permissions (role_id, feedback_permission_id, cluster_type_id, global_unit_id, description)
VALUES (430, 1, NULL, 45, 'SuperAdmin - can_write_comments on all clusters');
INSERT INTO feedback_roles_permissions (role_id, feedback_permission_id, cluster_type_id, global_unit_id, description)
VALUES (430, 2, NULL, 45, 'SuperAdmin - can_approve_comments on all clusters');
INSERT INTO feedback_roles_permissions (role_id, feedback_permission_id, cluster_type_id, global_unit_id, description)
VALUES (430, 3, NULL, 45, 'SuperAdmin - can_react_comments on all clusters');
INSERT INTO feedback_roles_permissions (role_id, feedback_permission_id, cluster_type_id, global_unit_id, description)
VALUES (430, 4, NULL, 45, 'SuperAdmin - can_track_comments on all clusters');
