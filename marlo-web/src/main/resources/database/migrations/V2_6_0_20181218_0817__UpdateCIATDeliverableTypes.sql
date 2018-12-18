/* Add Governance.., Breeding outputs and Sustainable... to CIAT*/

/* Governance, Administration & Management */

INSERT INTO `deliverable_types` ( `name`, `parent_id`, `description`, `timeline`, `is_fair`, `admin_type`, `global_unit_id`) 
VALUES ('Governance, Administration & Management', NULL, 'Governance, Administration & Management', NULL, '0', '1', '29');
INSERT INTO `deliverable_types` (`name`, `parent_id`, `description`, `timeline`, `is_fair`, `admin_type`, `global_unit_id`) 
VALUES ('Management Meetings', (SELECT id FROM deliverable_types dt WHERE dt.name ="Governance, Administration & Management" AND dt.global_unit_id=29), 'Management Meetings', NULL, '0', '0', NULL);
INSERT INTO `deliverable_types` ( `name`, `parent_id`, `description`, `timeline`, `is_fair`, `admin_type`, `global_unit_id`) 
VALUES ('Events', (SELECT id FROM deliverable_types dt WHERE dt.name ="Governance, Administration & Management" AND dt.global_unit_id=29), 'Events', NULL, '0', '0', NULL);
INSERT INTO `deliverable_types` ( `name`, `parent_id`, `description`, `timeline`, `is_fair`, `admin_type`, `global_unit_id`) 
VALUES ('Governance report', (SELECT id FROM deliverable_types dt WHERE dt.name ="Governance, Administration & Management" AND dt.global_unit_id=29), 'Authorizing plans, commitments and evaluation of the organization’s performance', NULL, '0', '0', NULL);
INSERT INTO `deliverable_types` ( `name`, `parent_id`, `description`, `timeline`, `is_fair`, `admin_type`, `global_unit_id`) 
VALUES ('Administration report', (SELECT id FROM deliverable_types dt WHERE dt.name ="Governance, Administration & Management" AND dt.global_unit_id=29), 'Formulation of plans, framing policies and objectives. Finance reports.', NULL, '0', '0', NULL);
INSERT INTO `deliverable_types` (`name`, `parent_id`, `description`, `timeline`, `is_fair`, `admin_type`, `global_unit_id`) 
VALUES ('Management report', (SELECT id FROM deliverable_types dt WHERE dt.name ="Governance, Administration & Management" AND dt.global_unit_id=29), 'Putting plans and policies into actions.', NULL, '0', '0', NULL);


/* Breeding outputs */

INSERT INTO `deliverable_types` ( `name`, `parent_id`, `description`, `timeline`, `is_fair`, `admin_type`, `global_unit_id`) 
VALUES ('Breeding outputs', NULL, 'Breeding outputs', NULL, '0', '0', '29');
INSERT INTO `deliverable_types` ( `name`, `parent_id`, `description`, `timeline`, `is_fair`, `admin_type`, `global_unit_id`) 
VALUES ('Breeding technology', (SELECT id FROM deliverable_types dt WHERE dt.name ="Breeding outputs" AND dt.global_unit_id=29), 'Improvement of or development of a phenotyping/genotyping tool, breeding approach, new molecular breeding tools/markers, new technology (ie. double haploid)', NULL, '0', '0', NULL);
INSERT INTO `deliverable_types` ( `name`, `parent_id`, `description`, `timeline`, `is_fair`, `admin_type`, `global_unit_id`) 
VALUES ('Characterization', (SELECT id FROM deliverable_types dt WHERE dt.name ="Breeding outputs" AND dt.global_unit_id=29), 'Markers for unique alleles/haplotypes, accession and passport records identified or improved', NULL, '0', '0', NULL);
INSERT INTO `deliverable_types` ( `name`, `parent_id`, `description`, `timeline`, `is_fair`, `admin_type`, `global_unit_id`) 
VALUES ('Line/variety developed', (SELECT id FROM deliverable_types dt WHERE dt.name ="Breeding outputs" AND dt.global_unit_id=29), 'Coded line, CIMMYT maize line, germplasm characterized/developed, new hybrid', NULL, '0', '0', NULL);
INSERT INTO `deliverable_types` ( `name`, `parent_id`, `description`, `timeline`, `is_fair`, `admin_type`, `global_unit_id`) 
VALUES ('variety released', (SELECT id FROM deliverable_types dt WHERE dt.name ="Breeding outputs" AND dt.global_unit_id=29), 'traits', NULL, '0', '0', NULL);
INSERT INTO `deliverable_types` ( `name`, `parent_id`, `description`, `timeline`, `is_fair`, `admin_type`, `global_unit_id`) 
VALUES ('Seed shipment', (SELECT id FROM deliverable_types dt WHERE dt.name ="Breeding outputs" AND dt.global_unit_id=29), 'seed shipment', NULL, '0', '0', NULL);
INSERT INTO `deliverable_types` ( `name`, `parent_id`, `description`, `timeline`, `is_fair`, `admin_type`, `global_unit_id`) 
VALUES ('Product allocation ', (SELECT id FROM deliverable_types dt WHERE dt.name ="Breeding outputs" AND dt.global_unit_id=29), 'Product advancement meetings, product announcements on websites, product profiles ', NULL, '0', '0', NULL);

/* Sustainable intensification outputs */

INSERT INTO `deliverable_types` (`name`, `parent_id`, `description`, `timeline`, `is_fair`, `admin_type`, `global_unit_id`) 
VALUES ('Sustainable intensification outputs', NULL, 'Sustainable intensification outputs', NULL, '0', '0', '29');
INSERT INTO `deliverable_types` (`name`, `parent_id`, `description`, `timeline`, `is_fair`, `admin_type`, `global_unit_id`) 
VALUES ('Technologies', (SELECT id FROM deliverable_types dt WHERE dt.name ="Sustainable intensification outputs" AND dt.global_unit_id=29), 'Crop modelling, Machinery, landscaping , tools and protocols, scaling', NULL, '0', '0', NULL);
INSERT INTO `deliverable_types` (`name`, `parent_id`, `description`, `timeline`, `is_fair`, `admin_type`, `global_unit_id`) 
VALUES ('Agronomic Practices', (SELECT id FROM deliverable_types dt WHERE dt.name ="Sustainable intensification outputs" AND dt.global_unit_id=29), 'e.g. crop rotation, fertilizer application, etc.', NULL, '0', '0', NULL);
