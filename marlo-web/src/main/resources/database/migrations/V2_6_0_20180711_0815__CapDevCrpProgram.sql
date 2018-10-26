ALTER TABLE `capacity_development` DROP FOREIGN KEY `capdev_researchProgram_fk`;

ALTER TABLE `capacity_development`
MODIFY COLUMN `research_program`  bigint(20) NULL DEFAULT NULL AFTER `research_area`;

ALTER TABLE `capacity_development` ADD CONSTRAINT `capdev_researchProgram_fk` FOREIGN KEY (`research_program`) REFERENCES `crp_programs` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION;