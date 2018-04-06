INSERT INTO `deliverable_rules` (`id`, `name`, `description`) VALUES ('1', 'publicationMetadata', 'Show metadata of publication');
INSERT INTO `deliverable_rules` (`id`, `name`, `description`) VALUES ('2', 'computerLicense', 'Ask for MIT and GNU licenses');
INSERT INTO `deliverable_rules` (`id`, `name`, `description`) VALUES ('3', 'dataLicense', 'Ask for CC and Open Data licenses');
INSERT INTO `deliverable_rules` (`id`, `name`, `description`) VALUES ('4', 'complianceCheck', 'Show complaince check section');
INSERT INTO `deliverable_rules` (`id`, `name`, `description`) VALUES ('5', 'journalArticles', 'Ask journal articles fields');

INSERT INTO `deliverable_type_rules` (`deliverable_type`, `deliverable_rule`) 
VALUES ( (SELECT id FROM deliverable_types dt WHERE dt.`name` = 'Reports and other publications ')
, (SELECT id FROM deliverable_rules dr WHERE dr.`name` = 'publicationMetadata'));

INSERT INTO `deliverable_type_rules` (`deliverable_type`, `deliverable_rule`) 
VALUES ( (SELECT id FROM deliverable_types dt WHERE dt.`name` = 'Data portal/Tool/Model code/Computer software')
, (SELECT id FROM deliverable_rules dr WHERE dr.`name` = 'computerLicense'));

INSERT INTO `deliverable_type_rules` (`deliverable_type`, `deliverable_rule`) 
VALUES ( (SELECT id FROM deliverable_types dt WHERE dt.`name` = 'Data portal/Tool/Model code/Computer software')
, (SELECT id FROM deliverable_rules dr WHERE dr.`name` = 'dataLicense'));

INSERT INTO `deliverable_type_rules` (`deliverable_type`, `deliverable_rule`) 
VALUES ( (SELECT id FROM deliverable_types dt WHERE dt.`name` = 'Maps/Geospatial data')
, (SELECT id FROM deliverable_rules dr WHERE dr.`name` = 'dataLicense'));

INSERT INTO `deliverable_type_rules` (`deliverable_type`, `deliverable_rule`) 
VALUES ( (SELECT id FROM deliverable_types dt WHERE dt.`name` = 'Database/Dataset/Data documentation')
, (SELECT id FROM deliverable_rules dr WHERE dr.`name` = 'complianceCheck'));

INSERT INTO `deliverable_type_rules` (`deliverable_type`, `deliverable_rule`) 
VALUES ( (SELECT id FROM deliverable_types dt WHERE dt.`name` = 'Maps/Geospatial data')
, (SELECT id FROM deliverable_rules dr WHERE dr.`name` = 'complianceCheck'));

INSERT INTO `deliverable_type_rules` (`deliverable_type`, `deliverable_rule`) 
VALUES ( (SELECT id FROM deliverable_types dt WHERE dt.`name` = 'Journal Article (peer reviewed)')
, (SELECT id FROM deliverable_rules dr WHERE dr.`name` = 'journalArticles'));