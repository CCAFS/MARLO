ALTER TABLE `deliverable_types`
ADD COLUMN `crp_id`  bigint(20) NULL AFTER `is_fair`;

ALTER TABLE `deliverable_types` ADD FOREIGN KEY (`crp_id`) REFERENCES `crps` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;

