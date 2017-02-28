INSERT INTO `crp_parameters` (`crp_id`, `key`, `value`, `is_active`, `created_by`, `modified_by`, `modification_justification`) VALUES 
('1', 'crp_refresh', '0', '1', '3', '3', 'a');
INSERT INTO `crp_parameters` (`crp_id`, `key`, `value`, `is_active`, `created_by`, `modified_by`, `modification_justification`) VALUES 
('3', 'crp_refresh', '0', '1', '3', '3', 'a');
INSERT INTO `crp_parameters` (`crp_id`, `key`, `value`, `is_active`, `created_by`, `modified_by`, `modification_justification`) VALUES 
('4', 'crp_refresh', '0', '1', '3', '3', 'a');
INSERT INTO `crp_parameters` (`crp_id`, `key`, `value`, `is_active`, `created_by`, `modified_by`, `modification_justification`) VALUES 
('5', 'crp_refresh', '0', '1', '3', '3', 'a');
INSERT INTO `crp_parameters` (`crp_id`, `key`, `value`, `is_active`, `created_by`, `modified_by`, `modification_justification`) VALUES 
('7', 'crp_refresh', '0', '1', '3', '3', 'a');
INSERT INTO `crp_parameters` (`crp_id`, `key`, `value`, `is_active`, `created_by`, `modified_by`, `modification_justification`) VALUES 
('21', 'crp_refresh', '0', '1', '3', '3', 'a');
INSERT INTO `crp_parameters` (`crp_id`, `key`, `value`, `is_active`, `created_by`, `modified_by`, `modification_justification`) VALUES 
('22', 'crp_refresh', '0', '1', '3', '3', 'a');

update crp_parameters set is_active=1, `value`=0 where `key`='crp_parameters' and crp_id=3;