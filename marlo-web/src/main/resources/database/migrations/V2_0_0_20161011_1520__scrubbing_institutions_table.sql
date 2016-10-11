#Add headquarter column
ALTER TABLE `institutions` 
ADD COLUMN `headquarter` BIGINT(20) NULL DEFAULT NULL AFTER `added`,
ADD INDEX `institutions_ibfk_4_idx` (`headquarter` ASC);
ALTER TABLE `institutions` 
ADD CONSTRAINT `institutions_ibfk_4`
  FOREIGN KEY (`headquarter`)
  REFERENCES `institutions` (`id`)
  ON DELETE CASCADE
  ON UPDATE CASCADE;

#Changes on institutions
INSERT INTO `institutions` (`id`, `name`, `acronym`, `city`, `website_link`, `institution_type_id`, `country_id`) VALUES ('1266', 'King Mongkuk\'s University of Technology Thonburi', 'KMUTT', 'Bangkok ', 'http://global.kmutt.ac.th/', '1', '208');
UPDATE `institutions` SET `headquarter`='1266' WHERE `id`='1231';
UPDATE `institutions` SET `name`='Acacia Water', `city`='Gouda' WHERE `id`='1070';
UPDATE `institutions` SET `acronym`='AU' WHERE `id`='1040';
INSERT INTO `institutions` (`id`, `name`, `city`, `website_link`, `institution_type_id`, `country_id`) VALUES ('1267', 'AgRisk - Asia Risk Centre', 'London', 'http://www.agriskhub.com/', '10', '80');
UPDATE `institutions` SET `name`='AgRisk', `acronym`='', `website_link`='http://www.agriskhub.com/', `headquarter`='1267' WHERE `id`='707';
UPDATE `institutions` SET `name`='AgRisk', `acronym`='', `website_link`='http://www.agriskhub.com/', `headquarter`='1267' WHERE `id`='302';
INSERT INTO `institutions` (`id`, `name`, `acronym`, `city`, `website_link`, `institution_type_id`, `country_id`) VALUES ('1268', 'Better Rice Initiative Asia', 'BRIA', 'Bangkok', 'http://www.better-rice-initiative-asia.org/', '18', '208');
INSERT INTO `institutions` (`id`, `name`, `acronym`, `city`, `website_link`, `institution_type_id`, `country_id`, `headquarter`) VALUES ('1269', 'Better Rice Initiative Asia', 'BRIA', 'Makati', 'http://www.better-rice-initiative-asia.org/philippines/', '18', '172', '1268');
UPDATE `institutions` SET `city`='Ho Chi MInh City', `website_link`='http://www.better-rice-initiative-asia.org/vietnam/index.html', `headquarter`='1268' WHERE `id`='1263';
UPDATE `institutions` SET `city`='Iloílo', `website_link`='http://www.better-rice-initiative-asia.org/philippines/', `headquarter`='1268' WHERE `id`='1265';
UPDATE `institutions` SET `acronym`='' WHERE `id`='49';
UPDATE `institutions` SET `acronym`='', `headquarter`='49' WHERE `id`='1215';
UPDATE `institutions` SET `acronym`='', `city`='Los Banos', `website_link`='http://www.bioversityinternational.org/', `headquarter`='49' WHERE `id`='1220';
DELETE FROM `institutions` WHERE `id`='379';
UPDATE `institutions` SET `city`='Nairobi', `headquarter`='115' WHERE `id`='1042';
UPDATE `institutions` SET `headquarter`='115' WHERE `id`='746';
UPDATE `institutions` SET `headquarter`='115' WHERE `id`='775';
INSERT INTO `institutions` (`id`, `name`, `acronym`, `city`, `website_link`, `institution_type_id`, `country_id`) VALUES ('1270', 'Centre International de Recherche Agricole et du Developppement', 'CIRAD', 'Paris', 'http://www.cirad.fr/', '2', '78');
UPDATE `institutions` SET `headquarter`='1270' WHERE `id`='9';
UPDATE `institutions` SET `name`='Centre International de Recherche Agricole et du Developppement', `acronym`='CIRAD', `website_link`='http://www.cirad.fr/', `headquarter`='1270' WHERE `id`='423';
UPDATE `institutions` SET `website_link`='http://www.cirad.fr/', `headquarter`='1270' WHERE `id`='676';
UPDATE `institutions` SET `city`='Palmira' WHERE `id`='46';
UPDATE `institutions` SET `website_link`='https://ciat.cgiar.org/', `headquarter`='46' WHERE `id`='1212';
UPDATE `institutions` SET `headquarter`='46' WHERE `id`='1221';
UPDATE `institutions` SET `website_link`='http://cipotato.org/', `headquarter`='67' WHERE `id`='1222';
INSERT INTO `institutions` (`id`, `name`, `acronym`, `city`, `website_link`, `institution_type_id`, `country_id`) VALUES ('1271', 'Cooperative for Assistance and Relief Everywhere', 'CARE', 'Atlanta', 'http://www.care.org/', '9', '222');
UPDATE `institutions` SET `headquarter`='1271' WHERE `id`='1071';
UPDATE `institutions` SET `headquarter`='1271' WHERE `id`='1067';
UPDATE `institutions` SET `headquarter`='1271' WHERE `id`='1066';
UPDATE `institutions` SET `headquarter`='1271' WHERE `id`='1065';
UPDATE `institutions` SET `headquarter`='1271' WHERE `id`='706';
UPDATE `institutions` SET `website_link`='http://www.care.org/', `headquarter`='1271' WHERE `id`='627';
UPDATE `institutions` SET `website_link`='http://www.care.org/', `headquarter`='1271' WHERE `id`='86';
UPDATE `institutions` SET `website_link`='http://www.care.org/', `headquarter`='1271' WHERE `id`='430';