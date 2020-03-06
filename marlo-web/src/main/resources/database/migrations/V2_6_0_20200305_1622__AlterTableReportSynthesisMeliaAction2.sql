ALTER TABLE `report_synthesis_melia_action_studies` DROP FOREIGN KEY `report_synthesis_melia_actions_studies_ibfk_1`;

ALTER TABLE `report_synthesis_melia_action_studies`
CHANGE COLUMN `report_synthesis_melia_id` `report_synthesis_melia_evaluation_id`  bigint(20) NULL DEFAULT NULL AFTER `id`,
DROP INDEX `report_synthesis_melia_id` ,
ADD INDEX `report_synthesis_melia_evaluation_id` (`report_synthesis_melia_evaluation_id`) USING BTREE ;

ALTER TABLE `report_synthesis_melia_action_studies` ADD CONSTRAINT `report_synthesis_melia_actions_studies_ibfk_1` FOREIGN KEY (`report_synthesis_melia_evaluation_id`) REFERENCES `report_synthesis_melia_evaluations` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;

