UPDATE custom_parameters set `value`= '170' where parameter_id = (
SELECT id from parameters where `key` = 'crp_pmu_rol' and global_unit_type_id=4);

UPDATE custom_parameters set `value`= '168' where parameter_id = (
SELECT id from parameters where `key` = 'crp_fpl_rol' and global_unit_type_id=4);

UPDATE custom_parameters set `value`= '167' where parameter_id = (
SELECT id from parameters where `key` = 'crp_rpl_rol' and global_unit_type_id=4);

UPDATE custom_parameters set `value`= '174' where parameter_id = (
SELECT id from parameters where `key` = 'crp_cl_rol' and global_unit_type_id=4);

UPDATE custom_parameters set `value`= '171' where parameter_id = (
SELECT id from parameters where `key` = 'crp_sl_rol' and global_unit_type_id=4);

UPDATE custom_parameters set `value`= '163' where parameter_id = (
SELECT id from parameters where `key` = 'crp_pl_rol' and global_unit_type_id=4);

UPDATE custom_parameters set `value`= '165' where parameter_id = (
SELECT id from parameters where `key` = 'crp_pc_rol' and global_unit_type_id=4);

UPDATE custom_parameters set `value`= '160' where parameter_id = (
SELECT id from parameters where `key` = 'crp_admin_rol' and global_unit_type_id=4);

UPDATE custom_parameters set `value`= '175' where parameter_id = (
SELECT id from parameters where `key` = 'crp_fpm_rol' and global_unit_type_id=4);

UPDATE custom_parameters set `value`= '176' where parameter_id = (
SELECT id from parameters where `key` = 'crp_rpm_rol' and global_unit_type_id=4);

UPDATE custom_parameters set `value`= '162' where parameter_id = (
SELECT id from parameters where `key` = 'crp_cp_role' and global_unit_type_id=4);

UPDATE custom_parameters set `value`= 'ciat' where parameter_id = (
SELECT id from parameters where `key` = 'crp_custom_file' and global_unit_type_id=4);