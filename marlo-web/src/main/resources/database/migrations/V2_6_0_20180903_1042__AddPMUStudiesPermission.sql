SET FOREIGN_KEY_CHECKS=0;

INSERT INTO `permissions` (`permission`, `description`, `type`) VALUES ('crp:{0}:studies:*', 'Full privilegies in studies without Projects', '0');
INSERT INTO `role_permissions` (`role_id`, `permission_id`) VALUES ((SELECT id FROM roles r WHERE r.acronym="PMU" AND r.global_unit_id=17), (SELECT id FROM permissions p WHERE p.permission="crp:{0}:studies:*"));
