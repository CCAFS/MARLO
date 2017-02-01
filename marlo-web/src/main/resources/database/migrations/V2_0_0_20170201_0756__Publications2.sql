ALTER TABLE `deliverables`
ADD COLUMN `crp_id`  bigint(20) NULL AFTER `is_publication`;

ALTER TABLE `deliverables` ADD FOREIGN KEY (`crp_id`) REFERENCES `crps` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;

