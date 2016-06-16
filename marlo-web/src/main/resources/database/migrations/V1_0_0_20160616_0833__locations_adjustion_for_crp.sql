ALTER TABLE `loc_elements`
ADD COLUMN `crp_id`  bigint(20) NULL AFTER `is_site_integration`;

ALTER TABLE `loc_elements` ADD FOREIGN KEY (`crp_id`) REFERENCES `crps` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

