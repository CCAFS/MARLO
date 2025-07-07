UPDATE feedback_roles_permissions SET requires_project_association = false;

UPDATE feedback_roles_permissions SET requires_project_association = true WHERE role_id IN (SELECT id FROM roles WHERE acronym IN ('PL', 'PC'));
