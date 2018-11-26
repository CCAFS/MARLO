UPDATE custom_parameters set `value`= (SELECT r.id from roles r where r.global_unit_id = 26 and acronym = 'PMU') where parameter_id = (
SELECT id from parameters where `key` = 'crp_pmu_rol' and global_unit_type_id=3) and global_unit_id = 26;

UPDATE custom_parameters set `value`= (SELECT r.id from roles r where r.global_unit_id = 26 and acronym = 'FPL') where parameter_id = (
SELECT id from parameters where `key` = 'crp_fpl_rol' and global_unit_type_id=3) and global_unit_id = 26;

UPDATE custom_parameters set `value`= (SELECT r.id from roles r where r.global_unit_id = 26 and acronym = 'RPL') where parameter_id = (
SELECT id from parameters where `key` = 'crp_rpl_rol' and global_unit_type_id=3) and global_unit_id = 26;

UPDATE custom_parameters set `value`= (SELECT r.id from roles r where r.global_unit_id = 26 and acronym = 'CL') where parameter_id = (
SELECT id from parameters where `key` = 'crp_cl_rol' and global_unit_type_id=3) and global_unit_id = 26;

UPDATE custom_parameters set `value`= (SELECT r.id from roles r where r.global_unit_id = 26 and acronym = 'SL') where parameter_id = (
SELECT id from parameters where `key` = 'crp_sl_rol' and global_unit_type_id=3) and global_unit_id = 26;

UPDATE custom_parameters set `value`= (SELECT r.id from roles r where r.global_unit_id = 26 and acronym = 'PL') where parameter_id = (
SELECT id from parameters where `key` = 'crp_pl_rol' and global_unit_type_id=3) and global_unit_id = 26;

UPDATE custom_parameters set `value`= (SELECT r.id from roles r where r.global_unit_id = 26 and acronym = 'PC') where parameter_id = (
SELECT id from parameters where `key` = 'crp_pc_rol' and global_unit_type_id=3) and global_unit_id = 26;

UPDATE custom_parameters set `value`= (SELECT r.id from roles r where r.global_unit_id = 26 and acronym = 'CRP-Admin') where parameter_id = (
SELECT id from parameters where `key` = 'crp_admin_rol' and global_unit_type_id=3) and global_unit_id = 26;

UPDATE custom_parameters set `value`= (SELECT r.id from roles r where r.global_unit_id = 26 and acronym = 'FPM') where parameter_id = (
SELECT id from parameters where `key` = 'crp_fpm_rol' and global_unit_type_id=3) and global_unit_id = 26;

UPDATE custom_parameters set `value`= (SELECT r.id from roles r where r.global_unit_id = 26 and acronym = 'RPM') where parameter_id = (
SELECT id from parameters where `key` = 'crp_rpm_rol' and global_unit_type_id=3) and global_unit_id = 26;

UPDATE custom_parameters set `value`= (SELECT r.id from roles r where r.global_unit_id = 26 and acronym = 'CP') where parameter_id = (
SELECT id from parameters where `key` = 'crp_cp_role' and global_unit_type_id=3) and global_unit_id = 26;

UPDATE custom_parameters set `value`= 'false' where parameter_id = (
SELECT id from parameters where `key` = 'crp_reporting_active' and global_unit_type_id=3) and global_unit_id = 26;

UPDATE custom_parameters set `value`= 'true' where parameter_id = (
SELECT id from parameters where `key` = 'crp_planning_active' and global_unit_type_id=3) and global_unit_id = 26;

UPDATE custom_parameters set `value`= '2018' where parameter_id = (
SELECT id from parameters where `key` = 'crp_reporting_year' and global_unit_type_id=3) and global_unit_id = 26;

UPDATE custom_parameters set `value`= '2018' where parameter_id = (
SELECT id from parameters where `key` = 'crp_planning_year' and global_unit_type_id=3) and global_unit_id = 26;

UPDATE custom_parameters set `value`= '5bfc0ce540105007f379a2f8' where parameter_id = (
SELECT id from parameters where `key` = 'crp_taw_api' and global_unit_type_id=3) and global_unit_id = 26;