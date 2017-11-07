CREATE TABLE `repository_channel` (
`id` bigint(20) NOT NULL AUTO_INCREMENT,
`short_name` text NOT NULL ,
`name` text NOT NULL ,
`url_example` text NOT NULL ,
`is_active` tinyint(1) NOT NULL DEFAULT '1',
PRIMARY KEY (`id`));

INSERT INTO `repository_channel` (`short_name`, `name`, `url_example`, `is_active`) VALUES 
('cgspace', 'CGSpace', 'https://cgspace.cgiar.org/handle/<b>10568/79435', '1');
INSERT INTO `repository_channel` (`short_name`, `name`, `url_example`, `is_active`) VALUES 
('dataverse', 'Dataverse (Harvard)', 'https://dataverse.harvard.edu/dataset.xhtml?persistentId=<b>doi:10.7910/DVN/0ZEXKC</b>', '1');
INSERT INTO `repository_channel` (`short_name`, `name`, `url_example`, `is_active`) VALUES 
('ifpri', 'IFPRI E-BRARY', 'http://ebrary.ifpri.org/cdm/singleitem/<b>collection/p15738coll5/id/5388</b>/rec/1', '1');
INSERT INTO `repository_channel` (`short_name`, `name`, `url_example`, `is_active`) VALUES 
('ilri', 'ILRI Datasets', 'http://data.ilri.org/portal/dataset/<b>ccafsnyando</b>', '1');
INSERT INTO `repository_channel` (`short_name`, `name`, `url_example`, `is_active`) VALUES 
('cimmyt', 'CIMMYT Dataverse', 'http://data.cimmyt.org/dvn/dv/cimmytswdvn/faces/study/StudyPage.xhtml?globalId=<b>hdl:11529/10820</b>', '1');
INSERT INTO `repository_channel` (`short_name`, `name`, `url_example`, `is_active`) VALUES 
('cimmytDspace', 'CIMMYT DSpace', 'http://repository.cimmyt.org/xmlui/handle/<b>10883/19062</b>', '1');
INSERT INTO `repository_channel` (`short_name`, `name`, `url_example`, `is_active`) VALUES 
('other', 'Other', '', '1');