ALTER TABLE `partner_requests`
ADD COLUMN `crp_id` bigint(20) NULL AFTER `request_source`;

ALTER TABLE `partner_requests` ADD CONSTRAINT `p_request_crp` FOREIGN KEY (`crp_id`) REFERENCES `crps` (`id`);