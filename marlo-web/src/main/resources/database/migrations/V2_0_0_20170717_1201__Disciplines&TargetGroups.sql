DROP TABLE IF EXISTS `disciplines`;

CREATE TABLE `disciplines` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(345) NULL,
  `research_program` bigint(20) NOT NULL,
  PRIMARY KEY (`id`));


DROP TABLE IF EXISTS `target_groups`;
CREATE TABLE `target_groups` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(245) NULL,
  `description` varchar(345) NULL,
  PRIMARY KEY (`id`));




INSERT INTO `disciplines` (`id`, `name`,`research_program`) VALUES ('1', 'Genetics',1);
INSERT INTO `disciplines` (`id`, `name`,`research_program`) VALUES ('2', 'Phytopathology',1);
INSERT INTO `disciplines` (`id`, `name`,`research_program`) VALUES ('3', 'Animal Nutrition',1);
INSERT INTO `disciplines` (`id`, `name`,`research_program`) VALUES ('4', 'Bioinfomatics',1);
INSERT INTO `disciplines` (`id`, `name`,`research_program`) VALUES ('5', 'Breeding',1);
INSERT INTO `disciplines` (`id`, `name`,`research_program`) VALUES ('6', 'Data Management',1);
INSERT INTO `disciplines` (`id`, `name`,`research_program`) VALUES ('7', 'Enthomology',1);
INSERT INTO `disciplines` (`id`, `name`,`research_program`) VALUES ('8', 'Field Breeding',1);
INSERT INTO `disciplines` (`id`, `name`,`research_program`) VALUES ('9', 'Genetic and Genomic',1);
INSERT INTO `disciplines` (`id`, `name`,`research_program`) VALUES ('10', 'Genetics & Genome',1);
INSERT INTO `disciplines` (`id`, `name`,`research_program`) VALUES ('11', 'Genomic Selection',1);
INSERT INTO `disciplines` (`id`, `name`,`research_program`) VALUES ('12', 'Germplasm',1);
INSERT INTO `disciplines` (`id`, `name`,`research_program`) VALUES ('13', 'Germplasm Conservation and Regeneration',1);
INSERT INTO `disciplines` (`id`, `name`,`research_program`) VALUES ('14', 'Germplasm Health',1);
INSERT INTO `disciplines` (`id`, `name`,`research_program`) VALUES ('15', 'Information Technologies',1);
INSERT INTO `disciplines` (`id`, `name`,`research_program`) VALUES ('16', 'In-vitro Culture',1);
INSERT INTO `disciplines` (`id`, `name`,`research_program`) VALUES ('17', 'Molecular Biology',1);
INSERT INTO `disciplines` (`id`, `name`,`research_program`) VALUES ('18', 'Molecular Genetic',1);
INSERT INTO `disciplines` (`id`, `name`,`research_program`) VALUES ('19', 'Molecular Markers',1);
INSERT INTO `disciplines` (`id`, `name`,`research_program`) VALUES ('20', 'Pathology',1);
INSERT INTO `disciplines` (`id`, `name`,`research_program`) VALUES ('21', 'Physiology',1);
INSERT INTO `disciplines` (`id`, `name`,`research_program`) VALUES ('22', 'PostHarvest',1);
INSERT INTO `disciplines` (`id`, `name`,`research_program`) VALUES ('23', 'Socioeconomy',1);
INSERT INTO `disciplines` (`id`, `name`,`research_program`) VALUES ('24', 'Tissue Culture',1);
INSERT INTO `disciplines` (`id`, `name`,`research_program`) VALUES ('25', 'Virology',1);





INSERT INTO `target_groups` (`id`, `name`) VALUES ('1', 'School students');
INSERT INTO `target_groups` (`id`, `name`) VALUES ('2', 'Undergraduate students');
INSERT INTO `target_groups` (`id`, `name`) VALUES ('3', 'MS students');
INSERT INTO `target_groups` (`id`, `name`) VALUES ('4', 'PHD students');
INSERT INTO `target_groups` (`id`, `name`) VALUES ('5', 'Post docs');
INSERT INTO `target_groups` (`id`, `name`, `description`) VALUES ('6', 'Extensionists', 'from the public and private sector');
INSERT INTO `target_groups` (`id`, `name`, `description`) VALUES ('7', 'Techniciens', 'such as techniciens working at national research institutes');
INSERT INTO `target_groups` (`id`, `name`, `description`) VALUES ('8', 'Scientists', 'includes university professors ');
INSERT INTO `target_groups` (`id`, `name`, `description`) VALUES ('9', 'Farmers', 'includes farmer associations');
INSERT INTO `target_groups` (`id`, `name`) VALUES ('10', 'Private sector actors');
INSERT INTO `target_groups` (`id`, `name`) VALUES ('11', 'Decision makers from governance agencies ');





















 







