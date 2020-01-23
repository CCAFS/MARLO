ALTER TABLE `policy_milestones`
ADD COLUMN `id_phase`  bigint(20) NULL AFTER `is_active`,
ADD INDEX `id_phase` (`id_phase`) USING BTREE ;

ALTER TABLE `policy_milestones` ADD CONSTRAINT `policy_milestones_ibfk_5` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`) ON UPDATE RESTRICT;

