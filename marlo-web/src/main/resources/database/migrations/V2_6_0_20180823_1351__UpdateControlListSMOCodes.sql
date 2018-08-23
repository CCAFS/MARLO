SET FOREIGN_KEY_CHECKS=0;

/* Geographic Scope: Update IATI Standard */

UPDATE `rep_ind_geographic_scopes` SET `iati_name`='1 - Global' WHERE (`id`='1');
UPDATE `rep_ind_geographic_scopes` SET `iati_name`='2 - Regional' WHERE (`id`='2');
UPDATE `rep_ind_geographic_scopes` SET `iati_name`='3 - Multi-national-national' WHERE (`id`='3');
UPDATE `rep_ind_geographic_scopes` SET `iati_name`='4 - National' WHERE (`id`='4');
UPDATE `rep_ind_geographic_scopes` SET `iati_name`='6 - Sub-national: Single first-level administrative area', `definition`='The activity scope covers one first-level subnational administrative area (e.g. country, province, state)' WHERE (`id`='5');


/* Regions*/

/* Add SMO Code, IATI id and IATI name columns*/
ALTER TABLE `rep_ind_regions`
ADD COLUMN `un_m49_code`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT 'SMO Code (UN M.49)' AFTER `sub_region`,
ADD COLUMN `iati_id`  bigint(20) NULL AFTER `un_m49_code`,
ADD COLUMN `iati_name`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT 'IATI Region Name' AFTER `iati_id`;

/* Update regions*/
UPDATE `rep_ind_regions` SET `un_m49_code`='015', `iati_id`='189', `iati_name`='North of Sahara, regional' WHERE (`id`='1');
UPDATE `rep_ind_regions` SET `un_m49_code`='202', `iati_id`='289', `iati_name`='South of Sahara, regional' WHERE (`id`='2');
UPDATE `rep_ind_regions` SET `un_m49_code`='419', `iati_id`='489', `iati_name`='South America, regional' WHERE (`id`='3');
UPDATE `rep_ind_regions` SET `un_m49_code`='021', `iati_id`='389', `iati_name`='North & Central America, regional' WHERE (`id`='4');
UPDATE `rep_ind_regions` SET `un_m49_code`='143', `iati_id`='619', `iati_name`='Central Asia, regional' WHERE (`id`='5');
UPDATE `rep_ind_regions` SET `un_m49_code`='030', `iati_id`='798', `iati_name`='Asia, regional' WHERE (`id`='6');
UPDATE `rep_ind_regions` SET `un_m49_code`='035', `iati_id`='798', `iati_name`='Asia, regional' WHERE (`id`='7');
UPDATE `rep_ind_regions` SET `un_m49_code`='034', `iati_id`='798', `iati_name`='Asia, regional' WHERE (`id`='8');
UPDATE `rep_ind_regions` SET `un_m49_code`='145', `iati_id`='798', `iati_name`='Asia, regional' WHERE (`id`='9');
UPDATE `rep_ind_regions` SET `un_m49_code`='151', `iati_id`='89', `iati_name`='Europe, regional' WHERE (`id`='10');
UPDATE `rep_ind_regions` SET `un_m49_code`='154', `iati_id`='89', `iati_name`='Europe, regional' WHERE (`id`='11');
UPDATE `rep_ind_regions` SET `un_m49_code`='039', `iati_id`='89', `iati_name`='Europe, regional' WHERE (`id`='12');
UPDATE `rep_ind_regions` SET `un_m49_code`='155', `iati_id`='89', `iati_name`='Europe, regional' WHERE (`id`='13');
UPDATE `rep_ind_regions` SET `un_m49_code`='053', `iati_id`='889', `iati_name`='Oceania, regional' WHERE (`id`='14');
UPDATE `rep_ind_regions` SET `un_m49_code`='054', `iati_id`='889', `iati_name`='Oceania, regional' WHERE (`id`='15');
UPDATE `rep_ind_regions` SET `un_m49_code`='057', `iati_id`='889', `iati_name`='Oceania, regional' WHERE (`id`='16');
UPDATE `rep_ind_regions` SET `un_m49_code`='061', `iati_id`='889', `iati_name`='Oceania, regional' WHERE (`id`='17');
UPDATE `rep_ind_regions` SET `name` = 'Sub-Saharan Africa / Eastern Africa',`un_m49_code`='014', `iati_id`='289', `iati_name`='South of Sahara, regional' WHERE (`id`='18');
UPDATE `rep_ind_regions` SET `name` = 'Sub-Saharan Africa / Middle Africa',`un_m49_code`='017', `iati_id`='289', `iati_name`='South of Sahara, regional' WHERE (`id`='19');
UPDATE `rep_ind_regions` SET `name` = 'Sub-Saharan Africa / Southern Africa',`un_m49_code`='018', `iati_id`='289', `iati_name`='South of Sahara, regional' WHERE (`id`='20');
UPDATE `rep_ind_regions` SET `name` = 'Sub-Saharan Africa / Western Africa',`un_m49_code`='011', `iati_id`='289', `iati_name`='South of Sahara, regional' WHERE (`id`='21');

/* Insert new regions*/
INSERT INTO `rep_ind_regions` (`name`, `sub_region`,`un_m49_code`,`iati_id`,`iati_name`) VALUES ('Latin America and the Caribbean / Caribbean', '3','029','389','North & Central America, regional');
INSERT INTO `rep_ind_regions` (`name`, `sub_region`,`un_m49_code`,`iati_id`,`iati_name`) VALUES ('Latin America and the Caribbean / South America', '3','005','489','South America, regional');
INSERT INTO `rep_ind_regions` (`name`, `sub_region`,`un_m49_code`,`iati_id`,`iati_name`) VALUES ('Latin America and the Caribbean / Central America', '3','013','389','North & Central America, regional');

/* SRF*/

/* SRF SLOs */
ALTER TABLE `srf_slos`
ADD COLUMN `smo_code`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL AFTER `description`;

UPDATE `srf_slos` SET `smo_code`='1' WHERE (`id`='1');
UPDATE `srf_slos` SET `smo_code`='2' WHERE (`id`='2');
UPDATE `srf_slos` SET `smo_code`='3' WHERE (`id`='3');

/* SRF IDOs */
ALTER TABLE `srf_idos`
ADD COLUMN `smo_code`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL AFTER `cross_cutting_issue`;

UPDATE `srf_idos` SET `smo_code`='1' WHERE (`id`='1');
UPDATE `srf_idos` SET `smo_code`='2' WHERE (`id`='2');
UPDATE `srf_idos` SET `smo_code`='3' WHERE (`id`='3');
UPDATE `srf_idos` SET `smo_code`='4' WHERE (`id`='4');
UPDATE `srf_idos` SET `smo_code`='5' WHERE (`id`='5');
UPDATE `srf_idos` SET `smo_code`='6',`description`='Improved food security' WHERE (`id`='6');
UPDATE `srf_idos` SET `smo_code`='7' WHERE (`id`='7');
UPDATE `srf_idos` SET `smo_code`='8' WHERE (`id`='8');
UPDATE `srf_idos` SET `smo_code`='9' WHERE (`id`='9');
UPDATE `srf_idos` SET `smo_code`='10',`description`='More sustainably managed agroecosystems' WHERE (`id`='10');
UPDATE `srf_idos` SET `smo_code`='A1' WHERE (`id`='11');
UPDATE `srf_idos` SET `smo_code`='B1' WHERE (`id`='12');
UPDATE `srf_idos` SET `smo_code`='C1' WHERE (`id`='13');
UPDATE `srf_idos` SET `smo_code`='D1' WHERE (`id`='14');


/* SRF Cross-Cutting Issues */
ALTER TABLE `srf_cross_cutting_issues`
ADD COLUMN `smo_code`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL AFTER `name`;

UPDATE `srf_cross_cutting_issues` SET `smo_code`='A' WHERE (`id`='1');
UPDATE `srf_cross_cutting_issues` SET `smo_code`='B' WHERE (`id`='2');
UPDATE `srf_cross_cutting_issues` SET `smo_code`='C' WHERE (`id`='3');
UPDATE `srf_cross_cutting_issues` SET `smo_code`='D' WHERE (`id`='4');

/* SRF Sub-IDOs */
ALTER TABLE `srf_sub_idos`
ADD COLUMN `smo_code`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL AFTER `ido_id`;

UPDATE `srf_sub_idos` SET `smo_code`='1.1.1' WHERE (`id`='1');
UPDATE `srf_sub_idos` SET `smo_code`='1.1.2' WHERE (`id`='2');
UPDATE `srf_sub_idos` SET `smo_code`='1.2.1' WHERE (`id`='3');
UPDATE `srf_sub_idos` SET `smo_code`='1.2.2' WHERE (`id`='4');
UPDATE `srf_sub_idos` SET `smo_code`='1.3.1' WHERE (`id`='5');
UPDATE `srf_sub_idos` SET `smo_code`='1.3.2' WHERE (`id`='6');
UPDATE `srf_sub_idos` SET `smo_code`='1.3.3' WHERE (`id`='7');
UPDATE `srf_sub_idos` SET `smo_code`='1.3.4' WHERE (`id`='8');
UPDATE `srf_sub_idos` SET `smo_code`='1.4.1' WHERE (`id`='9');
UPDATE `srf_sub_idos` SET `smo_code`='1.4.2' WHERE (`id`='10');
UPDATE `srf_sub_idos` SET `smo_code`='1.4.3' WHERE (`id`='11');
UPDATE `srf_sub_idos` SET `smo_code`='1.4.4' WHERE (`id`='12');
UPDATE `srf_sub_idos` SET `smo_code`='1.4.5' WHERE (`id`='13');
UPDATE `srf_sub_idos` SET `smo_code`='2.1.1' WHERE (`id`='14');
UPDATE `srf_sub_idos` SET `smo_code`='2.1.2' WHERE (`id`='15');
UPDATE `srf_sub_idos` SET `smo_code`='2.1.3' WHERE (`id`='16');
UPDATE `srf_sub_idos` SET `smo_code`='2.2.1' WHERE (`id`='17');
UPDATE `srf_sub_idos` SET `smo_code`='2.2.2' WHERE (`id`='18');
UPDATE `srf_sub_idos` SET `smo_code`='2.3.1' WHERE (`id`='19');
UPDATE `srf_sub_idos` SET `smo_code`='2.3.2' WHERE (`id`='20');
UPDATE `srf_sub_idos` SET `smo_code`='2.3.3' WHERE (`id`='21');
UPDATE `srf_sub_idos` SET `smo_code`='3.1.1' WHERE (`id`='22');
UPDATE `srf_sub_idos` SET `smo_code`='3.1.2' WHERE (`id`='23');
UPDATE `srf_sub_idos` SET `smo_code`='3.1.3' WHERE (`id`='24');
UPDATE `srf_sub_idos` SET `smo_code`='3.2.1' WHERE (`id`='25');
UPDATE `srf_sub_idos` SET `smo_code`='3.2.2' WHERE (`id`='26');
UPDATE `srf_sub_idos` SET `smo_code`='3.2.3' WHERE (`id`='27');
UPDATE `srf_sub_idos` SET `smo_code`='3.3.1' WHERE (`id`='28');
UPDATE `srf_sub_idos` SET `smo_code`='3.3.2' WHERE (`id`='29');
UPDATE `srf_sub_idos` SET `smo_code`='3.3.3' WHERE (`id`='30');
UPDATE `srf_sub_idos` SET `smo_code`='A.1.1' WHERE (`id`='31');
UPDATE `srf_sub_idos` SET `smo_code`='A.1.2' WHERE (`id`='32');
UPDATE `srf_sub_idos` SET `smo_code`='A.1.3' WHERE (`id`='33');
UPDATE `srf_sub_idos` SET `smo_code`='A.1.4' WHERE (`id`='34');
UPDATE `srf_sub_idos` SET `smo_code`='A.1.5' WHERE (`id`='35');
UPDATE `srf_sub_idos` SET `smo_code`='B.1.1' WHERE (`id`='36');
UPDATE `srf_sub_idos` SET `smo_code`='B.1.2' WHERE (`id`='37');
UPDATE `srf_sub_idos` SET `smo_code`='B.1.3' WHERE (`id`='38');
UPDATE `srf_sub_idos` SET `smo_code`='C.1.1' WHERE (`id`='39');
UPDATE `srf_sub_idos` SET `smo_code`='C.1.2' WHERE (`id`='40');
UPDATE `srf_sub_idos` SET `smo_code`='C.1.3' WHERE (`id`='41');
UPDATE `srf_sub_idos` SET `smo_code`='C.1.4' WHERE (`id`='42');
UPDATE `srf_sub_idos` SET `smo_code`='D.1.1' WHERE (`id`='43');
UPDATE `srf_sub_idos` SET `smo_code`='D.1.2' WHERE (`id`='44');
UPDATE `srf_sub_idos` SET `smo_code`='D.1.3' WHERE (`id`='45');
UPDATE `srf_sub_idos` SET `smo_code`='D.1.4' WHERE (`id`='46');

/* SRF SLO Indicator Targets*/
UPDATE `srf_slo_indicator_targets` SET narrative='100 million more farm households have adopted improved varieties, breeds, trees, and/or improved management practices.', `targets_indicator`='1.1.2022' WHERE (`id`='1');
UPDATE `srf_slo_indicator_targets` SET narrative='30 million people, of which 50% are women, assisted to exit poverty', `targets_indicator`='1.2.' WHERE (`id`='2');
UPDATE `srf_slo_indicator_targets` SET narrative='Improve the rate of yield increase for major food staples from current < 1% to 1.2-1.5% per year', `targets_indicator`='2.1.' WHERE (`id`='3');
UPDATE `srf_slo_indicator_targets` SET narrative='30 million more people, of which 50% are women, meeting minimum dietary energy requirements', `targets_indicator`='2.2.' WHERE (`id`='4');
UPDATE `srf_slo_indicator_targets` SET narrative='150 million more people, of which 50% are women, without deficiencies of one or more of the following essential micronutrients: iron, zinc, iodine, vitamin A, folate, and vitamin B12', `targets_indicator`='2.3.' WHERE (`id`='5');
UPDATE `srf_slo_indicator_targets` SET narrative='10% reduction in women of reproductive age who are consuming less than the adequate number of food groups', `targets_indicator`='2.4.' WHERE (`id`='6');
UPDATE `srf_slo_indicator_targets` SET narrative='5% increase in water and nutrient (inorganic, biological) use efficiency in agro-ecosystems, including through recycling and reuse', `targets_indicator`='3.1.' WHERE (`id`='7');
UPDATE `srf_slo_indicator_targets` SET narrative='Reduce agriculturally-related greenhouse gas emissions by 0.2 Gt CO2-e yr–1 (5%) compared with business-as-usual scenario in 2022', `targets_indicator`='3.2.' WHERE (`id`='8');
UPDATE `srf_slo_indicator_targets` SET narrative='55 million hectares (ha) degraded land area restored', `targets_indicator`='3.3.' WHERE (`id`='9');
UPDATE `srf_slo_indicator_targets` SET narrative='2.5 million ha of forest saved from deforestation', `targets_indicator`='3.4.' WHERE (`id`='10');
UPDATE `srf_slo_indicator_targets` SET narrative='350 million more farm households have adopted improved varieties, breeds or trees, and/or improved management practice.', `targets_indicator`='1.1.2030' WHERE (`id`='11');
UPDATE `srf_slo_indicator_targets` SET narrative='100 million people, of which 50% are women, assisted to exit poverty', `targets_indicator`='1.2.' WHERE (`id`='12');
UPDATE `srf_slo_indicator_targets` SET narrative='Improve the rate of yield increase for major food staples from current <2.0 to 2.5%/year', `targets_indicator`='2.1.' WHERE (`id`='13');
UPDATE `srf_slo_indicator_targets` SET narrative='150 million more people, of which 50% are women, meeting minimum dietary energy requirements', `targets_indicator`='2.2.' WHERE (`id`='14');
UPDATE `srf_slo_indicator_targets` SET narrative='500 million more people, of which 50% are women, without deficiencies of one or more of the following essential micronutrients: iron, zinc, iodine, vitamin A, folate, and vitamin B12', `targets_indicator`='2.3.' WHERE (`id`='15');
UPDATE `srf_slo_indicator_targets` SET narrative='33% reduction in women of reproductive age who are consuming less than the adequate number of food groups', `targets_indicator`='2.4.' WHERE (`id`='16');
UPDATE `srf_slo_indicator_targets` SET narrative='20% increase in water and nutrient (inorganic, biological) use efficiency in agro-ecosystems, including through recycling and reuse', `targets_indicator`='3.1.' WHERE (`id`='17');
UPDATE `srf_slo_indicator_targets` SET narrative='Reduce agriculturally-related greenhouse gas emissions by 0.8 Gt CO2-e yr–1 (15%) compared with a business-as-usual scenario in 2030', `targets_indicator`='3.2.' WHERE (`id`='18');
UPDATE `srf_slo_indicator_targets` SET narrative='190 million ha degraded land area restored', `targets_indicator`='3.3.' WHERE (`id`='19');
UPDATE `srf_slo_indicator_targets` SET narrative='7.5 million ha of forest saved from deforestation', `targets_indicator`='3.4.' WHERE (`id`='20');
