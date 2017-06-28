ALTER TABLE `reserach_outcomes` ADD CONSTRAINT `fk_outcomes_target_unit` FOREIGN KEY (`target_unit_id`) REFERENCES `target_units` (`id`);

ALTER TABLE `research_milestones`
CHANGE COLUMN `milestone` `value`  decimal(10,2) NULL DEFAULT NULL AFTER `target_year`;