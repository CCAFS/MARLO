INSERT INTO `crps` VALUES ('7', 'Livestock And Fish', 'livestockfish', '1', '3', '2016-08-05 14:30:00', '3', '  ');

INSERT INTO `roles` VALUES ('38', 'Program Management Unit', 'PMU', '7');
INSERT INTO `roles` VALUES ('39', 'Regional Program Leaders', 'RPL', '7');
INSERT INTO `roles` VALUES ('40', 'Flagship Leaders', 'FPL', '7');
INSERT INTO `roles` VALUES ('41', 'Site Integration Leader', 'SL', '7');
INSERT INTO `roles` VALUES ('42', 'Cluster Leader', 'CL', '7');

INSERT INTO `crp_parameters` (`crp_id`, `key`, `value`, `is_active`, `created_by`, `active_since`, `modified_by`, `modification_justification`) VALUES ('7', 'crp_pmu_rol', '38', '1', '3', '2016-08-05 15:00:00', '3', ' ');
INSERT INTO `crp_parameters` (`crp_id`, `key`, `value`, `is_active`, `created_by`, `active_since`, `modified_by`, `modification_justification`) VALUES ('7', 'crp_has_regions', 'false', '1', '3', '2016-08-05 15:00:00', '3', ' ');
INSERT INTO `crp_parameters` (`crp_id`, `key`, `value`, `is_active`, `created_by`, `active_since`, `modified_by`, `modification_justification`) VALUES ('7', 'crp_fpl_rol', '40', '1', '3', '2016-08-05 15:00:00', '3', ' ');
INSERT INTO `crp_parameters` (`crp_id`, `key`, `value`, `is_active`, `created_by`, `active_since`, `modified_by`, `modification_justification`) VALUES ('7', 'crp_rpl_rol', '39', '1', '3', '2016-08-05 15:00:00', '3', ' ');
INSERT INTO `crp_parameters` (`crp_id`, `key`, `value`, `is_active`, `created_by`, `active_since`, `modified_by`, `modification_justification`) VALUES ('7', 'crp_admin_active', 'true', '1', '3', '2016-08-05 15:00:00', '3', ' ');
INSERT INTO `crp_parameters` (`crp_id`, `key`, `value`, `is_active`, `created_by`, `active_since`, `modified_by`, `modification_justification`) VALUES ('7', 'crp_impPath_active', 'true', '1', '3', '2016-08-05 15:00:00', '3', ' ');
INSERT INTO `crp_parameters` (`crp_id`, `key`, `value`, `is_active`, `created_by`, `active_since`, `modified_by`, `modification_justification`) VALUES ('7', 'crp_cl_rol', '42', '1', '3', '2016-08-05 15:00:00', '3', ' ');
INSERT INTO `crp_parameters` (`crp_id`, `key`, `value`, `is_active`, `created_by`, `active_since`, `modified_by`, `modification_justification`) VALUES ('7', 'crp_sl_rol', '41', '1', '3', '2016-08-05 15:00:00', '3', ' ');