INSERT INTO `parameters` (global_unit_type_id,`key`,description,format,default_value,category) VALUES ('2', 'center_crp_phase_year', 'The year that extract the crp information.', '1', null, '3');
INSERT INTO `parameters` (global_unit_type_id,`key`,description,format,default_value,category) VALUES ('2', 'center_year', 'the current center year reporting phase', '1', null, '3');
INSERT INTO `parameters` (global_unit_type_id,`key`,description,format,default_value,category) VALUES ('2', 'center_crp_phase_cycle', 'The cycle that extract the crp information.', '1', null, '3');

insert into custom_parameters (parameter_id, global_unit_id, `value`, created_by, is_active, active_since, modified_by, modification_justification) 
(select id,23,'2017',3,1,now(),3,'' from parameters where `key` = 'center_crp_phase_year' );

insert into custom_parameters (parameter_id, global_unit_id, `value`, created_by, is_active, active_since, modified_by, modification_justification) 
(select id,23,'2017',3,1,now(),3,'' from parameters where `key` = 'center_year' );

insert into custom_parameters (parameter_id, global_unit_id, `value`, created_by, is_active, active_since, modified_by, modification_justification) 
(select id,23,'Planning',3,1,now(),3,'' from parameters where `key` = 'center_crp_phase_cycle' );