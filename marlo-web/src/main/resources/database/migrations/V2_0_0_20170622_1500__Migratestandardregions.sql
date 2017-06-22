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

/*Update  EAST ASIA AND PACIFIC countries [38 countries]*/
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

/*Update  EUROPE AND CENTRAL ASIA  countries [58 countries]*/
UPDATE loc_elements l
SET l.parent_id = 814
WHERE
  l.is_active
AND l.element_type_id = 2
AND (
  l.`name` = 'Albania'
  OR l.`name` = 'Andorra'
  OR l.`name` = 'Armenia'
  OR l.`name` = 'Austria'
  OR l.`name` = 'Azerbaijan'
  OR l.`name` = 'Belarus'
  OR l.`name` = 'Belgium'
  OR l.`name` = 'Bosnia and Herzegovina'
  OR l.`name` = 'Bulgaria'
/* channel islands*/
  OR l.`name` LIKE "%Jersey%"
  OR l.`name` LIKE "%Guernsey%"
/* end channel islands*/
  OR l.`name` = 'Croatia'
  OR l.`name` = 'Cyprus'
  OR l.`name` = 'Czech Republic'
  OR l.`name` = 'Denmark'
  OR l.`name` = 'Estonia'
  OR l.`name` = 'Faroe Islands'
  OR l.`name` = 'Finland'
  OR l.`name` = 'France'
  OR l.`name` = 'Georgia'
  OR l.`name` = 'Germany'  
  OR l.`name` = 'Gibraltar'
  OR l.`name` = 'Greece'
  OR l.`name` = 'Greenland'
  OR l.`name` = 'Hungary'
  OR l.`name` = 'Iceland'
  OR l.`name` = 'Ireland'
  OR l.`name` = 'Isle of Man'
  OR l.`name` = 'Italy'
  OR l.`name` = 'Kazakhstan'
  /* Kosovo is not in the list of countries or territories provided by the United Nations.
  OR l.`name` = 'Kosovo'
  */
  OR l.`name` LIKE '%Kyrgyz%'
  OR l.`name` = 'Latvia'
  OR l.`name` = 'Liechtenstein'
  OR l.`name` = 'Lithuania'
  OR l.`name` = 'Luxembourg'
  OR l.`name` LIKE '%Macedonia%'
  OR l.`name` = 'Moldova'
  OR l.`name` = 'Monaco'
  OR l.`name` = 'Montenegro'
  OR l.`name` = 'Netherlands'  
  OR l.`name` = 'Norway'
  OR l.`name` = 'Poland'
  OR l.`name` = 'Portugal'
  OR l.`name` = 'Romania'
  OR l.`name` = 'Russian Federation'
  OR l.`name` = 'San Marino'
  OR l.`name` = 'Serbia'
  OR l.`name` like '%Slovak%'
  OR l.`name` = 'Slovenia'
  OR l.`name` = 'Spain'
  OR l.`name` = 'Sweden'
  OR l.`name` = 'Switzerland'
  OR l.`name` = 'Tajikistan'
  OR l.`name` = 'Turkey'
  OR l.`name` = 'Turkmenistan'
  OR l.`name` = 'Ukraine'
  OR l.`name` = 'United Kingdom'
  OR l.`name` = 'Uzbekistan'
);
/*=========================================================*/

/*Update  LATIN AMERICA AND THE CARIBBEAN  countries [42 countries]*/
UPDATE loc_elements l
SET l.parent_id = 815
WHERE
  l.is_active
AND l.element_type_id = 2
AND (
  l.`name` = 'Antigua and Barbuda'
  OR l.`name` = 'Argentina'
  OR l.`name` = 'Aruba' 
  OR l.`name` = 'Bahamas' 
  OR l.`name` = 'Barbados' 
  OR l.`name` = 'Belize' 
  OR l.`name` = 'Bolivia' 
  OR l.`name` = 'Brazil'  
  OR l.`name` = 'British Virgin Islands' 
  OR l.`name` = 'Cayman Islands' 
  OR l.`name` = 'Chile' 
  OR l.`name` = 'Colombia' 
  OR l.`name` = 'Costa Rica' 
  OR l.`name` = 'Cuba' 
  OR l.`name` = 'Curacao'
  OR l.`name` = 'Dominica'
  OR l.`name` = 'Dominican Republic' 
  OR l.`name` = 'Ecuador' 
  OR l.`name` = 'El Salvador' 
  OR l.`name` = 'Grenada' 
  OR l.`name` = 'Guatemala' 
  OR l.`name` = 'Guyana'  
  OR l.`name` = 'Haiti' 
  OR l.`name` = 'Honduras' 
  OR l.`name` = 'Jamaica' 
  OR l.`name` = 'Mexico' 
  OR l.`name` = 'Nicaragua' 
  OR l.`name` = 'Panama'
  OR l.`name` = 'Paraguay'
  OR l.`name` = 'Peru'
  OR l.`name` = 'Puerto Rico' 
  OR l.`name` = 'Sint Maarten (Dutch part)' 
  OR l.`name` = 'Saint Kitts and Nevis' 
  OR l.`name` = 'Saint Lucia' 
  OR l.`name` = 'Saint-Martin (French part)' 
  OR l.`name` = 'Saint Vincent and Grenadines'
  OR l.`name` = 'Suriname' 
  OR l.`name` = 'Trinidad and Tobago' 
  OR l.`name` = 'Turks and Caicos Islands' 
  OR l.`name` = 'Uruguay' 
  OR l.`name` = 'Venezuela' 
  OR l.`name` = 'Virgin Islands, US'  
);
/*=========================================================*/

/* Insert State of Palestine. Consists of the West Bank and the Gaza Strip */
INSERT INTO `loc_elements` (`name`, `iso_alpha_2`, `iso_numeric`, `parent_id`, `element_type_id`, `geoposition_id`, `is_site_integration`, `crp_id`, `is_active`, `created_by`, `active_since`, `modified_by`, `modification_justification`) VALUES ('State of Palestine', 'PS', '275', '816', '2', NULL, '1', NULL, '1', NULL, CURRENT_TIMESTAMP, '1', '');
/* end insert State of Palestine.  */

/*Update  MIDDLE EAST AND NORTH AFRICA  countries [20 countries]*/
UPDATE loc_elements l
SET l.parent_id = 816
WHERE
  l.is_active
AND l.element_type_id = 2
AND (
    l.`name` = 'Algeria'
  OR l.`name` = 'Bahrain'
  OR l.`name` = 'Djibouti' 
  OR l.`name` = 'Egypt' 
  OR l.`name` = 'Islamic Republic of Iran' 
  OR l.`name` = 'Iraq' 
  OR l.`name` = 'Israel' 
  OR l.`name` = 'Jordan'  
  OR l.`name` = 'Kuwait' 
  OR l.`name` = 'Lebanon' 
  OR l.`name` = 'Libya' 
  OR l.`name` = 'Malta' 
  OR l.`name` = 'Morocco' 
  OR l.`name` = 'Oman' 
  OR l.`name` = 'Qatar'  
  OR l.`name` = 'Saudi Arabia' 
  OR l.`name` = 'Syrian Arab Republic (Syria)' 
  OR l.`name` = 'Tunisia' 
  OR l.`name` = 'United Arab Emirates' 
  OR l.`name` = 'West Bank and Gaza' 
  OR l.`name` = 'Yemen'
);
/*=========================================================*/

/*Update  NORTH AMERICA  countries [3 countries]*/
UPDATE loc_elements l
SET l.parent_id = 817
WHERE
  l.is_active
AND l.element_type_id = 2
AND (
    l.`name` = 'Bermuda'  
  OR l.`name` = 'Canada' 
  OR l.`name` = 'United States' 
);
/*=========================================================*/

/*Update  SOUTH ASIA  countries [8 countries]*/
UPDATE loc_elements l
SET l.parent_id = 818
WHERE
  l.is_active
AND l.element_type_id = 2
AND (
    l.`name` = 'Afghanistan' 
  OR l.`name` = 'India' 
  OR l.`name` = 'Pakistan' 
  OR l.`name` = 'Bangladesh' 
  OR l.`name` = 'Maldives' 
  OR l.`name` = 'Sri Lanka' 
  OR l.`name` = 'Bhutan' 
  OR l.`name` = 'Nepal'
);
/*=========================================================*/

/*Update SUB-SAHARAN AFRICA countries [48 countries]*/
UPDATE loc_elements l
SET l.parent_id = 819
WHERE
  l.is_active
AND l.element_type_id = 2
AND (
    l.`name` = 'Angola'  
  OR l.`name` = 'Benin' 
  OR l.`name` = 'Botswana' 
  OR l.`name` = 'Burkina Faso' 
  OR l.`name` = 'Burundi' 
  OR l.`name` = 'Cape Verde' 
  OR l.`name` = 'Cameroon' 
  OR l.`name` = 'Central African Republic' 
  OR l.`name` = 'Chad' 
  OR l.`name` = 'Comoros' 
  OR l.`name` = 'Democratic Republic of the Congo' 
  OR l.`name` = 'Congo (Brazzaville)' 
  OR l.`name` = 'Côte d\'Ivoire' 
  OR l.`name` = 'Equatorial Guinea' 
  OR l.`name` = 'Eritrea' 
  OR l.`name` = 'Ethiopia' 
  OR l.`name` = 'Gabon'  
  OR l.`name` = 'Gambia' 
  OR l.`name` = 'Ghana' 
  OR l.`name` = 'Guinea' 
  OR l.`name` = 'Guinea-Bissau' 
  OR l.`name` = 'Kenya' 
  OR l.`name` = 'Lesotho' 
  OR l.`name` = 'Liberia' 
  OR l.`name` = 'Madagascar' 
  OR l.`name` = 'Malawi' 
  OR l.`name` = 'Mali' 
  OR l.`name` = 'Mauritania' 
  OR l.`name` = 'Mauritius' 
  OR l.`name` = 'Mozambique' 
  OR l.`name` = 'Namibia' 
  OR l.`name` = 'Niger' 
  OR l.`name` = 'Nigeria'  
  OR l.`name` = 'Rwanda' 
  OR l.`name` = 'São Tomé and Principe' 
  OR l.`name` = 'Senegal' 
  OR l.`name` = 'Seychelles' 
  OR l.`name` = 'Sierra Leone' 
  OR l.`name` = 'Somalia' 
  OR l.`name` = 'South Africa' 
  OR l.`name` = 'South Sudan' 
  OR l.`name` = 'Sudan' 
  OR l.`name` = 'Swaziland' 
  OR l.`name` = 'United Republic of Tanzania' 
  OR l.`name` = 'Togo' 
  OR l.`name` = 'Uganda' 
  OR l.`name` = 'Zambia' 
  OR l.`name` = 'Zimbabwe' 
);
/*=========================================================*/


/* Move countries that are not in the world bank list to region 'others' */
UPDATE loc_elements l
LEFT JOIN loc_elements l_parent ON l_parent.id = l.parent_id
SET  l.parent_id = 6
WHERE l.element_type_id=2 AND l.is_active AND  l_parent.is_active AND (l.parent_id < 813 OR l.parent_id > 819);
/*=========================================================*/


/* Deactivate old regions except 'others' region*/
UPDATE loc_elements l
SET  l.is_active=0
WHERE l.element_type_id=1  AND (l.id < 813 OR l.id > 819) AND l.id!=6;
/*=========================================================*/

