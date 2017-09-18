ALTER TABLE `partner_requests`
DROP FOREIGN KEY `p_request_inst_fk`,
DROP COLUMN `institution_id`,
DROP COLUMN `city`;