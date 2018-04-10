INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('Advanced Research Institutes', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'Advanced Research Institutes';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('Agricultural Research Initiatives', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'Agricultural Research Initiatives';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('CGIAR Centers', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'CGIAR Centers';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('Civil Society Organizations (CSO)', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'Civil Society Organizations (CSO)';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('Countries/ States', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'Countries/ States';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('CRPs', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'CRPs';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('Decision Makers', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'Decision Makers';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('Farmers', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'Farmers';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('Farming households', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'Farming households';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('Fisheries', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'Fisheries';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('Government Agencies', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'Government Agencies';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('Hectares', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'Hectares';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('International Research Organizations', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'International Research Organizations';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('Livestock Dependent Communities', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'Livestock Dependent Communities';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('Market Buyers', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'Market Buyers';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('Market Sellers', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'Market Sellers';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('Markets', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'Markets';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('National Research and Extension Institutes (NAREs)', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'National Research and Extension Institutes (NAREs)';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('National Research Institutes (NARs)', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'National Research Institutes (NARs)';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('Non-governmental Organizations (NGOs)', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'Non-governmental Organizations (NGOs)';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('Partners', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'Partners';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('Partnerships', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'Partnerships';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('Persons', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'Persons';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('Policy Decisions', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'Policy Decisions';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('Policy Makers', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'Policy Makers';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('Private Sector Organizations', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'Private Sector Organizations';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('Purchasing Companies', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'Purchasing Companies';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('Recommendations Implemented', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'Recommendations Implemented';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('Regional Bodies', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'Regional Bodies';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('Researchers', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'Researchers';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('Small and Medium Enterprises (SMEs) ', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'Small and Medium Enterprises (SMEs) ';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('Stakeholders', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'Stakeholders';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('Technology Developers', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'Technology Developers';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('New investments', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'New investments';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('Value Chain Actors', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'Value Chain Actors';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('Wholesale companies', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'Wholesale companies';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('Women Farmer Groups', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'Women Farmer Groups';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('Other', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'Other';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('Breeders', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'Breeders';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('Development Agencies', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'Development Agencies';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('Donors', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'Donors';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('Financial Institutions (private)', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'Financial Institutions (private)';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('Extension Agencies (private)', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'Extension Agencies (private)';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('Extension Agencies (public)', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'Extension Agencies (public)';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('Financial institutions (public)', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'Financial institutions (public)';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('Input suppliers', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'Input suppliers';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('Land managers', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'Land managers';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('Land-use planners', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'Land-use planners';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('Local Community Members', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'Local Community Members';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('Local governments', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'Local governments';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('Ministry of Environment', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'Ministry of Environment';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('Ministry of Agriculture', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'Ministry of Agriculture';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('National Governments', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'National Governments';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('Other Government Ministry', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'Other Government Ministry';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('Producers', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'Producers';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('Product transformation/processing companies', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'Product transformation/processing companies';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('Product Commercialization Companies', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'Product Commercialization Companies';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('Hybrid Varieties', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'Hybrid Varieties';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('Lines', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'Lines';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('New Genes', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'New Genes';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('Research Partners', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'Research Partners';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('Tools', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'Tools';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('Varieties Released', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'Varieties Released';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('Scientists', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'Scientists';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('Variety', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'Variety';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('Strategies', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'Strategies';

INSERT INTO srf_target_units (`name`, is_active, created_by, active_since, modified_by, modification_justification)
VALUES ('Technologies', 1, 3,NOW(), 3, '');

INSERT INTO crp_target_units (target_unit_id, is_active, active_since, created_by, modified_by, modification_justification, global_unit_id)
SELECT id, 1, NOW(), 3, 3, '', 29 from srf_target_units where `name` = 'Technologies';