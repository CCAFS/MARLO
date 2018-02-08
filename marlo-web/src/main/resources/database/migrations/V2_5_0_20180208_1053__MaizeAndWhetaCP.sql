UPDATE `custom_parameters` SET  `value`='true', `active_since`=NOW() WHERE (`parameter_id`=(SELECT p.id FROM parameters p WHERE p.key="crp_has_contact_point" AND p.global_unit_type_id=(SELECT gt.id FROM global_unit_types gt WHERE gt.name="CRP")) AND global_unit_id = (SELECT id FROM global_units g WHERE g.name="Wheat"));

UPDATE `custom_parameters` SET  `value`='true', `active_since`=NOW() WHERE (`parameter_id`=(SELECT p.id FROM parameters p WHERE p.key="crp_has_contact_point"AND p.global_unit_type_id=(SELECT gt.id  FROM global_unit_types gt WHERE gt.name="CRP")) AND global_unit_id = (SELECT id FROM global_units g WHERE g.name="Maize"));

/*Create roles for Maize/Wheat*/

INSERT INTO `roles` (`description`, `acronym`, `order`, `global_unit_id`) VALUES ('Contact point', 'CP', '9', (SELECT id FROM global_units g WHERE g.name="Wheat"));

INSERT INTO `roles` (`description`, `acronym`, `order`, `global_unit_id`) VALUES ('Contact point', 'CP', '9', (SELECT id FROM global_units g WHERE g.name="Maize"));

/*Create cp relation with parameter cp*/

INSERT INTO `custom_parameters` (`parameter_id`, `value`, `created_by`, `is_active`, `active_since`, `modified_by`, `modification_justification`, `global_unit_id`) VALUES ((SELECT p.id FROM parameters p WHERE p.key="crp_cp_role" AND p.global_unit_type_id=(SELECT gt.id FROM global_unit_types gt WHERE gt.name="CRP")), (SELECT r.id FROM roles r WHERE r.acronym="CP" AND global_unit_id=(SELECT id FROM global_units g WHERE g.name="Wheat")), '3', '1', NOW(), '3', '', (SELECT id FROM global_units g WHERE g.name="Wheat"));

INSERT INTO `custom_parameters` (`parameter_id`, `value`, `created_by`, `is_active`, `active_since`, `modified_by`, `modification_justification`, `global_unit_id`) VALUES ((SELECT p.id FROM parameters p WHERE p.key="crp_cp_role" AND p.global_unit_type_id=(SELECT gt.id FROM global_unit_types gt WHERE gt.name="CRP")), (SELECT r.id FROM roles r WHERE r.acronym="CP" AND global_unit_id=(SELECT id FROM global_units g WHERE g.name="Maize")), '3', '1', NOW(), '3', '', (SELECT id FROM global_units g WHERE g.name="Maize"));