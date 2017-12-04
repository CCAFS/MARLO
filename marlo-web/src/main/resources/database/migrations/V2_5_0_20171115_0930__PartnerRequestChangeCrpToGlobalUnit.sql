UPDATE partner_requests set global_unit_id = crp_id;

ALTER TABLE `partner_requests` DROP FOREIGN KEY `p_request_crp`;

ALTER TABLE `partner_requests`
DROP COLUMN `crp_id`;

