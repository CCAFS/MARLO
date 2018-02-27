ALTER TABLE `submissions` DROP FOREIGN KEY `submissions_ibfk_5`;

ALTER TABLE `submissions`
DROP COLUMN `global_unit_id`,
ADD COLUMN `powb_synthesis_id`  bigint(20) NULL AFTER `project_id`;

