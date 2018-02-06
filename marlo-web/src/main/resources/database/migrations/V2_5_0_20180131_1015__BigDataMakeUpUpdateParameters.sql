UPDATE custom_parameters set `value`= '148' where parameter_id = (
SELECT id from parameters where `key` = 'crp_pmu_rol' and global_unit_type_id=3);

UPDATE custom_parameters set `value`= '146' where parameter_id = (
SELECT id from parameters where `key` = 'crp_fpl_rol' and global_unit_type_id=3);

UPDATE custom_parameters set `value`= '145' where parameter_id = (
SELECT id from parameters where `key` = 'crp_rpl_rol' and global_unit_type_id=3);

UPDATE custom_parameters set `value`= '152' where parameter_id = (
SELECT id from parameters where `key` = 'crp_cl_rol' and global_unit_type_id=3);

UPDATE custom_parameters set `value`= '149' where parameter_id = (
SELECT id from parameters where `key` = 'crp_sl_rol' and global_unit_type_id=3);

UPDATE custom_parameters set `value`= '141' where parameter_id = (
SELECT id from parameters where `key` = 'crp_pl_rol' and global_unit_type_id=3);

UPDATE custom_parameters set `value`= '143' where parameter_id = (
SELECT id from parameters where `key` = 'crp_pc_rol' and global_unit_type_id=3);

UPDATE custom_parameters set `value`= '138' where parameter_id = (
SELECT id from parameters where `key` = 'crp_admin_rol' and global_unit_type_id=3);

UPDATE custom_parameters set `value`= '146' where parameter_id = (
SELECT id from parameters where `key` = 'crp_fpm_rol' and global_unit_type_id=3);

UPDATE custom_parameters set `value`= '145' where parameter_id = (
SELECT id from parameters where `key` = 'crp_rpm_rol' and global_unit_type_id=3);

UPDATE custom_parameters set `value`= '140' where parameter_id = (
SELECT id from parameters where `key` = 'crp_cp_role' and global_unit_type_id=3);

UPDATE custom_parameters set `value`= 'bigdata' where parameter_id = (
SELECT id from parameters where `key` = 'crp_custom_file' and global_unit_type_id=3);