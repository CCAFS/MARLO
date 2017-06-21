/*Deactivate continents from locations and locations types.*/

UPDATE `loc_elements` SET  `is_active`='0' WHERE (`id`='721');
UPDATE `loc_elements` SET  `is_active`='0' WHERE (`id`='722');
UPDATE `loc_elements` SET  `is_active`='0' WHERE (`id`='723');
UPDATE `loc_elements` SET  `is_active`='0' WHERE (`id`='724');
UPDATE `loc_elements` SET  `is_active`='0' WHERE (`id`='725');
UPDATE `loc_element_types` SET `is_active`='0' WHERE (`id`='12');

/*=========================================================*/

/*Create new regions*/

INSERT INTO `loc_elements` (`id`, `name`, `iso_alpha_2`, `iso_numeric`, `parent_id`, `element_type_id`, `geoposition_id`, `is_site_integration`, `crp_id`, `is_active`, `created_by`, `active_since`, `modified_by`, `modification_justification`) VALUES ('813', 'East Asia and Pacific', NULL, NULL, NULL, '1', NULL, '0', NULL, '1', NULL, CURRENT_TIMESTAMP, '1', '');
INSERT INTO `loc_elements` (`id`, `name`, `iso_alpha_2`, `iso_numeric`, `parent_id`, `element_type_id`, `geoposition_id`, `is_site_integration`, `crp_id`, `is_active`, `created_by`, `active_since`, `modified_by`, `modification_justification`) VALUES ('814', 'Europe and Central Asia', NULL, NULL, NULL, '1', NULL, '0', NULL, '1', NULL, CURRENT_TIMESTAMP, '1', '');
INSERT INTO `loc_elements` (`id`, `name`, `iso_alpha_2`, `iso_numeric`, `parent_id`, `element_type_id`, `geoposition_id`, `is_site_integration`, `crp_id`, `is_active`, `created_by`, `active_since`, `modified_by`, `modification_justification`) VALUES ('815', 'Latin America & the Caribbean', NULL, NULL, NULL, '1', NULL, '0', NULL, '1', NULL, CURRENT_TIMESTAMP, '1', '');
INSERT INTO `loc_elements` (`id`, `name`, `iso_alpha_2`, `iso_numeric`, `parent_id`, `element_type_id`, `geoposition_id`, `is_site_integration`, `crp_id`, `is_active`, `created_by`, `active_since`, `modified_by`, `modification_justification`) VALUES ('816', 'Middle East and North Africa', NULL, NULL, NULL, '1', NULL, '0', NULL, '1', NULL, CURRENT_TIMESTAMP, '1', '');
INSERT INTO `loc_elements` (`id`, `name`, `iso_alpha_2`, `iso_numeric`, `parent_id`, `element_type_id`, `geoposition_id`, `is_site_integration`, `crp_id`, `is_active`, `created_by`, `active_since`, `modified_by`, `modification_justification`) VALUES ('817', 'North America', NULL, NULL, NULL, '1', NULL, '0', NULL, '1', NULL, CURRENT_TIMESTAMP, '1', '');
INSERT INTO `loc_elements` (`id`, `name`, `iso_alpha_2`, `iso_numeric`, `parent_id`, `element_type_id`, `geoposition_id`, `is_site_integration`, `crp_id`, `is_active`, `created_by`, `active_since`, `modified_by`, `modification_justification`) VALUES ('818', 'South Asia', NULL, NULL, NULL, '1', NULL, '0', NULL, '1', NULL, CURRENT_TIMESTAMP, '1', '');
INSERT INTO `loc_elements` (`id`, `name`, `iso_alpha_2`, `iso_numeric`, `parent_id`, `element_type_id`, `geoposition_id`, `is_site_integration`, `crp_id`, `is_active`, `created_by`, `active_since`, `modified_by`, `modification_justification`) VALUES ('819', 'Sub-Saharan Africa', NULL, NULL, NULL, '1', NULL, '0', NULL, '1', NULL, CURRENT_TIMESTAMP, '1', '');

/*=========================================================*/

/*Update  EAST ASIA AND PACIFIC  countries*/
UPDATE loc_elements l
SET l.parent_id = 813
WHERE
    l.is_active
AND l.element_type_id = 2
AND (
    l.`name` = 'American Samoa'
    OR l.`name` = 'Australia'
    OR l.`name` = 'Brunei Darussalam'
    OR l.`name` = 'Cambodia'
    OR l.`name` = 'China'
    OR l.`name` = 'Fiji'
    OR l.`name` = 'French Polynesia'
    OR l.`name` = 'Guam'
    OR l.`name` like '%Hong Kong%'
    OR l.`name` = 'Indonesia'
    OR l.`name` = 'Japan'
    OR l.`name` = 'Kiribati'
    OR l.`name` like '%Korea%'
    OR l.`name` = 'Lao PDR'
    OR l.`name` like '%Macao%'
    OR l.`name` = 'Malaysia'
    OR l.`name` = 'Marshall Islands'
    OR l.`name` like '%Micronesia%'
    OR l.`name` = 'Mongolia'
    OR l.`name` like '%Myanmar%'
    OR l.`name` = 'Nauru'
    OR l.`name` = 'New Caledonia'
    OR l.`name` = 'New Zealand'
    OR l.`name` = 'Northern Mariana Islands'
    OR l.`name` = 'Palau'
    OR l.`name` = 'Philippines'
    OR l.`name` = 'Samoa'
    OR l.`name` = 'Singapore'
    OR l.`name` = 'Solomon Islands'
    OR l.`name` LIKE '%Taiwan%'
    OR l.`name` = 'Thailand'
    OR l.`name` = 'Timor-Leste'
    OR l.`name` = 'Papua New Guinea'
    OR l.`name` = 'Tonga'
    OR l.`name` = 'Tuvalu'
    OR l.`name` = 'Vanuatu'
    OR l.`name` = 'Vietnam'
);
/*=========================================================*/
/*=========================================================
UPDATE loc_elements l
SET l.parent_id = 814
SELECT * FROM loc_elements l
WHERE
    l.is_active
AND l.element_type_id = 2
AND (
    l.`name` = 'Albania'  OR
    l.`name` = 'Andorra'  OR
    l.`name` = 'Armenia'  OR
    l.`name` = 'Austria'  OR
    l.`name` = 'Azerbaijan' OR
    l.`name` = 'Belarus'  OR
    l.`name` = 'Belgium'  OR
    l.`name` = 'Bosnia and Herzegovina' OR
    l.`name` = 'Bulgaria' OR
    l.`name` = 'Jersey' OR
    l.`name` = 'Guernsey' OR
    l.`name` = 'Jersey' OR
    l.`name` = 'Croatia'  OR
    l.`name` = 'Cyprus' OR
    l.`name` = 'Czech Republic' OR
    l.`name` = 'Denmark'  OR
    l.`name` = 'Estonia'  OR
    l.`name` = 'Faroe Islands'  OR
    l.`name` = 'Finland'  OR
    l.`name` = 'France' OR  
    l.`name` = 'Georgia'  OR
    l.`name` = 'Germany'
    /*OR l.`name` like '%%' */      
)
ORDER BY l.`name`;
*/
