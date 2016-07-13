ALTER TABLE `liaison_institutions`
DROP COLUMN `crp_program`,
ADD COLUMN `crp_program`  bigint(20) NULL DEFAULT NULL AFTER `acronym`;

ALTER TABLE `liaison_institutions` ADD FOREIGN KEY (`crp_program`) REFERENCES `crp_programs` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

