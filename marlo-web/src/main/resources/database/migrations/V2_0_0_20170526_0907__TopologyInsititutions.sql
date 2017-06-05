ALTER TABLE `institution_types`
ADD COLUMN `old`  tinyint(1) NULL DEFAULT 1 AFTER `acronym`;

INSERT INTO `institution_types` (`name`, `acronym`, `old`) VALUES ('Agricultural advisory and/or extension services', '', '0');
INSERT INTO `institution_types` (`name`, `acronym`, `old`) VALUES ('Associations (other than regional organizations, extension, and farmer/community level)','',0);
INSERT INTO `institution_types` (`name`, `acronym`, `old`) VALUES ('Bilateral development agency/bank','',0);
INSERT INTO `institution_types` (`name`, `acronym`, `old`) VALUES ('CGIAR Center','',0);
INSERT INTO `institution_types` (`name`, `acronym`, `old`) VALUES ('Farmer level/community level organization','',0);
INSERT INTO `institution_types` (`name`, `acronym`, `old`) VALUES ('Foundation','',0);
INSERT INTO `institution_types` (`name`, `acronym`, `old`) VALUES ('Government','',0);
INSERT INTO `institution_types` (`name`, `acronym`, `old`) VALUES ('International/regional financial institution','',0);
INSERT INTO `institution_types` (`name`, `acronym`, `old`) VALUES ('International NGO','',0);
INSERT INTO `institution_types` (`name`, `acronym`, `old`) VALUES ('International/regional research institution','',0);
INSERT INTO `institution_types` (`name`, `acronym`, `old`) VALUES ('National/local financial institution','',0);
INSERT INTO `institution_types` (`name`, `acronym`, `old`) VALUES ('National/Local NGO','',0);
INSERT INTO `institution_types` (`name`, `acronym`, `old`) VALUES ('National/local research Institution','',0);
INSERT INTO `institution_types` (`name`, `acronym`, `old`) VALUES ('International Organization (other than financial or research)','',0);
INSERT INTO `institution_types` (`name`, `acronym`, `old`) VALUES ('Other','',0);
INSERT INTO `institution_types` (`name`, `acronym`, `old`) VALUES ('Private company (other than financial)','',0);
INSERT INTO `institution_types` (`name`, `acronym`, `old`) VALUES ('Regional organization ','',0);
INSERT INTO `institution_types` (`name`, `acronym`, `old`) VALUES ('University','',0);

ALTER TABLE `institution_types`
ADD COLUMN `description`  text NULL AFTER `old`;

