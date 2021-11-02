create table oc_units (
id bigint(20) NOT NULL AUTO_INCREMENT,
financial_code VARCHAR(20) NOT NULL,
description TEXT,
science_group_id BIGINT,
parent_id BIGINT,
unit_type_id BIGINT,
PRIMARY KEY (id),
KEY `science_group_id` (science_group_id) USING BTREE,
KEY `parent_id` (parent_id),
KEY `unit_type_id` (unit_type_id),
INDEX `oc_unit_id` (`id`) USING BTREE,
CONSTRAINT `oc_units_ibfk_1` FOREIGN KEY (`science_group_id`) REFERENCES `oc_science_group` (`id`),
CONSTRAINT `oc_units_ibfk_2` FOREIGN KEY (`parent_id`) REFERENCES `oc_units` (`id`),
CONSTRAINT `oc_units_ibfk_3` FOREIGN KEY (`unit_type_id`) REFERENCES `oc_unit_types` (`id`)
)ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8;

insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0001', 'Research, Delivery & Impact', null, null, 1);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0003', 'Global Engagement & Innovation', null, null, 1);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0005', 'Institutional Strategy & Systems', null, null, 1);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0010', 'Systems Transformation', 3, 1, 2);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0011', 'Resilient Agri-Food Systems', 4, 1, 2);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0012', 'Genetic Innovation', 5, 1, 2);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0013', 'Platform  - Climate Adatation & Greenhouse Gas Reduction', 3, 1, 2);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0014', 'Platform  - Environmental Health & Biodiversity', 3, 1, 2);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0015', 'Platform  - Gender Equality, Youth and Social Inclusion', 3, 1, 2);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0016', 'Platform  - Nutrition, Health & Food Security', 3, 1, 2);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0017', 'Platform  - Poverty Reduction, Lievelihoods & Jobs', 3, 1, 2);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0018', 'Overall research support', 6, 1, 2);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0030', 'Innovative Finance & Resource Mobilization', 6, 2, 2);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0031', 'Partnerships & Advocacy', 6, 2, 2);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0032', 'Communications & Outreach', 6, 2, 2);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0033', 'Regional Group', 6, 2, 2);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0050', 'Digital Services', 6, 3, 2);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0051', 'Business Operations & Finance', 6, 3, 2);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0052', 'Governance & Assurance', 6, 3, 2);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0053', 'People and Culture', 6, 3, 2);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0100', 'Foresight, Strategy and Innovation', null, 4, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0101', 'Land and Environment', null, 4, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0102', 'Policies and Institutions', null, 4, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0103', 'Water Systems', null, 4, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0110', 'Aquatic Foods Systems', null, 5, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0111', 'Crop-Based Systems', null, 5, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0112', 'Data Scaling and Partnerships', null, 5, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0113', 'Integrated Land and Water Use and Management', null, 5, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0114', 'Livestock-Based Systems', null, 5, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0120', 'Breeding Strategy and Innovation', null, 6, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0121', 'Genebanks', null, 6, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0122', 'Integrated Breeding Research Services', null, 6, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0123', 'Outreach, Partnerships and Scaling', null, 6, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0124', 'Plant Breeding and Pre-breeding', null, 6, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0130', 'Platform - Climate Adatation & Greenhouse Gas Reduction', null, 7, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0140', 'Platform - Environmental Health & Biodiversity', null, 8, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0150', 'Platform - Gender Equality, Youth and Social Inclusion', null, 9, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0160', 'Platform - Nutrition, Health & Food Security', null, 10, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0170', 'Platform - Poverty Reduction, Lievelihoods & Jobs', null, 11, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0180', 'Monitoring and Performance Management', null, 12, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0181', 'Project Coordination', null, 12, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0182', 'Other Research Support Services', null, 12, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0300', 'Climate Funds & Finance', null, 13, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0301', 'Country Funding & IFI', null, 13, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0302', 'Emerging Markets', null, 13, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0303', 'General / unit not yet known', null, 13, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0304', 'ODA / System Council', null, 13, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0305', 'Philanthropy & Sustainable Finance', null, 13, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0306', 'Pipeline Coordination & Quality Standards', null, 13, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0310', 'General / unit not yet known', null, 14, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0311', 'Global Advocacy', null, 14, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0312', 'Global Partnerships', null, 14, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0313', 'Regional and Country partnerships & Advocacy', null, 14, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0320', 'Editorial and Digital Services & Knowledge Management', null, 15, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0321', 'Events & Outreach', null, 15, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0322', 'General / unit not yet known', null, 15, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0323', 'Strategic Comms and Brand', null, 15, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0330', 'Central & West Asia & North Africa (C&WA&NA)', null, 16, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0331', 'East & Southern Africa (E&SA)', null, 16, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0332', 'Latin America and the Caribbean (LAC)', null, 16, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0333', 'South Asia (SA)', null, 16, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0334', 'South East Asia & The Pacific (SEA&P)', null, 16, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0335', 'West & Central Africa (W&CA)', null, 16, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0336', 'General / unit not yet known', null, 16, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0500', 'Data Management and Analytics', null, 17, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0501', 'Digital IT Business Office', null, 17, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0502', 'Digital Solutions', null, 17, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0503', 'Information Security', null, 17, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0504', 'Infrastructure and Operations', null, 17, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0510', 'Business Operation', null, 18, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0511', 'Facilities, Travel & Hospitality Services', null, 18, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0512', 'Finance and Quality Assurance', null, 18, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0513', 'Financial Planning and Analysis', null, 18, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0514', 'Global Administration', null, 18, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0514', 'Global Controller', null, 18, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0515', 'Program Finance', null, 18, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0516', 'Finance (Budget use only)', null, 18, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0520', 'Institutional Risk Management', null, 19, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0521', 'Institutional Strategy', null, 19, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0522', 'Office of Ethics and Compliance', null, 19, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0523', 'Office of Evaluation & Evidence', null, 19, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0524', 'Office of Governance Affairs', null, 19, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0525', 'Office of Internal Audit & Investigations', null, 19, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0526', 'Office of Legal Affairs', null, 19, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0530', 'Gender, Diversity, Inclusion, and Culture', null, 20, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0531', 'HR Operations and Shared Services', null, 20, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0532', 'HR Partnering', null, 20, 3);
insert into oc_units(financial_code, description, science_group_id, parent_id, unit_type_id) value ('0533', 'Strategic HR Support', null, 20, 3);
