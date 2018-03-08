truncate table powb_collaboration;
ALTER TABLE `powb_collaboration` DROP FOREIGN KEY `powb_collaboration_ibfk_1`;

ALTER TABLE `powb_collaboration`
DROP COLUMN `powb_synthesis`,
DROP INDEX `powb_synthesis`;

ALTER TABLE `powb_collaboration` ADD CONSTRAINT `powb_collaboration_ibfk_1` FOREIGN KEY (`id`) REFERENCES `powb_synthesis` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;

