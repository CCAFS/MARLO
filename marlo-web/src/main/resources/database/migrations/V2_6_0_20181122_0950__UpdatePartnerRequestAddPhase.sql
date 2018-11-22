ALTER TABLE `partner_requests`
ADD COLUMN `id_phase`  bigint(20) NULL DEFAULT NULL AFTER `global_unit_id`,
ADD INDEX `id_phase` (`id_phase`) USING BTREE ;

ALTER TABLE `partner_requests` ADD CONSTRAINT `p_request_phase_id` FOREIGN KEY (`id_phase`) REFERENCES `phases` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;