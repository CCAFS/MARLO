ALTER TABLE `submissions` DROP FOREIGN KEY `submissions_ibfk_5`;

ALTER TABLE `submissions`
DROP COLUMN `powb_synthesis_id`,
ADD COLUMN `global_unit_id`  bigint(20) NULL AFTER `unsubmit_justification`;

ALTER TABLE `submissions` ADD CONSTRAINT `submissions_ibfk_5` FOREIGN KEY (`global_unit_id`) REFERENCES `global_units` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;