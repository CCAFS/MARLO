ALTER TABLE `section_statuses`
ADD COLUMN `powb_synthesis_id`  bigint(20) NULL AFTER `funding_source_id`;

ALTER TABLE `section_statuses` ADD CONSTRAINT `section_statuses_powb_synthesis_fk` FOREIGN KEY (`powb_synthesis_id`) REFERENCES `powb_synthesis` (`id`);