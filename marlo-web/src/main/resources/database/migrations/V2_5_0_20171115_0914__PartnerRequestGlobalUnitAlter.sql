ALTER TABLE `partner_requests`
ADD COLUMN `global_unit_id`  bigint(20) NULL;

ALTER TABLE `partner_requests` ADD CONSTRAINT `partner_requests_global_unit_fk` FOREIGN KEY (`global_unit_id`) REFERENCES `global_units` (`id`);

