-- RTB

UPDATE custom_parameters set `value`= (SELECT r.id from roles r where r.global_unit_id = 17 and acronym = 'PMU') where parameter_id = (
SELECT id from parameters where `key` = 'crp_pmu_rol' and global_unit_type_id=1) and global_unit_id = 17;

UPDATE custom_parameters set `value`= (SELECT r.id from roles r where r.global_unit_id = 17 and acronym = 'FPL') where parameter_id = (
SELECT id from parameters where `key` = 'crp_fpl_rol' and global_unit_type_id=1) and global_unit_id = 17;

UPDATE custom_parameters set `value`= (SELECT r.id from roles r where r.global_unit_id = 17 and acronym = 'RPL') where parameter_id = (
SELECT id from parameters where `key` = 'crp_rpl_rol' and global_unit_type_id=1) and global_unit_id = 17;

UPDATE custom_parameters set `value`= (SELECT r.id from roles r where r.global_unit_id = 17 and acronym = 'CL') where parameter_id = (
SELECT id from parameters where `key` = 'crp_cl_rol' and global_unit_type_id=1) and global_unit_id = 17;

UPDATE custom_parameters set `value`= (SELECT r.id from roles r where r.global_unit_id = 17 and acronym = 'SL') where parameter_id = (
SELECT id from parameters where `key` = 'crp_sl_rol' and global_unit_type_id=1) and global_unit_id = 17;

UPDATE custom_parameters set `value`= (SELECT r.id from roles r where r.global_unit_id = 17 and acronym = 'PL') where parameter_id = (
SELECT id from parameters where `key` = 'crp_pl_rol' and global_unit_type_id=1) and global_unit_id = 17;

UPDATE custom_parameters set `value`= (SELECT r.id from roles r where r.global_unit_id = 17 and acronym = 'PC') where parameter_id = (
SELECT id from parameters where `key` = 'crp_pc_rol' and global_unit_type_id=1) and global_unit_id = 17;

UPDATE custom_parameters set `value`= (SELECT r.id from roles r where r.global_unit_id = 17 and acronym = 'CRP-Admin') where parameter_id = (
SELECT id from parameters where `key` = 'crp_admin_rol' and global_unit_type_id=1) and global_unit_id = 17;

UPDATE custom_parameters set `value`= (SELECT r.id from roles r where r.global_unit_id = 17 and acronym = 'FPM') where parameter_id = (
SELECT id from parameters where `key` = 'crp_fpm_rol' and global_unit_type_id=1) and global_unit_id = 17;

UPDATE custom_parameters set `value`= (SELECT r.id from roles r where r.global_unit_id = 17 and acronym = 'RPM') where parameter_id = (
SELECT id from parameters where `key` = 'crp_rpm_rol' and global_unit_type_id=1) and global_unit_id = 17;

UPDATE custom_parameters set `value`= (SELECT r.id from roles r where r.global_unit_id = 17 and acronym = 'CP') where parameter_id = (
SELECT id from parameters where `key` = 'crp_cp_role' and global_unit_type_id=1) and global_unit_id = 17;

UPDATE custom_parameters set `value`= 'rtb' where parameter_id = (
SELECT id from parameters where `key` = 'crp_custom_file' and global_unit_type_id=1) and global_unit_id = 17;

UPDATE custom_parameters set `value`= 'false' where parameter_id = (
SELECT id from parameters where `key` = 'crp_reporting_active' and global_unit_type_id=1) and global_unit_id = 17;

UPDATE custom_parameters set `value`= 'true' where parameter_id = (
SELECT id from parameters where `key` = 'crp_planning_active' and global_unit_type_id=1) and global_unit_id = 17;

UPDATE custom_parameters set `value`= '2018' where parameter_id = (
SELECT id from parameters where `key` = 'crp_reporting_year' and global_unit_type_id=1) and global_unit_id = 17;

UPDATE custom_parameters set `value`= '2018' where parameter_id = (
SELECT id from parameters where `key` = 'crp_planning_year' and global_unit_type_id=1) and global_unit_id = 17;

-- Fish

UPDATE custom_parameters set `value`= (SELECT r.id from roles r where r.global_unit_id = 27 and acronym = 'PMU') where parameter_id = (
SELECT id from parameters where `key` = 'crp_pmu_rol' and global_unit_type_id=1) and global_unit_id = 27;

UPDATE custom_parameters set `value`= (SELECT r.id from roles r where r.global_unit_id = 27 and acronym = 'FPL') where parameter_id = (
SELECT id from parameters where `key` = 'crp_fpl_rol' and global_unit_type_id=1) and global_unit_id = 27;

UPDATE custom_parameters set `value`= (SELECT r.id from roles r where r.global_unit_id = 27 and acronym = 'RPL') where parameter_id = (
SELECT id from parameters where `key` = 'crp_rpl_rol' and global_unit_type_id=1) and global_unit_id = 27;

UPDATE custom_parameters set `value`= (SELECT r.id from roles r where r.global_unit_id = 27 and acronym = 'CL') where parameter_id = (
SELECT id from parameters where `key` = 'crp_cl_rol' and global_unit_type_id=1) and global_unit_id = 27;

UPDATE custom_parameters set `value`= (SELECT r.id from roles r where r.global_unit_id = 27 and acronym = 'SL') where parameter_id = (
SELECT id from parameters where `key` = 'crp_sl_rol' and global_unit_type_id=1) and global_unit_id = 27;

UPDATE custom_parameters set `value`= (SELECT r.id from roles r where r.global_unit_id = 27 and acronym = 'PL') where parameter_id = (
SELECT id from parameters where `key` = 'crp_pl_rol' and global_unit_type_id=1) and global_unit_id = 27;

UPDATE custom_parameters set `value`= (SELECT r.id from roles r where r.global_unit_id = 27 and acronym = 'PC') where parameter_id = (
SELECT id from parameters where `key` = 'crp_pc_rol' and global_unit_type_id=1) and global_unit_id = 27;

UPDATE custom_parameters set `value`= (SELECT r.id from roles r where r.global_unit_id = 27 and acronym = 'CRP-Admin') where parameter_id = (
SELECT id from parameters where `key` = 'crp_admin_rol' and global_unit_type_id=1) and global_unit_id = 27;

UPDATE custom_parameters set `value`= (SELECT r.id from roles r where r.global_unit_id = 27 and acronym = 'FPM') where parameter_id = (
SELECT id from parameters where `key` = 'crp_fpm_rol' and global_unit_type_id=1) and global_unit_id = 27;

UPDATE custom_parameters set `value`= (SELECT r.id from roles r where r.global_unit_id = 27 and acronym = 'RPM') where parameter_id = (
SELECT id from parameters where `key` = 'crp_rpm_rol' and global_unit_type_id=1) and global_unit_id = 27;

UPDATE custom_parameters set `value`= (SELECT r.id from roles r where r.global_unit_id = 27 and acronym = 'CP') where parameter_id = (
SELECT id from parameters where `key` = 'crp_cp_role' and global_unit_type_id=1) and global_unit_id = 27;

UPDATE custom_parameters set `value`= 'fish' where parameter_id = (
SELECT id from parameters where `key` = 'crp_custom_file' and global_unit_type_id=1) and global_unit_id = 27;

UPDATE custom_parameters set `value`= 'false' where parameter_id = (
SELECT id from parameters where `key` = 'crp_reporting_active' and global_unit_type_id=1) and global_unit_id = 27;

UPDATE custom_parameters set `value`= 'true' where parameter_id = (
SELECT id from parameters where `key` = 'crp_planning_active' and global_unit_type_id=1) and global_unit_id = 27;

UPDATE custom_parameters set `value`= '2018' where parameter_id = (
SELECT id from parameters where `key` = 'crp_reporting_year' and global_unit_type_id=1) and global_unit_id = 27;

UPDATE custom_parameters set `value`= '2018' where parameter_id = (
SELECT id from parameters where `key` = 'crp_planning_year' and global_unit_type_id=1) and global_unit_id = 27;

-- GLDC

UPDATE custom_parameters set `value`= (SELECT r.id from roles r where r.global_unit_id = 28 and acronym = 'PMU') where parameter_id = (
SELECT id from parameters where `key` = 'crp_pmu_rol' and global_unit_type_id=1) and global_unit_id = 28;

UPDATE custom_parameters set `value`= (SELECT r.id from roles r where r.global_unit_id = 28 and acronym = 'FPL') where parameter_id = (
SELECT id from parameters where `key` = 'crp_fpl_rol' and global_unit_type_id=1) and global_unit_id = 28;

UPDATE custom_parameters set `value`= (SELECT r.id from roles r where r.global_unit_id = 28 and acronym = 'RPL') where parameter_id = (
SELECT id from parameters where `key` = 'crp_rpl_rol' and global_unit_type_id=1) and global_unit_id = 28;

UPDATE custom_parameters set `value`= (SELECT r.id from roles r where r.global_unit_id = 28 and acronym = 'CL') where parameter_id = (
SELECT id from parameters where `key` = 'crp_cl_rol' and global_unit_type_id=1) and global_unit_id = 28;

UPDATE custom_parameters set `value`= (SELECT r.id from roles r where r.global_unit_id = 28 and acronym = 'SL') where parameter_id = (
SELECT id from parameters where `key` = 'crp_sl_rol' and global_unit_type_id=1) and global_unit_id = 28;

UPDATE custom_parameters set `value`= (SELECT r.id from roles r where r.global_unit_id = 28 and acronym = 'PL') where parameter_id = (
SELECT id from parameters where `key` = 'crp_pl_rol' and global_unit_type_id=1) and global_unit_id = 28;

UPDATE custom_parameters set `value`= (SELECT r.id from roles r where r.global_unit_id = 28 and acronym = 'PC') where parameter_id = (
SELECT id from parameters where `key` = 'crp_pc_rol' and global_unit_type_id=1) and global_unit_id = 28;

UPDATE custom_parameters set `value`= (SELECT r.id from roles r where r.global_unit_id = 28 and acronym = 'CRP-Admin') where parameter_id = (
SELECT id from parameters where `key` = 'crp_admin_rol' and global_unit_type_id=1) and global_unit_id = 28;

UPDATE custom_parameters set `value`= (SELECT r.id from roles r where r.global_unit_id = 28 and acronym = 'FPM') where parameter_id = (
SELECT id from parameters where `key` = 'crp_fpm_rol' and global_unit_type_id=1) and global_unit_id = 28;

UPDATE custom_parameters set `value`= (SELECT r.id from roles r where r.global_unit_id = 28 and acronym = 'RPM') where parameter_id = (
SELECT id from parameters where `key` = 'crp_rpm_rol' and global_unit_type_id=1) and global_unit_id = 28;

UPDATE custom_parameters set `value`= (SELECT r.id from roles r where r.global_unit_id = 28 and acronym = 'CP') where parameter_id = (
SELECT id from parameters where `key` = 'crp_cp_role' and global_unit_type_id=1) and global_unit_id = 28;

UPDATE custom_parameters set `value`= 'gldc' where parameter_id = (
SELECT id from parameters where `key` = 'crp_custom_file' and global_unit_type_id=1) and global_unit_id = 28;

UPDATE custom_parameters set `value`= 'false' where parameter_id = (
SELECT id from parameters where `key` = 'crp_reporting_active' and global_unit_type_id=1) and global_unit_id = 28;

UPDATE custom_parameters set `value`= 'true' where parameter_id = (
SELECT id from parameters where `key` = 'crp_planning_active' and global_unit_type_id=1) and global_unit_id = 28;

UPDATE custom_parameters set `value`= '2018' where parameter_id = (
SELECT id from parameters where `key` = 'crp_reporting_year' and global_unit_type_id=1) and global_unit_id = 28;

UPDATE custom_parameters set `value`= '2018' where parameter_id = (
SELECT id from parameters where `key` = 'crp_planning_year' and global_unit_type_id=1) and global_unit_id = 28;

-- EiB

UPDATE custom_parameters set `value`= (SELECT r.id from roles r where r.global_unit_id = 25 and acronym = 'PMU') where parameter_id = (
SELECT id from parameters where `key` = 'crp_pmu_rol' and global_unit_type_id=3) and global_unit_id = 25;

UPDATE custom_parameters set `value`= (SELECT r.id from roles r where r.global_unit_id = 25 and acronym = 'FPL') where parameter_id = (
SELECT id from parameters where `key` = 'crp_fpl_rol' and global_unit_type_id=3) and global_unit_id = 25;

UPDATE custom_parameters set `value`= (SELECT r.id from roles r where r.global_unit_id = 25 and acronym = 'RPL') where parameter_id = (
SELECT id from parameters where `key` = 'crp_rpl_rol' and global_unit_type_id=3) and global_unit_id = 25;

UPDATE custom_parameters set `value`= (SELECT r.id from roles r where r.global_unit_id = 25 and acronym = 'CL') where parameter_id = (
SELECT id from parameters where `key` = 'crp_cl_rol' and global_unit_type_id=3) and global_unit_id = 25;

UPDATE custom_parameters set `value`= (SELECT r.id from roles r where r.global_unit_id = 25 and acronym = 'SL') where parameter_id = (
SELECT id from parameters where `key` = 'crp_sl_rol' and global_unit_type_id=3) and global_unit_id = 25;

UPDATE custom_parameters set `value`= (SELECT r.id from roles r where r.global_unit_id = 25 and acronym = 'PL') where parameter_id = (
SELECT id from parameters where `key` = 'crp_pl_rol' and global_unit_type_id=3) and global_unit_id = 25;

UPDATE custom_parameters set `value`= (SELECT r.id from roles r where r.global_unit_id = 25 and acronym = 'PC') where parameter_id = (
SELECT id from parameters where `key` = 'crp_pc_rol' and global_unit_type_id=3) and global_unit_id = 25;

UPDATE custom_parameters set `value`= (SELECT r.id from roles r where r.global_unit_id = 25 and acronym = 'CRP-Admin') where parameter_id = (
SELECT id from parameters where `key` = 'crp_admin_rol' and global_unit_type_id=3) and global_unit_id = 25;

UPDATE custom_parameters set `value`= (SELECT r.id from roles r where r.global_unit_id = 25 and acronym = 'FPM') where parameter_id = (
SELECT id from parameters where `key` = 'crp_fpm_rol' and global_unit_type_id=3) and global_unit_id = 25;

UPDATE custom_parameters set `value`= (SELECT r.id from roles r where r.global_unit_id = 25 and acronym = 'RPM') where parameter_id = (
SELECT id from parameters where `key` = 'crp_rpm_rol' and global_unit_type_id=3) and global_unit_id = 25;

UPDATE custom_parameters set `value`= (SELECT r.id from roles r where r.global_unit_id = 25 and acronym = 'CP') where parameter_id = (
SELECT id from parameters where `key` = 'crp_cp_role' and global_unit_type_id=3) and global_unit_id = 25;

UPDATE custom_parameters set `value`= 'eib' where parameter_id = (
SELECT id from parameters where `key` = 'crp_custom_file' and global_unit_type_id=3) and global_unit_id = 25;

UPDATE custom_parameters set `value`= 'false' where parameter_id = (
SELECT id from parameters where `key` = 'crp_reporting_active' and global_unit_type_id=3) and global_unit_id = 25;

UPDATE custom_parameters set `value`= 'true' where parameter_id = (
SELECT id from parameters where `key` = 'crp_planning_active' and global_unit_type_id=3) and global_unit_id = 25;

UPDATE custom_parameters set `value`= '2018' where parameter_id = (
SELECT id from parameters where `key` = 'crp_reporting_year' and global_unit_type_id=3) and global_unit_id = 25;

UPDATE custom_parameters set `value`= '2018' where parameter_id = (
SELECT id from parameters where `key` = 'crp_planning_year' and global_unit_type_id=3) and global_unit_id = 25;