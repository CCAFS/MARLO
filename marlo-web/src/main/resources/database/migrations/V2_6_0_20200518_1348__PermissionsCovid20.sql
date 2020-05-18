INSERT INTO `permissions` (`permission`, `description`, `type`) 
VALUES ('crp:{0}:project:{1}:impacts', 'Base Permission to COVID-19 section in the reporting round.', '1');

INSERT INTO `permissions` (`permission`, `description`, `type`) 
VALUES ('crp:{0}:project:{1}:impacts:canEdit', 'Can make changes in the COVID-19 section section in the reporting round.', '1');

INSERT INTO `role_permissions` (`role_id`, `permission_id`) 
SELECT role_id, (SELECT id FROM `permissions` WHERE permission = 'crp:{0}:project:{1}:impacts') 
FROM role_permissions
WHERE permission_id IN (SELECT id FROM `permissions` WHERE permission = 'crp:{0}:project:{1}:contributionsLP6');

INSERT INTO `role_permissions` (`role_id`, `permission_id`) 
SELECT role_id, (SELECT id FROM `permissions` WHERE permission = 'crp:{0}:project:{1}:impacts:canEdit') 
FROM role_permissions
WHERE permission_id IN (SELECT id FROM `permissions` WHERE permission = 'crp:{0}:project:{1}:contributionsLP6:canEdit');