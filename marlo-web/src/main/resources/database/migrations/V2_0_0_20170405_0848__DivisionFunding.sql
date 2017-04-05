ALTER TABLE `funding_sources`
ADD COLUMN `division`  varchar(1000) NULL AFTER `type`;

INSERT INTO `crp_parameters` (`crp_id`, `key`, `value`, `is_active`, `created_by`, `modified_by`, `modification_justification`) VALUES 
('1', 'crp_division_fs', '0', '1', '3', '3', '');
INSERT INTO `crp_parameters` (`crp_id`, `key`, `value`, `is_active`, `created_by`, `modified_by`, `modification_justification`) VALUES 
('3', 'crp_division_fs', '1', '1', '3', '3', '');
INSERT INTO `crp_parameters` (`crp_id`, `key`, `value`, `is_active`, `created_by`, `modified_by`, `modification_justification`) VALUES 
('4', 'crp_division_fs', '0', '1', '3', '3', '');
INSERT INTO `crp_parameters` (`crp_id`, `key`, `value`, `is_active`, `created_by`, `modified_by`, `modification_justification`) VALUES 
('5', 'crp_division_fs', '0', '1', '3', '3', '');
INSERT INTO `crp_parameters` (`crp_id`, `key`, `value`, `is_active`, `created_by`, `modified_by`, `modification_justification`) VALUES 
('7', 'crp_division_fs', '0', '1', '3', '3', '');
INSERT INTO `crp_parameters` (`crp_id`, `key`, `value`, `is_active`, `created_by`, `modified_by`, `modification_justification`) VALUES 
('21', 'crp_division_fs', '0', '1', '3', '3', '');
INSERT INTO `crp_parameters` (`crp_id`, `key`, `value`, `is_active`, `created_by`, `modified_by`, `modification_justification`) VALUES 
('22', 'crp_division_fs', '0', '1', '3', '3', '');

