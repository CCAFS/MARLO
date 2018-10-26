INSERT INTO `deliverable_rules` (`id`, `name`, `description`) VALUES ('1', 'publicationMetadata', 'Show metadata of publication');
INSERT INTO `deliverable_rules` (`id`, `name`, `description`) VALUES ('2', 'computerLicense', 'Ask for MIT and GNU licenses');
INSERT INTO `deliverable_rules` (`id`, `name`, `description`) VALUES ('3', 'dataLicense', 'Ask for CC and Open Data licenses');
INSERT INTO `deliverable_rules` (`id`, `name`, `description`) VALUES ('4', 'complianceCheck', 'Show complaince check section');
INSERT INTO `deliverable_rules` (`id`, `name`, `description`) VALUES ('5', 'journalArticles', 'Ask journal articles fields');

/* Articles and Books  */
INSERT INTO `deliverable_type_rules` (`deliverable_type`, `deliverable_rule`) 
VALUES ( (SELECT id FROM deliverable_types dt WHERE dt.`id` = 49)
, (SELECT id FROM deliverable_rules dr WHERE dr.`name` = 'publicationMetadata'));

/* Data portal/Tool/Model code/Computer software  */
INSERT INTO `deliverable_type_rules` (`deliverable_type`, `deliverable_rule`) 
VALUES ( (SELECT id FROM deliverable_types dt WHERE dt.`id` = 52)
, (SELECT id FROM deliverable_rules dr WHERE dr.`name` = 'computerLicense'));

/* Data portal/Tool/Model code/Computer software  */
INSERT INTO `deliverable_type_rules` (`deliverable_type`, `deliverable_rule`) 
VALUES ( (SELECT id FROM deliverable_types dt WHERE dt.`id` = 52)
, (SELECT id FROM deliverable_rules dr WHERE dr.`name` = 'dataLicense'));

/* Maps/Geospatial data  */
INSERT INTO `deliverable_type_rules` (`deliverable_type`, `deliverable_rule`) 
VALUES ( (SELECT id FROM deliverable_types dt WHERE dt.`id` = 74)
, (SELECT id FROM deliverable_rules dr WHERE dr.`name` = 'dataLicense'));

/* Database/Dataset/Data documentation  */
INSERT INTO `deliverable_type_rules` (`deliverable_type`, `deliverable_rule`) 
VALUES ( (SELECT id FROM deliverable_types dt WHERE dt.`id` = 51)
, (SELECT id FROM deliverable_rules dr WHERE dr.`name` = 'complianceCheck'));

/* Maps/Geospatial data  */
INSERT INTO `deliverable_type_rules` (`deliverable_type`, `deliverable_rule`) 
VALUES ( (SELECT id FROM deliverable_types dt WHERE dt.`id` = 74)
, (SELECT id FROM deliverable_rules dr WHERE dr.`name` = 'complianceCheck'));

/* Journal Article (peer reviewed)  */
INSERT INTO `deliverable_type_rules` (`deliverable_type`, `deliverable_rule`) 
VALUES ( (SELECT id FROM deliverable_types dt WHERE dt.`id` = 63)
, (SELECT id FROM deliverable_rules dr WHERE dr.`name` = 'journalArticles'));