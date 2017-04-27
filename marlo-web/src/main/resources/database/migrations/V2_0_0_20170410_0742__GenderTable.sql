CREATE TABLE `gender_types` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT ,
`description`  text NOT NULL ,
`crp_id`  bigint(20)  NULL ,
PRIMARY KEY (`id`),
FOREIGN KEY (`crp_id`) REFERENCES `crps` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
)
;

INSERT INTO `crp_parameters` (`crp_id`, `key`, `value`, `is_active`, `created_by`, `modified_by`, `modification_justification`) VALUES 
('1', 'crp_custom_gender', '0', '1', '3', '3', '');
INSERT INTO `crp_parameters` (`crp_id`, `key`, `value`, `is_active`, `created_by`, `modified_by`, `modification_justification`) VALUES 
('3', 'crp_custom_gender', '0', '1', '3', '3', '');
INSERT INTO `crp_parameters` (`crp_id`, `key`, `value`, `is_active`, `created_by`, `modified_by`, `modification_justification`) VALUES 
('4', 'crp_custom_gender', '0', '1', '3', '3', '');
INSERT INTO `crp_parameters` (`crp_id`, `key`, `value`, `is_active`, `created_by`, `modified_by`, `modification_justification`) VALUES 
('5', 'crp_custom_gender', '1', '1', '3', '3', '');
INSERT INTO `crp_parameters` (`crp_id`, `key`, `value`, `is_active`, `created_by`, `modified_by`, `modification_justification`) VALUES 
('7', 'crp_custom_gender', '0', '1', '3', '3', '');
INSERT INTO `crp_parameters` (`crp_id`, `key`, `value`, `is_active`, `created_by`, `modified_by`, `modification_justification`) VALUES 
('21', 'crp_custom_gender', '0', '1', '3', '3', '');
INSERT INTO `crp_parameters` (`crp_id`, `key`, `value`, `is_active`, `created_by`, `modified_by`, `modification_justification`) VALUES 
('22', 'crp_custom_gender', '0', '1', '3', '3', '');