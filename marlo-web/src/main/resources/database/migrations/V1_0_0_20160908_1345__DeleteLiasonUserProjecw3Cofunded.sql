START TRANSACTION;
SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for projects_bilateral_cofinancing
-- ----------------------------
DROP TABLE IF EXISTS `projects_bilateral_cofinancing`;
CREATE TABLE `projects_bilateral_cofinancing` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` text,
  `start_date` datetime DEFAULT NULL,
  `end_date` datetime DEFAULT NULL,
  `agreement` int(11) DEFAULT NULL,
  `liason_institution` bigint(20) DEFAULT NULL,
  `donor` bigint(20) DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `active_since` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified_by` bigint(20) NOT NULL,
  `modification_justification` text NOT NULL,
  `budget` decimal(10,0) DEFAULT NULL,
  `crp_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `liason_institution` (`liason_institution`),
  KEY `donor` (`donor`),
  KEY `created_by` (`created_by`),
  KEY `modified_by` (`modified_by`),
  KEY `projects_bilateral_cofinancing_ibfk_10` (`crp_id`),
  CONSTRAINT `projects_bilateral_cofinancing_ibfk_10` FOREIGN KEY (`crp_id`) REFERENCES `crps` (`id`),
  CONSTRAINT `projects_bilateral_cofinancing_ibfk_5` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`),
  CONSTRAINT `projects_bilateral_cofinancing_ibfk_6` FOREIGN KEY (`liason_institution`) REFERENCES `liaison_institutions` (`id`),
  CONSTRAINT `projects_bilateral_cofinancing_ibfk_8` FOREIGN KEY (`donor`) REFERENCES `institutions` (`id`),
  CONSTRAINT `projects_bilateral_cofinancing_ibfk_9` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=248 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of projects_bilateral_cofinancing
-- ----------------------------
INSERT INTO `projects_bilateral_cofinancing` VALUES ('131', 'UVM CIAT Reducing and Accounting for Agriculture-Driven GHG Emissions in USAID\'s Agriculture Related Work', '2015-01-01 00:00:00', '2016-12-31 00:00:00', null, '4', null, '1', '3', '2014-11-11 15:01:47', '31', 'minor edit', '1649318', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('134', 'Crowdsourcing Crop Improvement: Evidence base and outscaling model', '2015-03-01 00:00:00', '2018-02-28 00:00:00', null, '12', null, '1', '3', '2014-09-12 02:32:24', '844', 'Change file', '1000000', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('136', 'Mainstreaming agrobiodiversity conservation and utilization in agricultural sector to ensure \r\n ecosystem services and reduce vulnerability', '2015-07-01 00:00:00', '2018-12-31 00:00:00', null, '12', null, '1', '3', '2014-09-28 01:58:51', '844', 'Change file', '1285021', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('137', 'Linking crop index insurance schemes to bilateral research projects that are developing and generating climate-adapted maize germplasm in Sub-Saharan Africa', '2015-01-01 00:00:00', '2018-01-31 00:00:00', null, '15', null, '1', '3', '2014-09-28 18:25:48', '100', 'Added documents linking index insurance to climate-adapted maize germplasm developed by DTMA project', '780000', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('140', 'Integrated Agricultural Production and Food Security Forecasting System for East Africa', '2015-01-01 00:00:00', '2018-01-31 00:00:00', null, '15', null, '1', '3', '2014-10-04 11:26:34', '844', 'Change', '300000', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('141', 'Policy Action for Sustainable Intensification of Ugandan Cropping Systems', '2014-03-01 00:00:00', '2017-12-31 00:00:00', null, '21', null, '1', '3', '2014-10-06 05:30:27', '271', 'Added new info', '300000', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('142', 'Trade-offs and synergies in climate change adaptation and mitigation in coffee and cocoa systems', '2013-06-01 00:00:00', '2016-04-01 00:00:00', null, '21', null, '1', '3', '2014-10-06 06:17:17', '271', 'Added info', '250000', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('145', 'Enhancement of modeling tools (IMPACT), to handle variability and land-use, for improved analysis of climate change impacts.', '2015-01-01 00:00:00', '2016-12-31 00:00:00', null, '20', null, '1', '3', '2014-10-07 21:34:50', '52', '.', '502000', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('146', 'USAID-Climate Smart Agriculture (CSA) Strategic Support for Feed the Future Stakeholders and National Institutions', '2015-01-01 00:00:00', '2016-12-31 00:00:00', null, '13', null, '1', '3', '2014-10-08 19:56:10', '69', 'Edited', '625000', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('147', 'Assessing the implications of private commitments in land use change trajectories and ranching intensification', '2015-01-01 00:00:00', '2018-12-31 00:00:00', null, '14', null, '1', '3', '2014-10-08 20:42:31', '62', 'Project document has been updated', '200000', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('148', 'Convenio MADR - Project with the Colombian Ministry of Agriculture on Climate Change and Agricultre', '2014-03-01 00:00:00', '2016-12-31 00:00:00', null, '13', null, '1', '3', '2014-10-09 06:45:15', '69', 'contract added', '60000', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('149', 'Remote Sensing as a Monitoring Tool for Smallholder\'s Cropping Area Determination', '2014-03-01 00:00:00', '2016-07-31 00:00:00', null, '16', null, '1', '3', '2014-10-09 07:03:27', '1', 'Adjusting end date to July 31th, 2016 as requested by Roberto Quiroz.', '200000', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('151', 'Scaling of climate smart practices that reduce GHG emissions.', '2015-01-01 00:00:00', '2018-01-31 00:00:00', null, '15', null, '1', '3', '2014-10-09 11:56:19', '844', '', '1090000', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('152', 'Objective 4. SIMLESA II Project:  To support the development of local and regional innovations systems and scaling out modalities', '2015-01-01 00:00:00', '2018-06-30 00:00:00', null, '15', null, '1', '3', '2014-10-09 15:49:47', '95', 'Sites added', '500000', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('154', 'Sustainable development options to enhance climate change mitigation and adaptation capacities in Colombian and the Peruvian Amazon.', '2015-01-01 00:00:00', '2017-12-31 00:00:00', null, '13', null, '1', '3', '2014-10-10 04:30:15', '69', 'Added Proposal', '187000', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('156', 'Enhancing climate-resilience of Agricultural Livelihoods', '2014-03-01 00:00:00', '2017-10-31 00:00:00', null, '21', null, '1', '3', '2014-10-10 13:06:59', '271', 'Attached file', '228308', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('157', 'Increasing food security and farming system resilience in East Africa through wide-scale adoption of climate-smart agricultural practices', '2014-01-01 00:00:00', '2016-12-31 00:00:00', null, '13', null, '1', '3', '2014-10-10 13:48:27', '69', 'Proposal Added', '550000', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('158', 'CSI India: Enhancing farmers’ adaptive capacity by developing CSI - India Food Security Portal', '2015-01-01 00:00:00', '2016-01-31 00:00:00', null, '20', null, '1', '3', '2014-10-10 21:44:13', '98', 'Region and Flagship have been added', '190000', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('159', 'Climate change analyses to support participatory investment plans in coffee-based landscapes', '2015-01-01 00:00:00', '2018-12-31 00:00:00', null, '12', null, '1', '3', '2014-10-10 23:41:31', '843', 'Uploading report as per Deissy request', '50000', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('161', 'Grand Challenges Explorations Round 12: Less is More: The 5Q Approach', '2014-03-01 00:00:00', '2015-12-30 00:00:00', null, '13', null, '1', '3', '2014-10-12 12:42:05', '66', '', '550', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('164', 'Climate change, food security and policy reform in India.', '2014-04-01 00:00:00', '2016-03-31 00:00:00', null, '20', null, '1', '3', '2014-11-11 21:09:56', '241', 'no significant edits made', '61798', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('166', 'USAID- Unlocking Private Sector Engagement and Creating a Learning Community in Climate Smart Agriculture', '2016-01-01 00:00:00', '2017-12-31 00:00:00', null, '13', null, '1', '3', '2015-10-23 21:17:25', '55', '', '1078556', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('167', 'Strengthening national capacities to implement the International Treaty of PGRFA: Genetic Resources Policy Initiative (GRPI) Phase II (DGIS)', '2015-12-31 00:00:00', '2016-12-31 00:00:00', null, '12', null, '1', '3', '2015-10-23 22:25:00', '243', '', '800000', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('168', 'Mitigation Options to Reduce Methane Emissions in Paddy Rice', '2016-01-01 00:00:00', '2016-05-31 00:00:00', null, '23', null, '1', '3', '2015-10-26 03:49:10', '108', '', '120000', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('170', 'Climate-smart Agriculture in Rice-based Systems of Vietnamese Deltas: Technologies, Knowledge Products and Decision Tools', '2016-04-01 00:00:00', '2018-12-31 00:00:00', null, '23', null, '1', '3', '2015-10-26 08:25:27', '108', '', '493000', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('172', 'Salinity Advisory as a Location-specific Timely Service for Rice farmers (SALTS)', '2016-01-01 00:00:00', '2017-02-28 00:00:00', null, '23', null, '1', '3', '2015-10-26 09:17:18', '108', '', '80000', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('174', 'Scalable straw management options for sustainability and low environmental footprint in rice-based production systems', '2016-01-01 00:00:00', '2016-12-31 00:00:00', null, '23', null, '1', '3', '2015-10-26 09:27:27', '108', '', '200000', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('175', 'Remote Sensing-based Information and Insurance for Crops in Emerging Economies - Phase II (RIICE-Ph2)', '2016-01-01 00:00:00', '2016-12-31 00:00:00', null, '23', null, '1', '3', '2015-10-26 09:32:51', '108', '', '620301', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('176', 'Climate Resilient Planning: Interdisciplinary Research to Improve Information Provision for Decision Making', '2016-01-01 00:00:00', '2017-03-31 00:00:00', null, '12', null, '1', '3', '2015-10-26 12:46:29', '61', '', '75000', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('177', 'Mutually supportive implementation of the Nagoya Protocol and Plant Treaty (Darwin Initiative)', '2016-01-01 00:00:00', '2018-03-31 00:00:00', null, '12', null, '1', '3', '2015-10-26 13:04:33', '243', '', '279692', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('178', 'Bioversity GRDC Partnership in Vavilov-Frankel Fellowships', '2011-07-01 00:00:00', '2016-06-30 00:00:00', null, '12', null, '1', '3', '2015-10-26 13:21:34', '243', '', '15000', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('180', 'Use and conservation of agrobiodiversity for increased agricultural sustainability, smallholder wellbeing and resilience to climate change in India', '2016-01-01 00:00:00', '2018-03-31 00:00:00', null, '12', null, '1', '3', '2015-10-26 14:17:02', '243', '', '1800000', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('181', 'Integrated Seed Sector Development - Africa - ‘Global Policies and National Realities’', '2016-01-01 00:00:00', '2016-08-31 00:00:00', null, '12', null, '1', '3', '2015-10-26 14:32:45', '243', '', '6800', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('182', 'Standard assessment of mitigation potential and livelihoods in smallholder systems (SAMPLES)', '2016-01-01 00:00:00', '2016-06-30 00:00:00', null, '23', null, '1', '3', '2015-10-28 08:04:08', '108', '', '50000', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('184', 'Farmers’ seed systems and community seed banks in South Africa: a baseline study of selected sites', '2015-12-31 00:00:00', '2016-12-31 00:00:00', null, '12', null, '1', '3', '2015-10-28 09:13:56', '243', '', '188400', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('185', 'Reducing Methane Emissions from Rice Production in Vietnam and Bangladesh', '2016-07-01 00:00:00', '2018-12-31 00:00:00', null, '23', null, '1', '3', '2015-10-29 06:59:14', '108', '', '1000000', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('187', 'Greenhouse gas mitigation in irrigated rice systems in Asia Part 2 (MIRSA 2)', '2016-04-01 00:00:00', '2018-02-28 00:00:00', null, '23', null, '1', '3', '2015-10-30 06:42:56', '108', '', '24835', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('188', 'Developing climate smart districts in Uganda', '2016-01-01 00:00:00', '2017-01-01 00:00:00', null, '21', null, '1', '3', '2015-10-30 09:42:12', '271', '', '150000', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('189', 'Enhancing adaptive capacity of women and ethnic minority smallholder farmers through improved agro-climate information in Laos', '2015-11-01 00:00:00', '2018-12-31 00:00:00', null, '9', null, '1', '3', '2015-11-02 01:45:16', '872', '', '1084503', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('190', 'The Agricultural Modeling Intercomparison and Improvement project (AgMIP) - Regional Integrated Assessments in SSA and SA', '2015-01-01 00:00:00', '2017-03-31 00:00:00', null, '19', null, '1', '3', '2015-11-04 10:30:31', '162', '', '3000000', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('192', 'WD - Big Data for Climate Smart Agriculture Enhancing & Sustaining Rice Systems for Latin America and the World.', '2016-01-01 00:00:00', '2016-06-30 00:00:00', null, '13', null, '1', '3', '2015-11-04 16:15:35', '69', '', '60000', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('194', 'MAFF(Japan) Environmental Protection using Traits Associated with Biological Nitrification Inhibition', '2015-01-01 00:00:00', '2019-05-31 00:00:00', null, '13', null, '1', '3', '2015-11-04 17:10:14', '69', '', '69127', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('195', 'JIRCAS - Quantifying the BNI-residual effect from B. humidicola on N-recovery and N-use efficiencyof the subsequent annual crops', '2016-01-01 00:00:00', '2016-03-31 00:00:00', null, '13', null, '1', '3', '2015-11-04 17:16:04', '69', '', '21774', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('198', 'Surveillance of Climate-smart Agriculture for Nutrition (SCAN)', '2015-09-01 00:00:00', '2017-09-30 00:00:00', null, '18', null, '1', '3', '2015-11-04 22:24:42', '73', '', '365000', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('199', 'Tanzania Climate Smart Agriculture Reference/Learning Sites (ICRAF-USDA)', '2015-09-16 00:00:00', '2017-03-15 00:00:00', null, '18', null, '1', '3', '2015-11-04 23:45:19', '73', '', '235000', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('201', 'Strengthening cultivar diversity in Ethiopian seed systems to manage climate related risks and foster nutrition', '2016-01-01 00:00:00', '2019-12-31 00:00:00', null, '12', null, '1', '3', '2015-11-05 08:33:09', '243', '', '936904', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('202', 'In situ assessment of GHG emissions from two livestock systems in East Africa', '2015-02-16 00:00:00', '2017-08-28 00:00:00', null, '22', null, '1', '3', '2015-11-05 09:00:52', '86', '', '1410000', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('203', 'Climate and Clean Air Coalition (CCAC) - manure management', '2015-01-01 00:00:00', '2015-12-31 00:00:00', null, '22', null, '1', '3', '2015-11-05 09:15:42', '910', '', '255000', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('204', 'Greening Livestock:  Incentive-based interventions for reducing the climate impact of livestock in East Africa', '2016-01-29 00:00:00', '2017-11-30 00:00:00', null, '22', null, '1', '3', '2015-11-05 09:21:24', '82', '', '1377000', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('205', 'Promoting opensource seedsystems for beans, forage legumes, millet & sorghum for climate change adaptation in Kenya, Tanzania and Uganda', '2016-01-01 00:00:00', '2019-12-31 00:00:00', null, '12', null, '1', '3', '2015-11-05 14:15:35', '243', '', '400000', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('206', '(CORPOICA) - Biological Nitrification Nnhibition (BNI) in pastures to help agriculture intensification and climate change mitigation', '2016-01-01 00:00:00', '2016-07-31 00:00:00', null, '13', null, '1', '3', '2015-11-05 21:11:54', '69', '', '101804', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('211', '(UNI-CAUCA) - Plan Nacional de Regalias', '2015-01-01 00:00:00', '2018-02-28 00:00:00', null, '13', null, '1', '3', '2015-11-10 20:58:44', '69', '', '272985', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('213', '(Cross CRP 3.7) - Quantifying enteric methane emissions from cattle grazing on improved forages', '2015-01-01 00:00:00', '2016-04-01 00:00:00', null, '13', null, '1', '3', '2015-11-11 13:52:35', '69', '', '43000', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('217', 'Fish in national development: contrasting case studies in the Indo-Pacific Region (FIS/2015/031)', '2015-10-10 00:00:00', '2017-09-30 00:00:00', null, '25', null, '1', '3', '2015-11-12 01:40:14', '153', '', '360000', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('225', 'CORMACARENA', '2015-11-13 00:00:00', '2016-12-31 00:00:00', null, '13', null, '1', '3', '2015-11-13 04:23:54', '69', '', '500000', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('226', 'CVC', '2015-11-13 00:00:00', '2016-11-30 00:00:00', null, '13', null, '1', '3', '2015-11-13 04:29:50', '69', '', '1000000', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('227', 'Pragmatic economic valuation of adaptation risk and responses across scales', '2015-01-01 00:00:00', '2016-12-31 00:00:00', null, '13', null, '1', '3', '2015-11-13 04:31:54', '66', '', '250000', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('232', 'Agricultural Risk Policy and Climate Change', '2016-01-01 00:00:00', '2016-12-31 00:00:00', null, '20', null, '1', '3', '2015-11-13 17:53:47', '52', '', '400000', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('233', 'ACIAR-SRFSI (Sustainable and resilient farming systems intensification for eastern Gangetic plains)-CIMMYT', '2015-01-01 00:00:00', '2018-06-30 00:00:00', null, '15', null, '1', '3', '2015-11-17 10:39:34', '1', '', '3456000', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('234', 'Cereal Systems Initiative for South Asia (CSISA)-CIMMYT', '2015-01-01 00:00:00', '2019-11-30 00:00:00', null, '15', null, '1', '3', '2015-11-18 09:55:53', '89', '', '800000', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('235', 'ICAR-Conservation Agriculture-CIMMYT', '2015-01-01 00:00:00', '2016-12-31 00:00:00', null, '15', null, '1', '3', '2015-11-18 09:59:31', '89', '', '300000', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('237', 'Facilitation of ICON Phase-2 Field Experiment', '2015-01-01 00:00:00', '2017-06-30 00:00:00', null, '23', null, '1', '3', '2015-11-19 08:08:06', '108', '', '61000', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('238', 'Funding position of Decision Support Expert for Climate Change at IRRI', '2015-01-01 00:00:00', '2016-12-31 00:00:00', null, '23', null, '1', '3', '2015-11-19 09:48:41', '108', '', '138000', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('239', 'Innovation platform for improving farmers’ adoption of Climate Smart Agriculture technologies: piloting in Honduras and Colombia', '2015-06-01 00:00:00', '2018-05-31 00:00:00', null, '7', null, '1', '3', '2015-11-20 02:00:56', '987', '', '200000', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('240', 'Improved Solutions for Management of Floods and Droughts in South Asia', '2015-06-01 00:00:00', '2018-07-31 00:00:00', null, '24', null, '1', '3', '2015-11-20 03:50:18', '275', '', '253813', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('241', 'Drought Monitoring in South Asia', '2015-07-01 00:00:00', '2016-04-30 00:00:00', null, '24', null, '1', '3', '2015-11-20 04:05:49', '275', '', '91000', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('242', 'Innovative Insurance Solutions for Agricultural Risk and their benefits to Climate Change Adaptation for Resilient Communities in two pilot regions', '2016-01-01 00:00:00', '2019-12-31 00:00:00', null, '24', null, '1', '3', '2015-11-20 04:21:03', '275', '', '226291', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('244', 'Site-and climate- specific agriculture recommendations across time-scales USAID', '2016-02-01 00:00:00', '2017-02-01 00:00:00', null, '13', null, '1', '3', '2015-11-26 19:02:48', '69', '', '1000000', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('245', '(USDA-BAA) Identifying Opportunities for Action on Private Sector Engagement in Climate-Smart Agriculture', '2016-02-01 00:00:00', '2018-02-28 00:00:00', null, '13', null, '1', '3', '2015-11-26 22:13:34', '69', '', '300000', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('246', 'Science support to the Sustainable Cocoa Production Platform (SCPP)', '2016-02-01 00:00:00', '2016-12-30 00:00:00', null, '13', null, '1', '3', '2015-11-26 22:30:16', '69', '', '230000', '1');
INSERT INTO `projects_bilateral_cofinancing` VALUES ('247', '(BILATERAL-USAID) CSA Integration and Analysis: Strategic support on Climate Smart Agriculture in Feed the Future', '2015-01-01 00:00:00', '2015-12-31 00:00:00', null, '2', null, '1', '3', '2016-02-23 20:19:18', '17', '', '490000', '1');

COMMIT;