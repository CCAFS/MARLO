-- Improving SLOs text
UPDATE `srf_slos` SET `title` = 'Reduce Poverty' WHERE `srf_slos`.`id` = 1;
UPDATE `srf_slos` SET `title` = 'Improve food and nutrition security' WHERE `srf_slos`.`id` = 2;
UPDATE `srf_slos` SET `title` = 'Improve Natural resources and ecosystem services' WHERE `srf_slos`.`id` = 3;


-- Improving Cross-Cutting text
UPDATE `srf_cross_cutting_issues` SET `name` = 'Climate Change' WHERE `srf_cross_cutting_issues`.`id` = 1; 
UPDATE `srf_cross_cutting_issues` SET `name` = 'Gender and Youth' WHERE `srf_cross_cutting_issues`.`id` = 2; 
UPDATE `srf_cross_cutting_issues` SET `name` = 'Policies and Institutions' WHERE `srf_cross_cutting_issues`.`id` = 3; 
UPDATE `srf_cross_cutting_issues` SET `name` = 'Capacity development' WHERE `srf_cross_cutting_issues`.`id` = 4;


-- Adding SLOs Indicators
INSERT INTO `srf_slo_indicators` (`id`, `title`, `slo_id`, `description`, `is_active`, `created_by`, `active_since`, `modified_by`, `modification_justification`) VALUES
(1, '350 million more farm households have adopted improved varieties, breeds or trees, and or improved management practice', 1, '', 1, 3, '2016-07-27 19:10:52', 3, 'Initial Data'),
(2, '100 million people, of which 50% are women, assisted to exit poverty', 1, '', 1, 3, '2016-07-27 19:10:52', 3, 'Initial Data'),
(3, 'Improve the rate of yield increase for major food staples from current <2.0 to 2.5%/year', 2, '', 1, 3, '2016-07-27 19:22:44', 3, 'Initial Data'),
(4, '150 million more people, of which 50% are women, meeting minimum dietary energy requirements', 2, '', 1, 3, '2016-07-27 19:22:44', 3, 'Initial Data'),
(5, '500 million more people, of which 50% are women, without deficiencies of one or more of the following essential micronutrients: iron, zinc, iodine, vitamin A, folate, and vitamin B12', 2, '', 1, 3, '2016-07-27 19:22:44', 3, 'Initial Data'),
(6, '33% reduction in women of reproductive age who are consuming less than the adequate number of food groups', 2, '', 1, 3, '2016-07-27 19:22:44', 3, 'Initial Data'),
(7, '20% increase in water and nutrient (inorganic, biological) use efficiency in agroecosystems, including through recycling and reuse', 3, '', 1, 3, '2016-07-27 19:22:44', 3, 'Initial Data'),
(8, 'Reduce agriculturally-related greenhouse gas emissions by 0.8 Gt CO2-e yr–1 (15%) compared with a business-as-usual scenario in 2030', 3, '', 1, 3, '2016-07-27 19:22:44', 3, 'Initial Data'),
(9, '190 million ha degraded land area restored', 3, '', 1, 3, '2016-07-27 19:22:44', 3, 'Initial Data'),
(10, '7.5 million ha of forest saved from deforestation', 3, '', 1, 3, '2016-07-27 19:22:44', 3, 'Initial Data');
