/* Add AR2018 funding use fields/tables */
/* interesting_points */
ALTER TABLE `report_synthesis_funding_use_summaries`
ADD COLUMN `interesting_points`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT 'AR2018 field' AFTER `id`;


/* expenditure categories table */
DROP TABLE IF EXISTS `report_synthesis_expenditure_categories`;
CREATE TABLE `report_synthesis_expenditure_categories` (
`id`  bigint(20) NOT NULL AUTO_INCREMENT,
`name`  text NOT NULL ,
`description`  text NULL,
`is_active`  tinyint(1) NOT NULL ,
`active_since`  timestamp NULL ,
`created_by`  bigint(20) NULL ,
`modified_by`  bigint(20) NULL ,
`modification_justification`  text NULL ,
PRIMARY KEY (`id`),
CONSTRAINT `fk_report_synthesis_expenditure_categories_created_by` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`),
CONSTRAINT `fk_report_synthesis_expenditure_categories_modified_by` FOREIGN KEY (`modified_by`) REFERENCES `users` (`id`)
);

/* expenditure categories table records */
INSERT INTO `report_synthesis_expenditure_categories` ( `name`, `description`, `is_active`, `active_since`, `created_by`, `modified_by`, `modification_justification`) VALUES  ('Policy','Sole or partial funding source for dissemination of findings, learning from evidence etc.; For example, policy workshops, contracts with partners working on policy etc','1',now(),'3','3',' ');
INSERT INTO `report_synthesis_expenditure_categories` ( `name`, `description`, `is_active`, `active_since`, `created_by`, `modified_by`, `modification_justification`) VALUES  ('Partnerships','Start-up and maintenance of partnerships','1',now(),'3','3',' ');
INSERT INTO `report_synthesis_expenditure_categories` ( `name`, `description`, `is_active`, `active_since`, `created_by`, `modified_by`, `modification_justification`) VALUES  ('Capacity development','Any activities reported under capdev indicator','1',now(),'3','3',' ');
INSERT INTO `report_synthesis_expenditure_categories` ( `name`, `description`, `is_active`, `active_since`, `created_by`, `modified_by`, `modification_justification`) VALUES  ('Other cross-cutting issues','Gender, youth, climate change; e.g. funding research projects tagged as ‘principal’ for one of these; funding cross-cutting work by the Program Management Unit; funding specific gender/youth/Climate Action ‘add ons’ to other projects. In every case, it should be obvious from the title of the activity what the cross-cutting issue is','1',now(),'3','3',' ');
INSERT INTO `report_synthesis_expenditure_categories` ( `name`, `description`, `is_active`, `active_since`, `created_by`, `modified_by`, `modification_justification`) VALUES  ('Other Monitoring, learning, evaluation and impact assessment (MELIA)','Activities covered under the MELIA section of this reporting template','1',now(),'3','3',' ');
INSERT INTO `report_synthesis_expenditure_categories` ( `name`, `description`, `is_active`, `active_since`, `created_by`, `modified_by`, `modification_justification`) VALUES  ('Contingency/ emergency','e.g. immediate unplanned response to a new virulent disease, or moving germplasm collections as a result of conflict','1',now(),'3','3',' ');
INSERT INTO `report_synthesis_expenditure_categories` ( `name`, `description`, `is_active`, `active_since`, `created_by`, `modified_by`, `modification_justification`) VALUES  ('Pre-start up','Conceptualization, design, ex-ante analysis before research start-up; For example: foresight, ex-ante studies, building theories of change, proof of concept studies for novel areas of work. (However, start-up meetings with partners should normally be tagged as ‘partnerships’.)','1',now(),'3','3',' ');
INSERT INTO `report_synthesis_expenditure_categories` ( `name`, `description`, `is_active`, `active_since`, `created_by`, `modified_by`, `modification_justification`) VALUES  ('Research','Sole or partial funding source for a research line or significant research activity','1',now(),'3','3',' ');
INSERT INTO `report_synthesis_expenditure_categories` ( `name`, `description`, `is_active`, `active_since`, `created_by`, `modified_by`, `modification_justification`) VALUES  ('Delivery','Funding for any activities connected with scale-up and delivery','1',now(),'3','3',' ');
INSERT INTO `report_synthesis_expenditure_categories` ( `name`, `description`, `is_active`, `active_since`, `created_by`, `modified_by`, `modification_justification`) VALUES  ('Other','','1',now(),'3','3',' ');


/* update expenditure areas table  */
ALTER TABLE `report_synthesis_funding_use_expenditury_areas`
ADD COLUMN `example_expenditure`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT 'AR2018 field' AFTER `report_synthesis_funding_use_summary_id`,
ADD COLUMN `expenditure_category_id`  bigint(20) NULL DEFAULT NULL COMMENT 'AR2018 field' AFTER `example_expenditure`,
ADD COLUMN `other_category`  text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT 'AR2018 field' AFTER `expenditure_category_id`,
ADD INDEX `expenditure_category_id` (`expenditure_category_id`) USING BTREE ;

ALTER TABLE `report_synthesis_funding_use_expenditury_areas` ADD CONSTRAINT `report_synthesis_funding_use_expenditury_areas_ibfk_5` FOREIGN KEY (`expenditure_category_id`) REFERENCES `report_synthesis_expenditure_categories` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;