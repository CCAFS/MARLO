--
ALTER TABLE `powb_toc` DROP FOREIGN KEY `powb_toc_synthesis_id_fk`;

ALTER TABLE `powb_toc`
DROP COLUMN `powb_synthesis_id`,
MODIFY COLUMN `id`  bigint(20) NOT NULL FIRST ;

ALTER TABLE `powb_toc` ADD CONSTRAINT `powb_synthesis_id_fk` FOREIGN KEY (`id`) REFERENCES `powb_synthesis` (`id`);
--
ALTER TABLE `powb_flagship_plans` DROP FOREIGN KEY `powb_flagship_plans_fk_powb_synthesis_id`;

ALTER TABLE `powb_flagship_plans`
DROP COLUMN `powb_synthesis_id`,
MODIFY COLUMN `id`  bigint(20) NOT NULL FIRST ;

ALTER TABLE `powb_flagship_plans` ADD CONSTRAINT `powb_flagship_plans_syntehsis_fk` FOREIGN KEY (`id`) REFERENCES `powb_synthesis` (`id`);