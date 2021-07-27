ALTER TABLE project_expected_study_lever_outcomes DROP FOREIGN KEY `expected_study_lever_outcomes_ibfk_2`;
truncate table project_expected_study_lever_outcomes;
drop table lever_outcomes;
CREATE TABLE `alliance_levers` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` text DEFAULT NULL,
  `indicator` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE TABLE `alliance_lever_outcomes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` text DEFAULT NULL,
  `parent_id` bigint DEFAULT NULL,
  `indicator` text,
  PRIMARY KEY (`id`),
  KEY `lever_outcomes_parent_id` (`parent_id`) USING BTREE,
  CONSTRAINT `lever_outcomes_parent_fk` FOREIGN KEY (`parent_id`) REFERENCES `alliance_levers` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
CREATE TABLE `project_expected_study_levers` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `expected_id` bigint DEFAULT NULL,
  `lever_id` bigint DEFAULT NULL,
  `id_phase` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `expected_id` (`expected_id`) USING BTREE,
  KEY `lever_id` (`lever_id`) USING BTREE,
  KEY `id_phase` (`id_phase`) USING BTREE,
  CONSTRAINT `expected_study_lever_ibfk_1` FOREIGN KEY (`expected_id`) REFERENCES `project_expected_studies` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `expected_study_lever_ibfk_2` FOREIGN KEY (`lever_id`) REFERENCES `alliance_levers` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `expected_study_lever_ibfk_3` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
ALTER TABLE project_expected_study_lever_outcomes ADD CONSTRAINT `expected_study_lever_outcomes_ibfk_2` FOREIGN KEY (`lever_outcome_id`) REFERENCES `alliance_lever_outcomes` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;
INSERT INTO `alliance_levers` (`id`, `name`, `indicator`) VALUES (1, 'Food environment and consumer behaviour', '1');
INSERT INTO `alliance_levers` (`id`, `name`, `indicator`) VALUES (2, 'Multifunctional Landscapes', '2');
INSERT INTO `alliance_levers` (`id`, `name`, `indicator`) VALUES (3, 'Climate Action', '3');
INSERT INTO `alliance_levers` (`id`, `name`, `indicator`) VALUES (4, 'Biodiversity', '4');
INSERT INTO `alliance_levers` (`id`, `name`, `indicator`) VALUES (5, 'Digital Inclusion', '5');
INSERT INTO `alliance_levers` (`id`, `name`, `indicator`) VALUES (6, 'Crops for Nutrition and Health', '6');
INSERT INTO `alliance_levers` (`id`, `name`, `indicator`) VALUES (7, 'CGIAR Initiatives', '7');
INSERT INTO `alliance_lever_outcomes` (`id`, `name`, `parent_id`, `indicator`) VALUES (1, 'National and subnational authorities in priority countries adopt policies and build capacity to improve diet quality [including safety and diversity] of consumers. [add healthier diets?]', 1, '1.1');
INSERT INTO `alliance_lever_outcomes` (`id`, `name`, `parent_id`, `indicator`) VALUES (2, 'Food environments offer greater opportunities for inclusion for women and youth in local, domestic and transnational markets [inclusion is also for access and affordability for the consumer]', 1, '1.2');
INSERT INTO `alliance_lever_outcomes` (`id`, `name`, `parent_id`, `indicator`) VALUES (3, 'National and international public sector organizations, as well as civil society and private sector organizations at local, national and international level promote sustainable healthy diets', 1, '1.3');
INSERT INTO `alliance_lever_outcomes` (`id`, `name`, `parent_id`, `indicator`) VALUES (4, 'Consumers in the global south are mobilized to support food environments that inclusively offer healthy, diverse and sustainable food choices to all consumers', 1, '1.4');
INSERT INTO `alliance_lever_outcomes` (`id`, `name`, `parent_id`, `indicator`) VALUES (5, 'National and subnational authorities in priority countries implement policies and incentives that promote evidence-based agro-environmental solutions that enhance ecosystem services, healthy foods, and equitable livelihoods under a changing climate', 2, '2.1');
INSERT INTO `alliance_lever_outcomes` (`id`, `name`, `parent_id`, `indicator`) VALUES (6, 'Land managers and development actors participate in the co-design and deployment of land uses that are more biodiverse, resilient to climate change and equitable, and reduce the environmental impact of agricultural systems in priority countries', 2, '2.2');
INSERT INTO `alliance_lever_outcomes` (`id`, `name`, `parent_id`, `indicator`) VALUES (7, 'Public and private value-chain actors and investors participate in the negotiation and implementation of sustainable business models that lead to improved environmental and socio-economic outcomes in farms and landscapes', 2, '2.3');
INSERT INTO `alliance_lever_outcomes` (`id`, `name`, `parent_id`, `indicator`) VALUES (8, 'Producers sustainably manage productive farms and landscapes and their efforts towards sustainable production are being rewarded through environmental, social and governance? incentives, more favorable prices, government aids, or other type of economic and financial mechanisms -- add healthy foods here', 2, '2.4');
INSERT INTO `alliance_lever_outcomes` (`id`, `name`, `parent_id`, `indicator`) VALUES (9, 'Development partners use tailored climate services in priority countries to help farmers and their institutions reduce the impact of climate risks', 3, '3.1');
INSERT INTO `alliance_lever_outcomes` (`id`, `name`, `parent_id`, `indicator`) VALUES (10, 'Development agencies make smarter investments that deliver climate adaptation and mitigation based on agricultural and climate risks profiled', 3, '3.2');
INSERT INTO `alliance_lever_outcomes` (`id`, `name`, `parent_id`, `indicator`) VALUES (11, 'Newly established innovative finance partnerships support climate adaptation and mitigation efforts across a range of geographies', 3, '3.3');
INSERT INTO `alliance_lever_outcomes` (`id`, `name`, `parent_id`, `indicator`) VALUES (12, 'Farmers and their institutions co-develop adapted and low-emissions agricultural practices and technologies, improving livelihoods, environmental sustainability, and food and nutrition security in priority countries', 3, '3.4');
INSERT INTO `alliance_lever_outcomes` (`id`, `name`, `parent_id`, `indicator`) VALUES (13, 'National programs and communities in priority countries characterize and use diverse genebank and local material and crop wild relatives to address resilience, climate change, and nutrition through integrated selection and breeding approaches', 4, '4.1');
INSERT INTO `alliance_lever_outcomes` (`id`, `name`, `parent_id`, `indicator`) VALUES (14, 'Inclusive formal, intermediate, and informal seed systems deliver forages, trees, and crops adapted to local conditions to improve food security and sustainability of production systems', 4, '4.2');
INSERT INTO `alliance_lever_outcomes` (`id`, `name`, `parent_id`, `indicator`) VALUES (15, 'National governments adopt policies and practice recommendations for genetic resource management', 4, '4.3');
INSERT INTO `alliance_lever_outcomes` (`id`, `name`, `parent_id`, `indicator`) VALUES (16, 'Value-chain stakeholders integrate a greater diversity of crops for greater resilience and healthier diets', 4, '4.4');
INSERT INTO `alliance_lever_outcomes` (`id`, `name`, `parent_id`, `indicator`) VALUES (17, 'Public and private organizations support inclusive digital services for producers and rural entrepreneurs to improve employment of women and youth and increase agricultural productivity and quality of life', 5, '5.1');
INSERT INTO `alliance_lever_outcomes` (`id`, `name`, `parent_id`, `indicator`) VALUES (18, 'Agricultural innovation facilities make effective and responsive use of digital technology for two-way interaction to address the needs of users in R&D targeting and to ensure efficient delivery of innovations', 5, '5.2');
INSERT INTO `alliance_lever_outcomes` (`id`, `name`, `parent_id`, `indicator`) VALUES (19, 'Food sellers, buyers, processors, traders and retailers, and food system governance bodies make effective use of digital technology to make better use of agrobiodiversity, make food systems more inclusive, and monitor and control environmental and social sustainability impacts', 5, '5.3');
INSERT INTO `alliance_lever_outcomes` (`id`, `name`, `parent_id`, `indicator`) VALUES (20, 'National institutions accelerate delivery of new varieties with higher nutritional value in target countries, by integrating genotyping, phenotyping, and participatory approaches', 6, '6.1');
INSERT INTO `alliance_lever_outcomes` (`id`, `name`, `parent_id`, `indicator`) VALUES (21, 'Partner countries improve capacity in the use of nutrition-sensitive lenses and appropriate gender-responsive participatory and genetic methodologies in the selection and breeding of crops', 6, '6.2');