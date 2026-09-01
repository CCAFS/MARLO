-- CHG-COGNITO-AUTH-001-T02 -- specificity flag for CGIAR authentication via Amazon Cognito.
-- Rolls out per Global Unit; default is 'false' everywhere, so deploying this migration changes
-- nobody's login. Enabling a unit is an operational act in custom_parameters, not a deploy artifact,
-- which is why no custom_parameters rows are seeded here (design.md section 3).
-- Global Unit types 1 (CRP), 3 (Platform) and 4 (Center) only, per the AGENTS.md specificity template.
-- Types 2 and 5 are deliberately absent and therefore can never carry this flag -- see OQ-11.

INSERT INTO parameters (global_unit_type_id, `key`, `description`, `format`, default_value, category)
VALUES ( '1', 'cognito_auth_active', 'Authenticate CGIAR users through Amazon Cognito instead of LDAP', '1', 'false', '2');

INSERT INTO parameters (global_unit_type_id, `key`, `description`, `format`, default_value, category)
VALUES ( '3', 'cognito_auth_active', 'Authenticate CGIAR users through Amazon Cognito instead of LDAP', '1', 'false', '2');

INSERT INTO parameters (global_unit_type_id, `key`, `description`, `format`, default_value, category)
VALUES ( '4', 'cognito_auth_active', 'Authenticate CGIAR users through Amazon Cognito instead of LDAP', '1', 'false', '2');
