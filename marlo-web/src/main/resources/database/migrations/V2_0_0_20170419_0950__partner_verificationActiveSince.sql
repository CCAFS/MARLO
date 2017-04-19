ALTER TABLE `partner_requests`
ADD COLUMN `active_since`  timestamp NULL AFTER `is_active`;