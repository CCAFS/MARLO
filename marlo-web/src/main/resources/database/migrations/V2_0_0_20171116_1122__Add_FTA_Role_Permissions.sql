/** Remove existing permissions for FTA. **/
DELETE pe FROM role_permissions pe INNER JOIN roles ro ON pe.role_id=ro.id WHERE ro.crp_id=11;

/** These permissions seem to be the same across CRPS. **/
INSERT INTO role_permissions(`role_id`, `permission_id`) SELECT (SELECT r.id from roles r WHERE r.crp_id = 11 AND r.acronym = 'SuperAdmin'), rp.permission_id FROM role_permissions rp INNER JOIN roles r2 ON r2.id = rp.role_id WHERE r2.crp_id = 1 AND r2.acronym = 'SuperAdmin';
INSERT INTO role_permissions(`role_id`, `permission_id`) SELECT (SELECT r.id from roles r WHERE r.crp_id = 11 AND r.acronym = 'E'), rp.permission_id FROM role_permissions rp INNER JOIN roles r2 ON r2.id = rp.role_id WHERE r2.crp_id = 1 AND r2.acronym = 'E';
INSERT INTO role_permissions(`role_id`, `permission_id`) SELECT (SELECT r.id from roles r WHERE r.crp_id = 11 AND r.acronym = 'CRP-Admin'), rp.permission_id FROM role_permissions rp INNER JOIN roles r2 ON r2.id = rp.role_id WHERE r2.crp_id = 1 AND r2.acronym = 'CRP-Admin';
INSERT INTO role_permissions(`role_id`, `permission_id`) SELECT (SELECT r.id from roles r WHERE r.crp_id = 11 AND r.acronym = 'FM'), rp.permission_id FROM role_permissions rp INNER JOIN roles r2 ON r2.id = rp.role_id WHERE r2.crp_id = 1 AND r2.acronym = 'FM';
INSERT INTO role_permissions(`role_id`, `permission_id`) SELECT (SELECT r.id from roles r WHERE r.crp_id = 11 AND r.acronym = 'DM'), rp.permission_id FROM role_permissions rp INNER JOIN roles r2 ON r2.id = rp.role_id WHERE r2.crp_id = 1 AND r2.acronym = 'DM';
INSERT INTO role_permissions(`role_id`, `permission_id`) SELECT (SELECT r.id from roles r WHERE r.crp_id = 11 AND r.acronym = 'CP'), rp.permission_id FROM role_permissions rp INNER JOIN roles r2 ON r2.id = rp.role_id WHERE r2.crp_id = 1 AND r2.acronym = 'CP';
INSERT INTO role_permissions(`role_id`, `permission_id`) SELECT (SELECT r.id from roles r WHERE r.crp_id = 11 AND r.acronym = 'G'), rp.permission_id FROM role_permissions rp INNER JOIN roles r2 ON r2.id = rp.role_id WHERE r2.crp_id = 1 AND r2.acronym = 'G';
INSERT INTO role_permissions(`role_id`, `permission_id`) SELECT (SELECT r.id from roles r WHERE r.crp_id = 11 AND r.acronym = 'ML'), rp.permission_id FROM role_permissions rp INNER JOIN roles r2 ON r2.id = rp.role_id WHERE r2.crp_id = 1 AND r2.acronym = 'ML';

/** These permissions are different per CRP. **/

/** PMU team - align with PIM permissions **/
INSERT INTO role_permissions(`role_id`, `permission_id`) SELECT (SELECT r.id from roles r WHERE r.crp_id = 11 AND r.acronym = 'PMU'), rp.permission_id FROM role_permissions rp INNER JOIN roles r2 ON r2.id = rp.role_id WHERE r2.crp_id = 3 AND r2.acronym = 'PMU';
/** Flagship leader - align with PIM permissions **/
INSERT INTO role_permissions(`role_id`, `permission_id`) SELECT (SELECT r.id from roles r WHERE r.crp_id = 11 AND r.acronym = 'FPL'), rp.permission_id FROM role_permissions rp INNER JOIN roles r2 ON r2.id = rp.role_id WHERE r2.crp_id = 3 AND r2.acronym = 'FPL';
/** Cluser Leader  -align with PIM permissions **/
INSERT INTO role_permissions(`role_id`, `permission_id`) SELECT (SELECT r.id from roles r WHERE r.crp_id = 11 AND r.acronym = 'CL'), rp.permission_id FROM role_permissions rp INNER JOIN roles r2 ON r2.id = rp.role_id WHERE r2.crp_id = 3 AND r2.acronym = 'CL';
/** Flagship manager - align with Livestock permissions **/
INSERT INTO role_permissions(`role_id`, `permission_id`) SELECT (SELECT r.id from roles r WHERE r.crp_id = 11 AND r.acronym = 'FPM'), rp.permission_id FROM role_permissions rp INNER JOIN roles r2 ON r2.id = rp.role_id WHERE r2.crp_id = 7 AND r2.acronym = 'FPM';
/** Project leader - same as CCAFS permissions **/
INSERT INTO role_permissions(`role_id`, `permission_id`) SELECT (SELECT r.id from roles r WHERE r.crp_id = 11 AND r.acronym = 'PL'), rp.permission_id FROM role_permissions rp INNER JOIN roles r2 ON r2.id = rp.role_id WHERE r2.crp_id = 1 AND r2.acronym = 'PL';
/** Project coordinator - same as CCAFS permissions **/
INSERT INTO role_permissions(`role_id`, `permission_id`) SELECT (SELECT r.id from roles r WHERE r.crp_id = 11 AND r.acronym = 'PC'), rp.permission_id FROM role_permissions rp INNER JOIN roles r2 ON r2.id = rp.role_id WHERE r2.crp_id = 1 AND r2.acronym = 'PC';

