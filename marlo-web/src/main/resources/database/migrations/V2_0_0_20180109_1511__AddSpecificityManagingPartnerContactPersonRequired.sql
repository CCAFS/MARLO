INSERT INTO `parameters` (`key`, `description`, `format`, `category`) VALUES ('crp_managing_partners_contact_persons', 'Contact person is required for Managing partner', '1', '2');

INSERT INTO `custom_parameters` (`parameter_id`, `crp_id`, `value`, `created_by`, `modified_by`, `modification_justification`) VALUES ((SELECT id FROM parameters p WHERE p.key="crp_managing_partners_contact_persons") , '1', 'true', '3', '3', ' ');
INSERT INTO `custom_parameters` (`parameter_id`, `crp_id`, `value`, `created_by`, `modified_by`, `modification_justification`) VALUES ((SELECT id FROM parameters p WHERE p.key="crp_managing_partners_contact_persons"), '3', 'false', '3', '3', ' ');
INSERT INTO `custom_parameters` (`parameter_id`, `crp_id`, `value`, `created_by`, `modified_by`, `modification_justification`) VALUES ((SELECT id FROM parameters p WHERE p.key="crp_managing_partners_contact_persons"), '4', 'true', '3', '3', ' ');
INSERT INTO `custom_parameters` (`parameter_id`, `crp_id`, `value`, `created_by`, `modified_by`, `modification_justification`) VALUES ((SELECT id FROM parameters p WHERE p.key="crp_managing_partners_contact_persons"), '5', 'true', '3', '3', ' ');
INSERT INTO `custom_parameters` (`parameter_id`, `crp_id`, `value`, `created_by`, `modified_by`, `modification_justification`) VALUES ((SELECT id FROM parameters p WHERE p.key="crp_managing_partners_contact_persons"), '7', 'true', '3', '3', ' ');
INSERT INTO `custom_parameters` (`parameter_id`, `crp_id`, `value`, `created_by`, `modified_by`, `modification_justification`) VALUES ((SELECT id FROM parameters p WHERE p.key="crp_managing_partners_contact_persons"), '11', 'true', '3', '3', ' ');
INSERT INTO `custom_parameters` (`parameter_id`, `crp_id`, `value`, `created_by`, `modified_by`, `modification_justification`) VALUES ((SELECT id FROM parameters p WHERE p.key="crp_managing_partners_contact_persons"), '21', 'true', '3', '3', ' ');
INSERT INTO `custom_parameters` (`parameter_id`, `crp_id`, `value`, `created_by`, `modified_by`, `modification_justification`) VALUES ((SELECT id FROM parameters p WHERE p.key="crp_managing_partners_contact_persons"), '22', 'true', '3', '3', ' ');