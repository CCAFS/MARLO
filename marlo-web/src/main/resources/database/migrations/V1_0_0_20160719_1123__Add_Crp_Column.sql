ALTER TABLE `liaison_institutions`
ADD COLUMN `crp_id`  bigint(20) NOT NULL DEFAULT 1 AFTER `crp_program`;

ALTER TABLE `liaison_institutions` ADD FOREIGN KEY (`crp_id`) REFERENCES `crps` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE `liaison_users`
ADD COLUMN `crp_id`  bigint(20) NOT NULL DEFAULT 1 AFTER `institution_id`;

ALTER TABLE `liaison_users` ADD FOREIGN KEY (`crp_id`) REFERENCES `crps` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

