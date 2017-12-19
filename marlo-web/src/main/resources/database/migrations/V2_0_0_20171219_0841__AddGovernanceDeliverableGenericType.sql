UPDATE deliverable_types dt SET dt.crp_id=(SELECT c.id FROM crps c WHERE c.acronym="CCAFS"),dt.description = "Governance classification for CCAFS deliverables"
WHERE dt.name="Governance, Administration & Management";

INSERT INTO deliverable_types (name,description,admin_type) 
VALUES("Governance, Administration & Management","New deliverables governance classification",1);

INSERT INTO deliverable_types (name,parent_id) 
VALUES("Plan/strategy",(SELECT dt.id FROM deliverable_types dt WHERE dt.name="Governance, Administration & Management" AND dt.crp_id IS NULL)),
("Concept note",(SELECT dt.id FROM deliverable_types dt WHERE dt.name="Governance, Administration & Management" AND dt.crp_id IS NULL)),
("Communication material",(SELECT dt.id FROM deliverable_types dt WHERE dt.name="Governance, Administration & Management" AND dt.crp_id IS NULL)),
("Impact assessment/evaluation report",(SELECT dt.id FROM deliverable_types dt WHERE dt.name="Governance, Administration & Management" AND dt.crp_id IS NULL)),
("Other report",(SELECT dt.id FROM deliverable_types dt WHERE dt.name="Governance, Administration & Management" AND dt.crp_id IS NULL));

INSERT INTO `parameters` (`key`, `description`, `format`, `default_value`, `category`) VALUES ('crp_has_specific_management_deliverable_type', 'CRP use specific deliverable types for management projects', '1', 'false', '2');
INSERT INTO `custom_parameters` (`parameter_id`, `crp_id`, `value`, `created_by`, `is_active`, `active_since`,`modified_by`, `modification_justification`) VALUES ((SELECT p.id FROM parameters p WHERE p.key="crp_has_specific_management_deliverable_type"), '1', 'true', '3', '1', CURRENT_TIMESTAMP, '1', '');
INSERT INTO `custom_parameters` (`parameter_id`, `crp_id`, `value`, `created_by`, `is_active`, `active_since`,`modified_by`, `modification_justification`) VALUES ((SELECT p.id FROM parameters p WHERE p.key="crp_has_specific_management_deliverable_type"), '3', 'false', '3', '1', CURRENT_TIMESTAMP, '1', '');
INSERT INTO `custom_parameters` (`parameter_id`, `crp_id`, `value`, `created_by`, `is_active`, `active_since`,`modified_by`, `modification_justification`) VALUES ((SELECT p.id FROM parameters p WHERE p.key="crp_has_specific_management_deliverable_type"), '4', 'false', '3', '1', CURRENT_TIMESTAMP, '1', '');
INSERT INTO `custom_parameters` (`parameter_id`, `crp_id`, `value`, `created_by`, `is_active`, `active_since`,`modified_by`, `modification_justification`) VALUES ((SELECT p.id FROM parameters p WHERE p.key="crp_has_specific_management_deliverable_type"), '5', 'false', '3', '1', CURRENT_TIMESTAMP, '1', '');
INSERT INTO `custom_parameters` (`parameter_id`, `crp_id`, `value`, `created_by`, `is_active`, `active_since`,`modified_by`, `modification_justification`) VALUES ((SELECT p.id FROM parameters p WHERE p.key="crp_has_specific_management_deliverable_type"), '7', 'false', '3', '1', CURRENT_TIMESTAMP, '1', '');
INSERT INTO `custom_parameters` (`parameter_id`, `crp_id`, `value`, `created_by`, `is_active`, `active_since`,`modified_by`, `modification_justification`) VALUES ((SELECT p.id FROM parameters p WHERE p.key="crp_has_specific_management_deliverable_type"), '21', 'false', '3', '1', CURRENT_TIMESTAMP, '1', '');
INSERT INTO `custom_parameters` (`parameter_id`, `crp_id`, `value`, `created_by`, `is_active`, `active_since`,`modified_by`, `modification_justification`) VALUES ((SELECT p.id FROM parameters p WHERE p.key="crp_has_specific_management_deliverable_type"), '22', 'false', '3', '1', CURRENT_TIMESTAMP, '1', '');