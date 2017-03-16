
INSERT INTO `roles` (`id`, `description`, `acronym`, `crp_id`) VALUES ('75', 'Regional Manager', 'RPM', '1');
INSERT INTO `roles` (`id`, `description`, `acronym`, `crp_id`) VALUES ('76', 'Regional Manager', 'RPM', '3');
INSERT INTO `roles` (`id`, `description`, `acronym`, `crp_id`) VALUES ('77', 'Regional Manager', 'RPM', '4');
INSERT INTO `roles` (`id`, `description`, `acronym`, `crp_id`) VALUES ('78', 'Regional Manager', 'RPM', '5');
INSERT INTO `roles` (`id`, `description`, `acronym`, `crp_id`) VALUES ('79', 'Regional Manager', 'RPM', '7');
INSERT INTO `roles` (`id`, `description`, `acronym`, `crp_id`) VALUES ('80', 'Regional Manager', 'RPM', '21');
INSERT INTO `roles` (`id`, `description`, `acronym`, `crp_id`) VALUES ('81', 'Regional Manager', 'RPM', '22');

INSERT INTO `crp_parameters` (`crp_id`, `key`, `value`, `is_active`, `created_by`, `modified_by`, `modification_justification`) VALUES 
('1', 'crp_rpm_rol', '75', '1', '3', '3', '');
INSERT INTO `crp_parameters` (`crp_id`, `key`, `value`, `is_active`, `created_by`, `modified_by`, `modification_justification`) VALUES 
('3', 'crp_rpm_rol', '76', '1', '3', '3', '');
INSERT INTO `crp_parameters` (`crp_id`, `key`, `value`, `is_active`, `created_by`, `modified_by`, `modification_justification`) VALUES 
('4', 'crp_rpm_rol', '77', '1', '3', '3', '');
INSERT INTO `crp_parameters` (`crp_id`, `key`, `value`, `is_active`, `created_by`, `modified_by`, `modification_justification`) VALUES 
('5', 'crp_rpm_rol', '78', '1', '3', '3', '');
INSERT INTO `crp_parameters` (`crp_id`, `key`, `value`, `is_active`, `created_by`, `modified_by`, `modification_justification`) VALUES 
('7', 'crp_rpm_rol', '79', '1', '3', '3', '');
INSERT INTO `crp_parameters` (`crp_id`, `key`, `value`, `is_active`, `created_by`, `modified_by`, `modification_justification`) VALUES 
('21', 'crp_rpm_rol', '80', '1', '3', '3', '');
INSERT INTO `crp_parameters` (`crp_id`, `key`, `value`, `is_active`, `created_by`, `modified_by`, `modification_justification`) VALUES 
('22', 'crp_rpm_rol', '81', '1', '3', '3', '');



insert into role_permissions (role_id,permission_id) 
select id,469 from  roles where acronym='RPL';

insert into role_permissions (role_id,permission_id) 
select id,470 from  roles where acronym='RPL';

insert into role_permissions (role_id,permission_id) 
select distinct 75,p.id from role_permissions rp INNER JOIN roles ro on ro.id=rp.role_id
INNER JOIN permissions p on rp.permission_id=p.id
where ro.id=11 AND p.id not in (423,469,461);

insert into role_permissions (role_id,permission_id) 
select distinct 76,p.id from role_permissions rp INNER JOIN roles ro on ro.id=rp.role_id
INNER JOIN permissions p on rp.permission_id=p.id
where ro.id=11 AND p.id not in (423,469,461);

insert into role_permissions (role_id,permission_id) 
select distinct 77,p.id from role_permissions rp INNER JOIN roles ro on ro.id=rp.role_id
INNER JOIN permissions p on rp.permission_id=p.id
where ro.id=11 AND p.id not in (423,469,461);

insert into role_permissions (role_id,permission_id) 
select distinct 78,p.id from role_permissions rp INNER JOIN roles ro on ro.id=rp.role_id
INNER JOIN permissions p on rp.permission_id=p.id
where ro.id=11 AND p.id not in (423,469,461);

insert into role_permissions (role_id,permission_id) 
select distinct 79,p.id from role_permissions rp INNER JOIN roles ro on ro.id=rp.role_id
INNER JOIN permissions p on rp.permission_id=p.id
where ro.id=11 AND p.id not in (423,469,461);

insert into role_permissions (role_id,permission_id) 
select distinct 80,p.id from role_permissions rp INNER JOIN roles ro on ro.id=rp.role_id
INNER JOIN permissions p on rp.permission_id=p.id
where ro.id=11 AND p.id not in (423,469,461);

insert into role_permissions (role_id,permission_id) 
select distinct 81,p.id from role_permissions rp INNER JOIN roles ro on ro.id=rp.role_id
INNER JOIN permissions p on rp.permission_id=p.id
where ro.id=11 AND p.id not in (423,469);